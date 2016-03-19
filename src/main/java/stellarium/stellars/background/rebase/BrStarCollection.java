package stellarium.stellars.background.rebase;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.google.common.collect.Lists;

import stellarium.stellars.base.IStellarCollection;
import stellarium.stellars.base.StellarObject;
import stellarium.stellars.view.IStellarViewpoint;

public class BrStarCollection implements IStellarCollection {
	
	//constants
	public static final int NumStar=9110;
	public static final int Bufsize=198;

	//Initialization check
	private boolean IsInitialized=false;
	
	//File
	private byte str[];
	
	//stars
	private List<BrStar> stars = Lists.newArrayList();

	@Override
	public void initialize() throws IOException {
		
		//Counter Variable
		int i, j, k;
		
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
	    	BrStar star = new BrStar();
	    	star.star_value = new byte[Bufsize];
	    	
	    	k=0;
	    	while(str[j]!='\n'){
	    		star.star_value[k]=str[j];
	    		j++;
	    		k++;
	    	}
	    	j++;
	    	
	    	if(star.initialize())
	    		stars.add(star);
	    }
	    
	    str=null;
	    
	    System.out.println("[Stellarium]: "+"Bright Stars are Loaded!");
	    IsInitialized=true;
	}

	@Override
	public List<? extends StellarObject> getObjects() {
		return this.stars;
	}

}
