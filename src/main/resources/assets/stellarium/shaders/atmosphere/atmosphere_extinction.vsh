#version 120

uniform float cameraHeight;     // Camera height scaled by scale height
uniform float outerRadius;     // The outer (atmosphere) radius scaled by scale height
uniform float innerRadius;     // The inner (planetary) radius scaled by scale height
uniform vec3 extinctionFactor;
uniform float weatherAlpha;

const vec3 zenithDir = vec3(0.0, 0.0, 1.0);
varying vec4 alpha4;
#define PI 3.1415926535897932384626433832795
#define ERFC_FACTOR 0.147

float calcScale(float x, float sgn) {
    return (1.0 - sgn * sqrt(1.0 - exp(-x*(4.0 / PI + ERFC_FACTOR*x) / (1.0 + ERFC_FACTOR*x)))) * exp(x);
}

//Airmass on the horizon
float airmassFactor(float viewRadiusScaled) {
    return sqrt(PI * viewRadiusScaled * 0.5);
}

// Path length of light relative to the zenith
float airmass(float cosAngleToZenith, float viewRadiusScaled) {
    // Refraction correction
    viewRadiusScaled *= 7.0 / 6.0;
    float scale = cosAngleToZenith * cosAngleToZenith * viewRadiusScaled / 2.0;
    float result;
    float sgn = float(cosAngleToZenith > 0) * 2.0 - 1.0;

    if (scale > 12.0 && cosAngleToZenith > 0)
        result = 1.0 / cosAngleToZenith;
    else result = airmassFactor(viewRadiusScaled) * calcScale(scale, sgn);
    return result;
}


void main() {
	// Get the ray from the camera to the vertex and its length (which
	// is the far point of the ray passing through the atmosphere)

	vec3 v3Pos = vec3(gl_Vertex);
	vec3 v3Ray = vec3(v3Pos);
	v3Ray /= length(v3Ray);

	float cameraRadius = cameraHeight + innerRadius;

	// Calculate the ray's start and end positions in the atmosphere,
	// then calculate its scattering offset

	vec3 v3Start = vec3(0.0, 0.0, cameraRadius);

	float cosViewAngle = v3Ray.z;
	float invertFlag = 1.0;

	float depth2 = (1.0 - cosViewAngle * cosViewAngle) * cameraRadius * cameraRadius;
	float fFar = sqrt(outerRadius * outerRadius - depth2) - cosViewAngle * cameraRadius;
    vec3 v3End = v3Start + fFar * v3Ray;

	float airmassCamera = airmass(cosViewAngle, cameraRadius);
	float depthCamera = airmassCamera * exp(innerRadius - cameraRadius);

    float lenEnd = length(v3End);
    float airmassEnd = airmass(dot(v3End, v3Ray) / lenEnd, lenEnd);
    float depthEnd = airmassEnd * exp(innerRadius - lenEnd);

	alpha4.rgb = weatherAlpha * exp(invertFlag * (depthEnd - depthCamera) * extinctionFactor);
	if(!(depthCamera < 0.0 || depthCamera > 0.0 || depthCamera == 0.0))
	   alpha4.rgb = vec3(0.0);
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}