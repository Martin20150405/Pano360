attribute vec4 aPosition;
attribute vec4 aTextureCoord;
attribute vec4 aTextureCoord2;
varying vec2 vTextureCoord;
varying vec2 vTextureCoord2;
void main() {
  vTextureCoord = aTextureCoord.xy;
  vTextureCoord2 = aTextureCoord2.xy;
  gl_Position = aPosition;
}