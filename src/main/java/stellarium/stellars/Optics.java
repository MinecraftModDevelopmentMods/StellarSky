package stellarium.stellars;

public class Optics {
	public static float GetAlphaFromMagnitude(float Mag, float bglight){
		return (float) ((Math.pow(10.0f, (1.0f-Mag)/2.5f)-4.0f*bglight)*2.0f);
	}

	public static float GetAlphaFromMagnitude(double Mag, float bglight) {
		return (float) ((Math.pow(10.0f, (1.0-Mag)/2.5f)-4.0f*bglight)*2.0f);
	}
	
}
