package stellarium.stellars.deepsky;

import java.util.regex.Pattern;

import stellarium.util.math.StellarMath;

public class PositionUtil {
	
	public static double getDegreeFromDMS(String dms) {
		double sign = dms.startsWith("-")? -1 : 1;
		double value = 0.0;
		dms.replaceAll("(-|\\+)", "");
		
		String[] splitted = dms.split("(?<=(d|m|s))");
		for(String split : splitted) {
			double val = StellarMath.StrtoD(split.substring(0, split.length()-1));
			if(split.endsWith("d"))
				value += val;
			else if(split.endsWith("m"))
				value += val / 60.0;
			else value += val / 3600.0;
		}
		return sign * value;
	}
	
	public static double getDegreeFromHMS(String hms) {
		double sign = hms.startsWith("-")? -1 : 1;
		double value = 0.0;
		hms.replaceAll("(-|\\+)", "");
		String[] splitted = hms.split("(?<=(h|m|s))");
		for(String split : splitted) {
			double val = StellarMath.StrtoD(split.substring(0, split.length()-1));
			if(split.endsWith("h"))
				value += val * 15.0;
			else if(split.endsWith("m"))
				value += val / 4.0;
			else value += val / 240.0;
		}
		return sign * value;
	}

	public static double getMagnitude(String asString) {
		return Double.parseDouble(asString.replaceAll("V", ""));
	}

}
