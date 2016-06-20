#version 120

varying vec2 dominationCoord;
varying vec4 color;

#define PI 3.1415926535897932384626433832795

void main() {
    float x = gl_Vertex.x;
    float y = gl_Vertex.y;
    float z = gl_Vertex.z;

    vec4 vertex = vec4(z*cos(y)*cos(x), z*cos(y)*sin(x), z*sin(y), 1);
	gl_Position = gl_ModelViewProjectionMatrix * vertex;
	dominationCoord = vec2(x / (2.0 * PI), y / PI + 0.5);
	color = gl_Color;
}