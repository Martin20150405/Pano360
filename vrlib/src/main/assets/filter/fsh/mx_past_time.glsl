precision mediump float;
uniform sampler2D sTexture;
uniform sampler2D sTexture2;
varying vec2 vTextureCoord;
vec4 calCrossProcess(vec4 color) {
	vec3 ncolor = vec3(0.0, 0.0, 0.0);
	float value;
	if (color.r < 0.5) {
		value = color.r;
	} else {
		value = 1.0 - color.r;
	}
	float red = 4.0 * value * value * value;
	if (color.r < 0.5) {
		ncolor.r = red;
	} else {
		ncolor.r = 1.0 - red;
	}
	if (color.g < 0.5) {
		value = color.g;
	} else {
		value = 1.0 - color.g;
	}
	float green = 2.0 * value * value;
	if (color.g < 0.5) {
		ncolor.g = green;
	} else {
		ncolor.g = 1.0 - green;
	}
	ncolor.b = color.b * 0.5 + 0.25;
	return vec4(ncolor.rgb, color.a);
}
void main() {
	vec4 color = texture2D(sTexture, vTextureCoord);
	float gray = dot(color.rgb, vec3(0.229, 0.587, 0.114));
    gray = texture2D(sTexture2, vec2(gray, 1.0)).a;
	float alpha = 0.5;
	color.r = gray * (1.0 - alpha) + gray * 0.94 * alpha;
	color.g = gray * (1.0 - alpha) + gray * 0.71 * alpha;
	color.b = gray * (1.0 - alpha) + gray * 0.56 * alpha;
	gl_FragColor = color;
}

