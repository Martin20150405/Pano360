precision mediump float;
uniform sampler2D sTexture;
varying vec2 vTextureCoord;
uniform float rOffset;
uniform float gOffset;
uniform float bOffset;
vec4 calBrightnessContract(vec4 color,float brightness, float contrast,float threshold) {
	float cv = contrast <= -255.0 ? -1.0 : contrast / 255.0;
	if (contrast > 0.0 && contrast < 255.0) {
		cv = 1.0 / (1.0 - cv) - 1.0;
	}
	float r  = color.r + brightness / 255.0;
	float g = color.g + brightness / 255.0;
	float b = color.b + brightness / 255.0;
	if (contrast >= 255.0) {
		r = r >= threshold / 255.0 ? 1.0 : 0.0;
		g = g >= threshold / 255.0 ? 1.0 : 0.0;
 		b = b >= threshold / 255.0 ? 1.0 : 0.0;
	} else {
		r =  r + (r - threshold / 255.0) * cv;
		g = g + (g - threshold / 255.0) * cv;
		b = b + (b - threshold / 255.0) * cv;
	}
	color.r = r;
	color.g = g;
	color.b = b;
	return color;
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
vec4 calNewSaturation(vec4 color,float saturation) {
	float gray = dot(color.rgb, vec3(0.299,0.587,0.114));
	return vec4(gray + (saturation / 100.0 + 1.0) * (color.r - gray), gray + (saturation / 100.0 + 1.0) * (color.g - gray), gray + (saturation / 100.0 + 1.0) * (color.b - gray), color.a);
}
vec4 multiply(vec4 color1,vec4 color2,int option) {
	if (option == 0) {
		return vec4(color1.r * color2.r,color1.g * color2.g,color1.b * color2.b,color1.a);
	} else{
		vec4 color = vec4(color1.r * color2.r,color1.g * color2.g,color1.b * color2.b,color2.a);
		return vec4(color.r * color.a + color1.r * (1.0 - color.a),color.g * color.a + color1.g * (1.0 - color.a),color.b * color.a + color1.b * (1.0 - color.a),color1.a);
	}
}

void main() {
	vec4 color = texture2D(sTexture,vTextureCoord);
	color = calCrossProcess(color);
	color = calNewSaturation(color, - 50.0);
	color = multiply(color, vec4(rOffset, gOffset, bOffset, 0.15), 1);
	gl_FragColor = color;
}

