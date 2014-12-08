in float lum;
in vec3 color;
in vec2 texCoord;

uniform sampler2D color_texture;

void main() {
    gl_FragColor = texture2D(color_texture, gl_TexCoord[0].st) * lum;
}