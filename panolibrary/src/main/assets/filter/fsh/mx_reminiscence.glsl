precision mediump float;
uniform sampler2D sTexture;
varying vec2 vTextureCoord;
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
float calChannelColorLevel(float color,float minValue,float maxValue,float gamma,float outputMinValue,float outputMaxValue) {
   if (color > maxValue) {
       color = outputMaxValue;
   } else if (color < minValue) {
       color = outputMinValue;
   } else {
       color = pow((color - minValue) / (maxValue - minValue),1.0 / gamma);
		color = color * (outputMaxValue - outputMinValue) + outputMinValue;
   }
	return color;
}
vec4 calColorLevel(vec4 color,float minValue,float maxValue,float gamma,float outputMinValue,float outputMaxValue) {
	color.r = calChannelColorLevel(color.r,minValue, maxValue, gamma, outputMinValue, outputMaxValue);
	color.g = calChannelColorLevel(color.g,minValue, maxValue, gamma, outputMinValue, outputMaxValue);
	color.b = calChannelColorLevel(color.b,minValue, maxValue, gamma, outputMinValue, outputMaxValue);
   return color;
}
void main() {
	vec4 color = texture2D(sTexture,vTextureCoord);
	color.r = 0.6 * color.r + 0.08;
	color.g = 0.6 * color.g + 0.08;
	color.b = 0.6 * color.b + 0.08;
	float grayValue = dot(color.rgb, vec3(0.299,0.587,0.114));
	color.r = 0.09498 * grayValue + 0.854824 * color.r;
	color.g = 0.083216 * grayValue + 0.748941 * color.g;
	color.b = 0.067059 * grayValue + 0.603529 * color.b;
	color.r = calChannelColorLevel(color.r, 0.050980, 1.0, 0.83, 0.0, 1.0);
	color.g = calChannelColorLevel(color.g, 0.141176, 0.956862, 1.28, 0.0, 0.890196);
	color.b = calChannelColorLevel(color.b, 0.066667, 1.0, 0.86, 0.086274, 1.0);
	color = calColorLevel(color, 0.035294, 0.886275, 1.25, 0.058824, 0.878431);
	color.r = 1.554321 * color.r  - 0.082217;
	color.g = 1.508024 * color.g - 0.081747;
	color.b = 1.444444 * color.b - 0.081111;
	color = calVignette2(color, vTextureCoord, 0.5);
	gl_FragColor = color;
}

