package stellarium.stellars.deepsky;

import stellarium.util.math.StellarMath;

public class PositionUtil {
	
	public static double getDegreeFromDMS(String dms) {
		double sign = dms.startsWith("-")? -1 : 1;
		double value = 0.0, unit = 1.0;
		dms.replaceAll("(-|+)", "");
		String[] splitted = dms.split("d|m|s");
		for(String split : splitted) {
			value += StellarMath.StrtoD(split) * unit;
			unit /= 60.0;
		}
		return sign * value;
	}
	
	public static double getDegreeFromHMS(String hms) {
		double sign = hms.startsWith("-")? -1 : 1;
		double value = 0.0, unit = 15.0;
		hms.replaceAll("(-|+)", "");
		String[] splitted = hms.split("h|m|s");
		for(String split : splitted) {
			value += StellarMath.StrtoD(split) * unit;
			unit /= 60.0;
		}
		return sign * value;
	}

}
