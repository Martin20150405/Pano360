precision mediump float;
uniform sampler2D sTexture;
varying vec2 vTextureCoord;
vec4 calVignette2(vec4 color, vec2 coord, float strength) {
	float distance = (coord.x - 0.5) * (coord.x - 0.5) + (coord.y - 0.5) * (coord.y - 0.5);
	float scale = distance / 0.5 * strength;
	color.r =  color.r - scale;
	color.g = color.g - scale;
	color.b = color.b - scale;
	return color;
}
vec4 calNewSaturation(vec4 color,float saturation) {
	float gray = dot(color.rgb, vec3(0.299,0.587,0.114));
	return vec4(gray + (saturation / 100.0 + 1.0) * (color.r - gray), gray + (saturation / 100.0 + 1.0) * (color.g - gray), gray + (saturation / 100.0 + 1.0) * (color.b - gray), color.a);
}
void main() {
	vec4 color = texture2D(sTexture, vTextureCoord);
	color = calVignette2(color, vTextureCoord,1.0);
	color = calNewSaturation(color, 57.0);
	gl_FragColor = color;
}

