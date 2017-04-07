precision mediump float;
uniform sampler2D sTexture;
varying vec2 vTextureCoord;
float getHByHSV(vec4 color) {
	float maxValue = max(color.r, max(color.g, color.b));
	float minValue = min(color.r, min(color.g, color.b));
	float diff = maxValue - minValue;
	if (maxValue  == minValue) {		return -100.0;
	} else if (maxValue == color.r && color.g >= color.b) {
		return 60.0 * (color.g - color.b) / diff;
	} else if (maxValue == color.r && color.g < color.b) {
		return 60.0 * (color.g - color.b) / diff + 360.0;
	} else if (maxValue == color.g) {
		return 60.0 * (color.b - color.r) / diff + 120.0;
	} else if (maxValue == color.b) {
		return 60.0 * (color.r - color.b) / diff + 240.0;
	}
	return 0.0;
}
void main() {
	vec4 color = texture2D(sTexture, vTextureCoord);
	float h = getHByHSV(color) ;
	if (h > 60.0 && h < 300.0) {
		float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));
		color.r = gray;
		color.g = gray;
		color.b = gray;
	}
	gl_FragColor = color;
}
