#version 300 es
in vec4 a_Position;
in vec3 a_Color;
uniform mat4 u_Matrix;
out vec3 f_Color;
void main() {
    gl_Position = u_Matrix * a_Position;
    gl_PointSize = 40.0;
    f_Color = a_Color;
}