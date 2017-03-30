precision mediump float;
uniform sampler2D sTexture;
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
vec4 calColorBalance(vec4 color,float rOffset,float gOffset,float bOffset) {
	return vec4(color.r + rOffset / 255.0, color.g + gOffset / 255.0, color.b + bOffset / 255.0,  color.a);
}
void main() {
	vec4 color = texture2D(sTexture, vTextureCoord);
	float alpha = 0.7;
	vec4 cpColor = calCrossProcess(color);
	color.r = cpColor.r * alpha + color.r * (1.0 - alpha);
	color.g = cpColor.g * alpha + color.g * (1.0 - alpha);
	color.b = cpColor.b * alpha + color.b * (1.0 - alpha);
	color = calColorBalance(color, -7.0, 14.0, 36.0);
	gl_FragColor = color;
}

