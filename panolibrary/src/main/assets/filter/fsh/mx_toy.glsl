precision mediump float;
uniform sampler2D sTexture;
uniform sampler2D sTexture2;
varying vec2 vTextureCoord;
vec4 calNewSaturation(vec4 color,float saturation) {
	float gray = dot(color.rgb, vec3(0.299,0.587,0.114));
	return vec4(gray + (saturation / 100.0 + 1.0) * (color.r - gray), gray + (saturation / 100.0 + 1.0) * (color.g - gray), gray + (saturation / 100.0 + 1.0) * (color.b - gray), color.a);
}
vec4 calColorBalance(vec4 color,float rOffset,float gOffset,float bOffset) {
	return vec4(color.r + rOffset / 255.0, color.g + gOffset / 255.0, color.b + bOffset / 255.0,  color.a);
}
void main() {
	vec4 color = texture2D(sTexture, vTextureCoord);
    color.r = texture2D(sTexture2, vec2(color.r, 1.0)).a;
    color.g = texture2D(sTexture2, vec2(color.g, 1.0)).a;
    color.b = texture2D(sTexture2, vec2(color.b, 1.0)).a;
	color = calNewSaturation(color, 46.0);
	color = calColorBalance(color, -1.0, 10.0, 20.0);
	gl_FragColor = color;
}

