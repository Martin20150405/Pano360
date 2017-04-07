precision mediump float;
uniform sampler2D sTexture;
varying vec2 vTextureCoord;
vec4 calNewSaturation(vec4 color,float saturation) {
	float gray = dot(color.rgb, vec3(0.299,0.587,0.114));
	return vec4(gray + (saturation / 100.0 + 1.0) * (color.r - gray), gray + (saturation / 100.0 + 1.0) * (color.g - gray), gray + (saturation / 100.0 + 1.0) * (color.b - gray), color.a);
}
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
	float alpha = 0.26;
	vec4 cpColor = calCrossProcess(color);
	color.r = cpColor.r * alpha + color.r * (1.0 - alpha);
	color.g = cpColor.g * alpha + color.g * (1.0 - alpha);
	color.b = cpColor.b * alpha + color.b * (1.0 - alpha);
	color = calNewSaturation(color, -20.0);
	color = calColorBalance(color, 10.0, 20.0, 30.0);
	gl_FragColor = color;
}
