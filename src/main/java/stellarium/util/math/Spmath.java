package stellarium.util.math;

import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EVector;
import sciapi.api.value.euclidian.IEVector;
import sciapi.api.value.numerics.IReal;
import net.minecraft.util.MathHelper;

public class Spmath {
	
	public static final double infmin=1.0e-2;
	
	public static final float PI=(float)Math.PI;
	
	public static final int signi = 60001;
	
	public static float dattan[];
	
	//Angle Undercut
	public static final double AngleUndercut(double x){
		if(x<0) return x+2.0*Math.PI;
		return x;
	}
	
	//fmod
	public static final double fmod(double a, double b){
		return a-Math.floor(a/b)*b;
	}
	
	//fmod
	public static final float fmod(float a, float b){
		return a-(float)Math.floor(a/b)*b;
	}
	
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
	
	//Degrees to Radians
	public static final double Radians(double d){
		return d*Math.PI/180.0;
	}
	
	//Radians to Degrees
	public static final double Degrees(double r){
		return r/Math.PI*180.0;
	}
	
	//Degrees to Radians
	public static final float Radians(float d){
		return d*PI/180.0f;
	}
	
	//Radians to Degrees
	public static final float Degrees(float r){
		return r/PI*180.0f;
	}
	
	//Preparing datas for float sin/cos/tan
	public static final void Initialize(){
		int i;
		dattan=new float[signi];
		for(i=0; i<signi; i++){
			dattan[i]=(float)Math.tan((double)i*Math.PI/signi);
		}
	}
	
	//Float sine
	public static final float sinf(float d){
		return MathHelper.sin(d);
	}
	
	//Float cosine
	public static final float cosf(float d){
		return MathHelper.cos(d);
	}
	
	//Float tangent
	public static final float tanf(float d){
		int k=MathHelper.floor_float(d*signi/PI);
		k%=signi;
		if(k<0) k+=signi;
		return dattan[k];
	}
	
	//Degree sine
	public static final double sind(double d){
		return Math.sin(Radians(d));
	}
	
	//Degree cosine
	public static final double cosd(double d){
		return Math.cos(Radians(d));
	}
	
	//Degree tangent
	public static final double tand(double d){
		return Math.tan(Radians(d));
	}
	
	//Degree sine
	public static final float sind(float d){
		return sinf(Radians(d));
	}
	
	//Degree cosine
	public static final float cosd(float d){
		return cosf(Radians(d));
	}
	
	//Degree tangent
	public static final float tand(float d){
		return tanf(Radians(d));
	}
	
	
	public static final double getD(IValRef<IReal> val){
		val.onUsed();
		return val.getVal().asDouble();
	}
	
	//Calculate Eccentric Anomaly
	public static final double CalEcanomaly(double e, double M){
		double E=M+e*sind(M);
		double delE=GetdelE(M,E,e);
		int i=0;
		while((Math.abs(delE)>infmin))
		{
			E=E+delE;
			delE=GetdelE(M,E,e);
			if(E>180.0 || E<-180.0 || Math.abs(delE)>90.0 || i>1000)
			{
				return M+e*sind(M);
			}
			i++;
		}
		return E;
	}
	
	//Support for Calculating Eccentric Anomaly
	public static final double GetdelE(double M, double E, double e){
		double delM=M-(E-e*sind(E));
		return delM/(1.0-e*cosd(E));
	}
	
	public static IValRef GetOrbVec(double a, double e, Rotate Ir, Rotate wr, Rotate Omr, double M){
		M=Spmath.fmod(M+180.0,360.0)-180.0;
		double e2=Spmath.Degrees(e);
		double E=Spmath.CalEcanomaly(e2, M);
		IValRef r = new EVector(a*(cosd(E)-e), a*Math.sqrt(1-e*e)*sind(E), 0.0);
		return Omr.transform(Ir.transform(wr.transform(r)));
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
}
