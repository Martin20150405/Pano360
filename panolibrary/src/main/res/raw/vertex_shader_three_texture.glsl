attribute vec4 aPosition;
attribute vec4 aTextureCoord;

varying vec2 vTextureCoord;

void main() {
  vTextureCoord = aTextureCoord.xy;
  gl_Position = aPosition;
}