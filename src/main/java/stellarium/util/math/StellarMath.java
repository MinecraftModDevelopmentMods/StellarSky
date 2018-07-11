package stellarium.util.math;

import stellarapi.api.lib.math.Matrix3;
import stellarapi.api.lib.math.Spmath;
import stellarapi.api.lib.math.Vector3;

public class StellarMath {

	public static final double infmin=1.0e-2;

	public static final float sqr(float val) {
		return val * val;
	}

	//extract double from String
	public static final double btoD(byte[] dbs, int start, int size){
		int i;
		boolean under=false;
		double now=0.0, mult=1.0;
		for(i=start; i<start+size; i++){
			if('0'<=dbs[i] && dbs[i]<='9'){
				if(!under){
					now*=10;
					now+=(dbs[i]-'0');
				}
				else{
					mult*=0.1;
					now+=(dbs[i]-'0')*mult;
				}
			}
			else if(dbs[i]=='.'){
				under=true;
			}
		}
		return now;
	}

	//extract int from byte
	public static final int btoi(byte[] b, int start, int size){
		int i;
		int cnt=0;
		for(i=start; i<start+size; i++){
			cnt*=10;

			if(b[i] == ' ')
				continue;

			cnt+=(b[i]-'0');
		}

		return cnt;
	}

	//extract int from String
	public static final int StrtoI(String istr){
		return btoi(istr.getBytes(), 0, istr.getBytes().length);
	}

	//extract double from String
	public static final double StrtoD(String istr){
		return btoD(istr.getBytes(), 0, istr.getBytes().length);
	}

	//extract signal of number and set
	public static final double sgnize(byte b, double num){
		if(b=='-') return -num;
		else return num;
	}

	//extract signal of number and set
	public static final float sgnize(byte b, float num){
		if(b=='-') return -num;
		else return num;
	}

	//Calculate Eccentric Anomaly in Radians
	public static final double calEcanomaly(double ecc, double radM){
		double radE = radM + ecc * Math.sin(radM);
		double delE = getDelE(radM,radE,ecc);
		int i=0;
		while((Math.abs(delE)>infmin)) {
			radE = radE + delE;
			delE = getDelE(radM,radE,ecc);
			if(radE > Math.PI || radE < -Math.PI || Math.abs(delE) > Math.PI/2 || i > 1000) {
				return radM + ecc*Math.sin(radM);
			}
			i++;
		}
		return radE;
	}

	//Calculating Eccentric Anomaly in Radians
	public static final double getDelE(double radM, double radE, double ecc){
		double delM=radM-(radE-ecc*Math.sin(radE));
		return delM/(1.0-ecc*Math.cos(radE));
	}

	public static Vector3 getOrbVec(double a, double e, double M, Matrix3 rotation){
		M=(M+180.0)%360.0-180.0;
		double radE=calEcanomaly(e, Math.toRadians(M));
		Vector3 r = new Vector3(a*(Math.cos(radE)-e), a*Math.sqrt(1-e*e)*Math.sin(radE), 0.0);
		rotation.transform(r);
		return r;
	}

	public static double TemptoB_V(double temp) {
		double logT=Math.log10(temp);
		double B_V ;
		if(logT<3.961) B_V= -3.684 * logT + 14.551;
		else B_V = 0.344*logT*logT -3.402*logT +8.037;
		return B_V;
	}

	public static float clip(float f) {
		return Math.min(Math.max(f, 0.0f), 1.0f);
	}
}