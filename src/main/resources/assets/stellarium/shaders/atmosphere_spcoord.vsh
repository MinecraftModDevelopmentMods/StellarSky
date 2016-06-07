#version 130
uniform vec3 lightDir;      // Direction vectors to the light source
uniform vec3 lightColor;    // Brightness of the light source

uniform float cameraHeight;     // Camera height scaled by scale height
uniform float outerRadius;     // The outer (atmosphere) radius scaled by scale height
uniform float innerRadius;     // The inner (planetary) radius scaled by scale height
uniform int nSamples;
uniform vec3 extinctionFactor;
uniform float depthToFogFactor;

const vec3 zenithDir = vec3(0.0, 0.0, 1.0);
out vec4 scatteringColor4;
out vec3 v3Direction;
#define PI 3.1415926535897932384626433832795
#define ERFC_FACTOR 0.147

float calcScale(float x) {
	return (1 - sqrt(1 - exp(-x*(4 / PI + ERFC_FACTOR*x) / (1 + ERFC_FACTOR*x)))) * exp(x);
}

//Airmass on the horizon
float airmassFactor(float viewRadiusScaled) {
	return sqrt(PI * viewRadiusScaled / 2);
}

float airmass(float cosAngleToZenith, float viewRadiusScaled) {
	viewRadiusScaled *= 7.0 / 6.0;
	float scale = cosAngleToZenith * cosAngleToZenith * viewRadiusScaled / 2;
	float result;

	if (scale > 12.0)
		result = 1.0 / abs(cosAngleToZenith);
	else result = airmassFactor(viewRadiusScaled) * calcScale(scale);

	if (cosAngleToZenith >= 0.0)
		return result;
	else return 2 * airmassFactor(viewRadiusScaled) * exp(viewRadiusScaled * (1.0 - sqrt(1.0 - cosAngleToZenith*cosAngleToZenith))) - result;
}

void main() {
    float x = gl_Vertex.x;
    float y = gl_Vertex.y;

	// Get the ray from the camera to the vertex and its length (which

	// is the far point of the ray passing through the atmosphere)

	vec3 v3Pos = vec3(cos(y)*cos(x), cos(y)*sin(x), sin(y));

	vec3 v3Ray = vec3(v3Pos);

	float fFar = length(v3Ray);

	v3Ray /= fFar;

	float cameraRadius = cameraHeight + innerRadius;

	// Calculate the ray's start and end positions in the atmosphere,
	
	// then calculate its scattering offset

	vec3 v3Start = vec3(0.0, 0.0, cameraRadius);

	float cosViewAngle = v3Ray.z;
	float invertFlag = 1.0;

	float depth2 = (1.0 - cosViewAngle * cosViewAngle) * cameraRadius * cameraRadius;

	if (cosViewAngle > 0.0 || depth2 > innerRadius * innerRadius)
		fFar = sqrt(outerRadius * outerRadius - depth2) - sign(cosViewAngle) * sqrt(cameraRadius * cameraRadius - depth2);
	else {
		fFar = sqrt(cameraRadius * cameraRadius - depth2) - sqrt(innerRadius * innerRadius - depth2);
		cosViewAngle = -cosViewAngle;
		v3Start += fFar * v3Ray;
		v3Ray = -v3Ray;
		invertFlag = -1.0;
	}

	float airmassCamera = airmass(cosViewAngle, cameraRadius);
	float depthCamera = airmassCamera * exp(innerRadius - cameraRadius);

	// Initialize the scattering loop variables

	float fSampleLength = fFar / nSamples;

	float fScaledLength = fSampleLength / (outerRadius - innerRadius);

	vec3 v3SampleRay = v3Ray * fSampleLength;

	vec3 v3SamplePoint = v3Start + v3SampleRay * 0.5;


	// Now loop through the sample points

	vec3 integratedScatterColor = vec3(0.0, 0.0, 0.0);
	float debug = 0.0;

	for (int i = 0; i < nSamples; i++) {

		float radiusSample = length(v3SamplePoint);

		float depthFactor = exp(innerRadius - radiusSample);

		float cosLightAngle = dot(lightDir, v3SamplePoint) / radiusSample;

		depthFactor *= float((cosLightAngle >= 0.0 || radiusSample * radiusSample * (1.0 - cosLightAngle * cosLightAngle) >= innerRadius * innerRadius));

		float cosCameraAngle = dot(v3Ray, v3SamplePoint) / radiusSample;

		float depthCurrent = (invertFlag * depthCamera + (airmass(cosLightAngle, radiusSample) - invertFlag * airmass(cosCameraAngle, radiusSample)) * depthFactor);

		vec3 v3Extincted = exp(- depthCurrent * extinctionFactor);

		integratedScatterColor += max(v3Extincted * depthFactor * fScaledLength, 0.0);

		v3SamplePoint += v3SampleRay;
	}
	
	float depthEnd;
	if (invertFlag == 1.0) {
		float airmassEnd = airmass(cosViewAngle, length(v3SamplePoint));
		depthEnd = airmassEnd * exp(innerRadius - length(v3SamplePoint));
	}
	else {
		float airmassEnd = airmass(cosViewAngle, length(v3Start));
		depthEnd = airmassEnd * exp(innerRadius - length(v3Start));
	}


	// Finally, scale the Mie and Rayleigh colors

	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
	scatteringColor4.rgb = integratedScatterColor * lightColor;
	v3Direction.xyz = v3Pos;

    scatteringColor4.r = gl_Vertex.x / PI * 2;
    scatteringColor4.g = gl_Vertex.y;
    
    //if(gl_Vertex.x > 0.15)
    //    scatteringColor4.b = 1;
    //else scatteringColor4.b = 0;

	gl_FogFragCoord = exp(invertFlag * (depthEnd - depthCamera) / depthToFogFactor);
}