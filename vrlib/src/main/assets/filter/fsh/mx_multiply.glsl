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
vec4 calVignette(vec2 coord,vec4 color,float texture_width,float texture_height) {
	float shade = 0.6;
	float slope = 20.0;
	float range = 1.30 - sqrt(0.8) * 0.7;
	vec2 scale;
	if(texture_width > texture_height) {
		scale.x = 1.0;
		scale.y = texture_height / texture_width;
	} else {
		scale.x = texture_width / texture_height;
		scale.y = 1.0;
	}
	float inv_max_dist = 2.0 / length(scale);
	float dist = length((coord - vec2(0.5, 0.5)) * scale);
	float lumen = shade / (1.0 + exp((dist * inv_max_dist - range) * slope)) + (1.0 - shade);
	return vec4(color.rgb * lumen,color.a);
}
vec4 calNewVignette(vec2 coord,vec4 color,float texture_width,float texture_height,float value) {
	float shade = 0.85;
	float slope = 20.0;
	float range = 1.30 - sqrt(value) * 0.7;
	vec2 scale;
	if(texture_width > texture_height) {
		scale.x = 1.0;
		scale.y = texture_height / texture_width;
	} else {
		scale.x = texture_width / texture_height;
		scale.y = 1.0;
	}
	float inv_max_dist = 2.0 / length(scale);
	float dist = length((coord - vec2(0.5, 0.5)) * scale);
	float lumen = shade / (1.0 + exp((dist * inv_max_dist - range) * slope)) + (1.0 - shade);
	return vec4(color.rgb * lumen,color.a);
}
vec4 calVignette2(vec4 color, vec2 coord, float strength) {
	float distance = (coord.x - 0.5) * (coord.x - 0.5) + (coord.y - 0.5) * (coord.y - 0.5);
	float scale = distance / 0.5 * strength;
	color.r =  color.r - scale;
	color.g = color.g - scale;
	color.b = color.b - scale;
	return color;
}
void main() {
	vec4 color = texture2D(sTexture, vTextureCoord);
	float alpha = 0.73;
	float r =  color.r * (1.0 - alpha) + color.r * 0.984 * alpha;
	float g = color.g * (1.0 - alpha) + color.g * 0.69 * alpha;
	float b = color.b * (1.0 - alpha) + color.b * 0.517 * alpha;
	alpha = 0.32;
	color.r =  color.r * (1.0 - alpha) + r * alpha;
	color.g = color.g * (1.0 - alpha) + g * alpha;
	color.b = color.b * (1.0 - alpha) + b * alpha;
	color = calVignette2(color, vTextureCoord, 0.3);
	alpha = 0.35;
	vec4 cpColor = calCrossProcess(color);
	color.r = cpColor.r * alpha + color.r * (1.0 - alpha);
	color.g = cpColor.g * alpha + color.g * (1.0 - alpha);
	color.b = cpColor.b * alpha + color.b * (1.0 - alpha);
    color.r = texture2D(sTexture2, vec2(color.r, 1.0)).a;
    color.g = texture2D(sTexture2, vec2(color.g, 1.0)).a;
    color.b = texture2D(sTexture2, vec2(color.b, 1.0)).a;
	gl_FragColor = color;
}

