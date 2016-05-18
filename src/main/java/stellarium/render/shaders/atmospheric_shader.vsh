#version 130
uniform vec3 v3LightDir;      // Direction vector to the light source
uniform float cameraHeight;     // Camera height scaled by scale height
uniform float outerRadius;     // The outer (atmosphere) radius scaled by scale height
uniform float innerRadius;     // The inner (planetary) radius scaled by scale height
uniform int nSamples;
uniform vec3 extinctionFactor;
const vec3 zenithDir = vec3(0.0, 0.0, 1.0);
out vec4 scatteringColor4;
out vec3 v3Direction;
#define PI 3.1415926535897932384626433832795
#define ERFC_FACTOR 0.147

float erfcSqrt(float x) {
	return 1 - sqrt(1 - exp(-x*(4/PI + ERFC_FACTOR*x)/(1+ERFC_FACTOR)));
}

float airmass(float cosAngleToZenith, float viewRadiusScaled) {
	float scale = cosAngleToZenith * cosAngleToZenith * viewRadiusScaled / 2;
	float result = sqrt(PI * viewRadiusScaled / 2) * exp(scale) * erfcSqrt(scale);
	if (cosAngleToZenith >= 0.0)
		return result;
	else return 2 * airmass_zero(viewRadiusScaled) - result;
}

float airmass_zero(float viewRadiusScaled) {
	return sqrt(PI * viewRadiusScaled / 2);
}

void main() {

	// Get the ray from the camera to the vertex and its length (which

	// is the far point of the ray passing through the atmosphere)

	vec3 v3Pos = gl_Vertex.xyz;

	vec3 v3Ray = vec3(v3Pos);

	float fFar = length(v3Ray);

	v3Ray /= fFar;


	float cameraRadius = cameraHeight + innerRadius;

	// Calculate the ray's start and end positions in the atmosphere,
	
	// then calculate its scattering offset

	vec3 v3Start = zenithDir * cameraRadius;

	float cosViewAngle = dot(v3Ray, zenithDir);

	float airmassCamera = airmass(cosViewAngle, cameraRadius);

	float depth = cosViewAngle * cameraRadius;
	
	fFar = (sqrt(outerRadius * outerRadius - depth * depth) - sign(cosViewAngle) * sqrt(cameraRadius * cameraRadius - depth * depth));


	// Initialize the scattering loop variables

	float fSampleLength = fFar / (nSamples + 1);

	float fScaledFactor = 1.0 / nSamples;

	vec3 v3SampleRay = v3Ray * fSampleLength;

	vec3 v3SamplePoint = v3Start + v3SampleRay * 0.5;


	// Now loop through the sample points

	vec3 integratedScatterColor = vec3(0.0, 0.0, 0.0);

	for (int i = 0; i<nSamples; i++) {

		float radiusSample = length(v3SamplePoint);

		float fDepth = exp(innerRadius - radiusSample);

		float cosLightAngle = dot(v3LightDir, v3SamplePoint) / radiusSample;

		float cosCameraAngle = dot(v3Ray, v3SamplePoint) / radiusSample;

		float airmass = (airmassCamera - airmass(cosCameraAngle, radiusSample) + airmass(cosLightAngle, radiusSample));

		vec3 v3Extincted = exp(- airmass * extinctionFactor);

		integratedScatterColor += v3Extincted * fDepth * fScaledFactor;

		v3SamplePoint += v3SampleRay;

	}


	// Finally, scale the Mie and Rayleigh colors

	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
	scatteringColor4.rgb = integratedScatterColor;
	scatteringColor4.a = 1.0;
	v3Direction = - v3Ray;
}