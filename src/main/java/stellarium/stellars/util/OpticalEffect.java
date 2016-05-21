package stellarium.stellars.util;

public class OpticalEffect {
	
	public static double[] faintToWhite(double[] rgb, double alpha) {
		alpha = Math.min(alpha, 1.0);
		return new double[] {
				rgb[0] * alpha + 1.0 - alpha,
				rgb[1] * alpha + 1.0 - alpha,
				rgb[2] * alpha + 1.0 - alpha
		};
	}

}
