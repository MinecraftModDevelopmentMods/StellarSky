package stellarium.stellars.background;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EVector;
import stellarium.StellarSky;
import stellarium.stellars.StellarManager;
import stellarium.stellars.StellarTransforms;
import stellarium.stellars.util.ExtinctionRefraction;
import stellarium.util.math.Rotate;
import stellarium.util.math.SpCoord;
import stellarium.util.math.Spmath;

public class BrStar extends Star {
	
	public boolean unable;
	//constants
	public static final int NumStar=9110;
	public static final int Bufsize=198;
	
	//Zero-time axial tilt
	public static final double e=0.4090926;
	public static final Rotate EqtoEc = new Rotate('X').setRAngle(-e); 
		
	//Magnitude
	public float Mag;
	
	//Apparent Magnitude
	public float App_Mag;
	
	//B-V Value
	public float B_V;
	
	//Apparant B-V
	public float App_B_V;
	
	private float RA, Dec;
	
	//stars
	public static BrStar stars[];
	
	//File
	public static byte str[];
	
	//Initialization check
	public static boolean IsInitialized=false;
	
	
	/*
	 * Get star's position
	 * time is 'tick' unit
	 * world is false in Overworld, and true in Ender
	*/
	public IValRef<EVector> GetPositionf(){
		return getManager().transforms.projection.transform(this.EcRPos);
	}
	
	public IValRef<EVector> GetAtmPosf(){
		return ExtinctionRefraction.refraction(GetPositionf(), true);
	}

	@Override
	public void update() {
		//Too many objects are created here
		if(Mag>StellarSky.proxy.getClientSettings().mag_Limit) this.unable=true;
		appPos.set(GetAtmPosf());
		float Airmass=(float) ExtinctionRefraction.airmass(appPos, true);
    	App_Mag= (Mag+Airmass*ExtinctionRefraction.ext_coeff_Vf);
    	App_B_V= (B_V+Airmass*ExtinctionRefraction.ext_coeff_B_Vf);
	}
	
	
	//Load Stars
	public static final void initializeAll() throws IOException{
		//stars
		stars=new BrStar[NumStar];
		
		//Counter Variable
		int i, j, k;
		
		//Initialize star_value
		for(i=0; i<NumStar; i++)
		{
			stars[i]=new BrStar();
			stars[i].star_value=new byte[Bufsize];
		}
		
		System.out.println("[Stellarium]: "+"Loading Bright Stars Data...");
		
		//Read
		str=new byte[NumStar*Bufsize];
		InputStream brs=BrStar.class.getResourceAsStream("/data/bsc5.dat");
	    BufferedInputStream bbrs = new BufferedInputStream(brs);
	    bbrs.read(str);
	    bbrs.close();
	    
	    //Input Star Information
	    j=0;
	    for(i=0; i<NumStar; i++)
	    {
	    	k=0;
	    	while(str[j]!='\n'){
	    		stars[i].star_value[k]=str[j];
	    		j++;
	    		k++;
	    	}
	    	j++;
	    	stars[i].initialize();
	    }
	    str=null;
	    
	    System.out.println("[Stellarium]: "+"Bright Stars are Loaded!");
	    IsInitialized=true;
	}
	
	public static void UpdateAll(){
		int i;
		for(i=0; i<NumStar; i++)
			if(!stars[i].unable) stars[i].update();
	}
	
	//Initialization in each star
	@Override
	public void initialize(){
				
		if(star_value[103]==' '){
			this.unable=true;
			return;
		}
		else this.unable=false;
		
		Mag=Spmath.sgnize(star_value[102],
				(float)Spmath.btoi(star_value, 103, 1)
				+Spmath.btoi(star_value, 105, 2)*0.01f);
		
		if(this.Mag > StellarSky.proxy.getClientSettings().mag_Limit-ExtinctionRefraction.ext_coeff_Vf)
			unable=true;
		
		B_V=Spmath.sgnize(star_value[109],
				(float)Spmath.btoi(star_value, 110, 1)
				+Spmath.btoi(star_value, 112, 2)*0.01f);
		
		//J2000
		this.RA=Spmath.btoi(star_value, 75, 2)*15.0f
				+Spmath.btoi(star_value, 77, 2)/4.0f
				+Spmath.btoi(star_value, 79, 2)/240.0f
				+Spmath.btoi(star_value, 82, 1)/2400.0f;
		
		this.Dec=Spmath.sgnize(star_value[83],
				Spmath.btoi(star_value, 84, 2)
				+Spmath.btoi(star_value, 86, 2)/60.0f
				+Spmath.btoi(star_value, 88, 2)/3600.0f);
		
		EcRPos.set(EqtoEc.transform((IValRef)new SpCoord(RA, Dec).getVec()));
		
		star_value=null;
	}
}
