#version 120

#define PI 3.1415926535897932384626433832795

void main() {
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
	gl_TexCoord[0]  = gl_MultiTexCoord0;
}