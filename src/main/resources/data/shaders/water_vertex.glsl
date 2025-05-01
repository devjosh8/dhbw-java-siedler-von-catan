attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;

uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;
uniform mat3 u_normalMatrix;

uniform float u_time;

varying vec3 v_normal;
varying vec2 v_texCoord;
varying float v_time;

void main() {
    v_texCoord = a_texCoord0;
    v_time = u_time;

    v_normal = normalize(u_normalMatrix * a_normal);

    gl_Position = u_projViewTrans * u_worldTrans * vec4(a_position, 1.0);
}
