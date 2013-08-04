
attribute vec4 a_position;
attribute vec2 a_texCoord0;

uniform mat4 u_projectionViewMatrix;

varying vec2 v_texCoords;

void main()
{
  // pass through the texture coordinate
  v_texCoords = a_texCoord0;
  
  // pass through the quad position
  gl_Position = u_projectionViewMatrix * a_position;
}
