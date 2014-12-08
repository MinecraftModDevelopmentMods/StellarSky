package stellarium.initials;

public class CCertificateHelper {
	
	public static final double StarLimit=0.08;
	public static final double SatMassLimit=0.01;
	
	public static void IllegalConfig(String Content){
		throw new CreationException("Illegal Configuration: "+Content);
	}
	
	public static void Unstable(String Cause){
		throw new CreationException("Unstable System;\n Cause: "+Cause);
	}
}
