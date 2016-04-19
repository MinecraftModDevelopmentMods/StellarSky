package stellarium.util.math;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import javafx.scene.transform.Rotate;
import stellarapi.api.lib.math.Spmath;

public class StellarMath {
	
	public static final double infmin=1.0e-2;
	
	//extract double from String
	public static final double StrtoD(String dstr){
		byte[] dbs=dstr.getBytes();
		int i;
		boolean under=false;
		double now=0.0, mult=1.0;
		for(i=0; i<dbs.length; i++){
			if('0'<=dbs[i] && dbs[i]<='9'){
				if(!under){
					now*=10;
					now+=(dbs[i]-'0');
				}
				else{
					mult*=0.1;
					now+=dbs[i]*mult;
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
			cnt+=(b[i]-'0');
		}
		
		return cnt;
	}
	
	//extract int from String
	public static final int StrtoI(String istr){
		return btoi(istr.getBytes(), 0, istr.getBytes().length);
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
	
	//Calculate Eccentric Anomaly
	public static final double CalEcanomaly(double e, double M){
		double E=M+e*Spmath.sind(M);
		double delE=GetdelE(M,E,e);
		int i=0;
		while((Math.abs(delE)>infmin))
		{
			E=E+delE;
			delE=GetdelE(M,E,e);
			if(E>180.0 || E<-180.0 || Math.abs(delE)>90.0 || i>1000)
			{
				return M+e*Spmath.sind(M);
			}
			i++;
		}
		return E;
	}
	
	//Support for Calculating Eccentric Anomaly
	public static final double GetdelE(double M, double E, double e){
		double delM=M-(E-e*Spmath.sind(E));
		return delM/(1.0-e*Spmath.cosd(E));
	}
	
	public static Vector3d GetOrbVec(double a, double e, double M, Matrix3d rotation){
		M=Spmath.fmod(M+180.0,360.0)-180.0;
		double e2=Spmath.Degrees(e);
		double E=CalEcanomaly(e2, M);
		Vector3d r = new Vector3d(a*(Spmath.cosd(E)-e), a*Math.sqrt(1-e*e)*Spmath.sind(E), 0.0);
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
	
	public static double MagToLum(double Mag){
		return Math.pow(10.0, (-26.74) - Mag/2.5);
	}
	
	public static double MagToLumWithoutSize(double Mag){
		return Math.pow(10.0, - Mag/2.5);
	}
}