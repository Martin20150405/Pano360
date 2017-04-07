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
	float alpha = 0.7;
	vec4 cpColor = calCrossProcess(color);
	color.r = cpColor.r * alpha + color.r * (1.0 - alpha);
	color.g = cpColor.g * alpha + color.g * (1.0 - alpha);
	color.b = cpColor.b * alpha + color.b * (1.0 - alpha);
    color.r = texture2D(sTexture2, vec2(color.r, 1.0)).a;
    color.g = texture2D(sTexture2, vec2(color.g, 1.0)).a;
    color.b = texture2D(sTexture2, vec2(color.b, 1.0)).a;
	gl_FragColor = color;
}

