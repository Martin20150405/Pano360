precision mediump float;
uniform sampler2D sTexture;
varying vec2 vTextureCoord;
uniform float stepSizeX;
uniform float stepSizeY;

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

vec4 smoothes(vec2 coord, float lX, float lY, float radius,float stepX, float stepY) {
	vec4 color = vec4(0.0, 0.0, 0.0, 0.0);
	float num = 0.0;
	float startPos = coord.x - lX * radius;
	float endPos = coord.x + lX * radius;
	for (float i = startPos; i <= endPos; i += stepX) {
		vec4 tmpColor = texture2D(sTexture, vec2(i, coord.y));
		color += tmpColor;
		num++;
	}
	startPos = coord.y - lY * radius;
	endPos = coord.y + lY * radius;
	for (float i = startPos; i <= endPos; i += stepY) {
		vec4 tmpColor = texture2D(sTexture, vec2(coord.x, i));
		color += tmpColor;
		num++;
	}
	color/=num;
	return color;
}

void main() {
	vec4 color = texture2D(sTexture, vTextureCoord);
	float sum = color.r + color.g + color.b;
	float r = color.r;
	float g = color.g;
	float b = color.b;
	if (r > 0.3725 && g > 0.1569 && b > 0.0784 && r > b) {
		if (max(r, max(g,b)) - min(r, min(g, b)) > 0.05882) {
			if (abs(r - g) > 0.05882) {
				vec4 tmpColor = smoothes(vTextureCoord, stepSizeX, stepSizeY, 10.0, stepSizeX * 5.0, stepSizeY * 5.0);
				color = (color + tmpColor) / 2.0;
			}
		}
	}
	color.r = (color.r + 1.0 - (1.0 - color.r) * (1.0 - color.r)) / 2.0;
	color.g = (color.g + 1.0 - (1.0 - color.g) * (1.0 - color.g)) / 2.0;
	color.b = (color.b + 1.0 - (1.0 - color.b) * (1.0 - color.b)) / 2.0;
	color = calBrightnessContract(color, 0.0, 40.0, 128.0);
	gl_FragColor = color;
}

