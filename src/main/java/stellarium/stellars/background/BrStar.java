package stellarium.stellars.background;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import net.minecraft.client.Minecraft;
import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EVector;
import sciapi.api.value.euclidian.IEVector;
import stellarium.stellars.ExtinctionRefraction;
import stellarium.stellars.StellarManager;
import stellarium.util.math.SpCoordf;
import stellarium.util.math.Spmath;
import stellarium.util.math.Transforms;

public class BrStar extends Star {
	
	public boolean unable;
	//constants
	public static final int NumStar=9110;
	public static final int Bufsize=198;
	
	//Magnitude
	public float Mag;
	
	//Apparent Magnitude
	public float App_Mag;
	
	//B-V Value
	public float B_V;
	
	//Apparant B-V
	public float App_B_V;
	
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
		IValRef pvec=Transforms.ZTEctoNEc.transform((IEVector)EcRPos);
		pvec=Transforms.EctoEq.transform(pvec);
		pvec=Transforms.NEqtoREq.transform(pvec);
		pvec=Transforms.REqtoHor.transform(pvec);
		return pvec;
	}
	
	public IValRef<EVector> GetAtmPosf(){
		return ExtinctionRefraction.Refraction(GetPositionf(), true);
	}

	@Override
	public void Update() {
		if(Mag>StellarManager.Mag_Limit) this.unable=true;
		AppPos.set(GetAtmPosf());
		float Airmass=(float) ExtinctionRefraction.Airmass(AppPos, true);
    	App_Mag= (Mag+Airmass*ExtinctionRefraction.ext_coeff_Vf);
    	App_B_V= (B_V+Airmass*ExtinctionRefraction.ext_coeff_B_Vf);
	}
	
	
	//Load Stars
	public static final void InitializeAll() throws IOException{
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
	    	stars[i].Initialize();
	    }
	    str=null;
	    
	    System.out.println("[Stellarium]: "+"Bright Stars are Loaded!");
	    IsInitialized=true;
	}
	
	public static void UpdateAll(){
		int i;
		for(i=0; i<NumStar; i++)
			if(!stars[i].unable) stars[i].Update();
	}
	
	//Initialization in each star
	public void Initialize(){
		float RA, Dec;
		
		if(star_value[103]==' '){
			this.unable=true;
			return;
		}
		else this.unable=false;
		
		Mag=Spmath.sgnize(star_value[102],
				(float)Spmath.btoi(star_value, 103, 1)
				+Spmath.btoi(star_value, 105, 2)*0.01f);
		
		if(Mag>StellarManager.Mag_Limit-ExtinctionRefraction.ext_coeff_Vf)
			unable=true;
		
		B_V=Spmath.sgnize(star_value[109],
				(float)Spmath.btoi(star_value, 110, 1)
				+Spmath.btoi(star_value, 112, 2)*0.01f);
		
		//J2000
		RA=Spmath.btoi(star_value, 75, 2)*15.0f
				+Spmath.btoi(star_value, 77, 2)/4.0f
				+Spmath.btoi(star_value, 79, 2)/240.0f
				+Spmath.btoi(star_value, 82, 1)/2400.0f;
		
		Dec=Spmath.sgnize(star_value[83],
				Spmath.btoi(star_value, 84, 2)
				+Spmath.btoi(star_value, 86, 2)/60.0f
				+Spmath.btoi(star_value, 88, 2)/3600.0f);
		
		EcRPos.set((IValRef)Transforms.EqtoEc.transform((IValRef)new SpCoordf(RA, Dec).getVec()));
		
		star_value=null;
	}
}
