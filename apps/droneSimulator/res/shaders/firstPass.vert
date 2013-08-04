
attribute vec4 a_position;
attribute vec4 a_normal;
attribute vec4 a_color;

uniform vec4 u_color;
uniform mat4 u_projectionViewMatrix;
uniform mat4 u_modelViewMatrix;
uniform mat4 u_normalMatrix;

//invariant gl_Position;

varying float depth;
varying vec3 normal;
varying vec3 eye;
varying vec3 lightPosition;
varying vec4 color;


void main()
{
  // get the depth and eye position
  vec4 transformedVertex = u_modelViewMatrix * a_position;
  //depth = -transformedVertex.z;
  depth = a_position.z;
  eye = -(u_modelViewMatrix * a_position).xyz;//-transformedVertex.xyz;

  // transform normals to the current view
  normal = a_normal.xyz; //normalize(u_normalMatrix * a_normal).xyz;

  // pass the light position through
  lightPosition = vec3(10.0,10.0,10.0);

  if( a_color != vec4(0.0,0.0,0.0,1.0)){
    color = a_color;
  }else{
    color = u_color;  
  }
  //color = a_color;

  gl_Position = u_projectionViewMatrix * a_position;
}

