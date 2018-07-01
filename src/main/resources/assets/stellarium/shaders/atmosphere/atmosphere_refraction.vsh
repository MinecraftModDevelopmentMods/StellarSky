#version 120

uniform float pitch;
uniform vec3 relative;

void main() {
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;

	// Calculates the screen coord
	vec3 sCoord = gl_MultiTexCoord0.xyz - vec3(0.5, 0.5, 0.0);
	sCoord.z = 1.0;
	sCoord *= relative;

    // Pitch up to find the world coord
    vec3 wCoord = vec3(sCoord);
    float c = cos(pitch);
    float s = sin(pitch);
    wCoord.y = sCoord.y * c + sCoord.z * s;
    wCoord.z = - sCoord.y * s + sCoord.z * c;

    // Get unrefracted position for this point and apply it
    float h = asin(wCoord.y / length(wCoord));
    float ref = radians(1.0 / 60.0) / tan(radians(degrees(h) + 7.31/(degrees(h) + 4.4)));
    float d = atan(wCoord.x, wCoord.z);
    vec3 wPos = vec3(cos(h-ref) * sin(d), sin(h-ref), cos(h-ref) * cos(d));

    // Pitch down to find the screen coord
    vec3 sPos = vec3(wPos);
    sPos.y = wPos.y * c - wPos.z * s;
    sPos.z = wPos.y * s + wPos.z * c;

    // De-normalize
    sPos = sPos / sPos.z;
    sPos /= relative;
    sPos += vec3(0.5, 0.5, 0.0);

	gl_TexCoord[0].xy = sPos.xy;
}