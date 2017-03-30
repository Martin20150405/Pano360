precision mediump float;
uniform sampler2D sTexture;
uniform sampler2D sTexture2;
varying vec2 vTextureCoord;
vec4 calNewSaturation(vec4 color,float saturation) {
	float gray = dot(color.rgb, vec3(0.299,0.587,0.114));
	return vec4(gray + (saturation / 100.0 + 1.0) * (color.r - gray), gray + (saturation / 100.0 + 1.0) * (color.g - gray), gray + (saturation / 100.0 + 1.0) * (color.b - gray), color.a);
}
void main() {
	vec4 color = texture2D(sTexture, vTextureCoord);
	color = calNewSaturation(color, -50.0);
    color.r = texture2D(sTexture2, vec2(color.r, 1.0)).a;
    color.g = texture2D(sTexture2, vec2(color.g, 1.0)).a;
    color.b = texture2D(sTexture2, vec2(color.b, 1.0)).a;
	gl_FragColor = color;
}

