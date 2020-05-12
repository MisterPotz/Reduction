#version 300 es
precision mediump float;

in vec3 f_Color;
out vec4 color;
void main() {
    color = vec4(f_Color, 1.0);
}