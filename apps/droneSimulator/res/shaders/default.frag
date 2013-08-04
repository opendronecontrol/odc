
varying float depth;
varying vec3 normal;
varying vec3 eye;
varying vec3 lightPosition;
varying vec4 color;

void main()
{
  // these values are set empirically based on the dimensions
  // of the bunny model
  float near = -0.4;
  float far = 1.5;

  // get shifted versions for better visualization
  float depthShift = (depth + 1.5)/2.1; //1.0 - ((depth - near) / (far - near));
  vec3 normalShift = (normal + vec3(1.0)) * 0.5;

  //gl_FragData[0] = color;
  gl_FragData[0] = color*vec4(normalShift, depthShift);
  //gl_FragData[0] = vec4(normal, 1.0);
  //gl_FragData[0] = vec4(vec3(depthShift), 0.9);
  //gl_FragData[1] = vec4(normalize(eye), 1.0);
  //gl_FragData[2] = vec4(normalize(lightPosition), 1.0);
}
