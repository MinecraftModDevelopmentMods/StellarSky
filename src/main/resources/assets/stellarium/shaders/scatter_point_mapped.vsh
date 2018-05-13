#version 120

varying vec4 color;

#define PI 3.1415926535897932384626433832795

void main() {
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
	color = gl_Color;
}