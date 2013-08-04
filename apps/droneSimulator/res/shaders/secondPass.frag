uniform sampler2D depthTexture;
uniform sampler2D eyeTexture;
uniform sampler2D lightTexture;

varying vec2 v_texCoords;

void main() 
{
  // pull everything we want from the textures
  float depth   = texture2D(depthTexture, v_texCoords).a;
  vec3 normal   = texture2D(depthTexture, v_texCoords).rgb;
  vec3 eye      = texture2D(eyeTexture,   v_texCoords).rgb;
  vec3 light    = texture2D(lightTexture, v_texCoords).rgb;

  //edge detect
  float d = 0.005;
  vec2 coord = v_texCoords;
  float diff = -4.0 * depth;
  diff += texture2D(depthTexture, vec2(coord.x+d, coord.y)).a;
  diff += texture2D(depthTexture, vec2(coord.x-d, coord.y)).a;
  diff += texture2D(depthTexture, vec2(coord.x, coord.y+d)).a;
  diff += texture2D(depthTexture, vec2(coord.x, coord.y-d)).a;
  float edge = 1.0 - step(0.05,abs(diff));

  //average normals;
  normal += texture2D(depthTexture, vec2(coord.x+d, coord.y)).rgb;
  normal += texture2D(depthTexture, vec2(coord.x-d, coord.y)).rgb;
  normal += texture2D(depthTexture, vec2(coord.x, coord.y+d)).rgb;
  normal += texture2D(depthTexture, vec2(coord.x, coord.y-d)).rgb;
  normal += texture2D(depthTexture, vec2(coord.x+d, coord.y+d)).rgb;
  normal += texture2D(depthTexture, vec2(coord.x-d, coord.y+d)).rgb;
  normal += texture2D(depthTexture, vec2(coord.x-d, coord.y+d)).rgb;
  normal += texture2D(depthTexture, vec2(coord.x+d, coord.y-d)).rgb;
  normal /= 9.0;

  // repackage the normal component
  normal = normal * 2.0 - vec3(1.0);
  normal = normalize(normal);
 
  // get a diffuse lighting component
  float diffuse = dot(normal, light);

  // make it gray
  diffuse *= .5;
  if(diffuse < 1.0) diffuse = step(0.5,diffuse)*0.7;
  
  // specular
  vec3 reflectVec = reflect(-light, normal); 
  float spec = pow( max( dot(reflectVec, eye), 0.0), 10.0); 
  spec = step(.3, spec);

  // cartoon phong
  vec3 color = (vec3(diffuse) + vec3(spec) )*edge;

  //gl_FragColor = vec4(normal, 1.0);
  gl_FragColor = vec4(vec3(depth), 1.0);
  //gl_FragColor = vec4(vec3(diffuse), 1.0);
  //gl_FragColor = vec4(vec3(1.0)*edge,1.0);

  //gl_FragColor = vec4( color, 1.0);
}
