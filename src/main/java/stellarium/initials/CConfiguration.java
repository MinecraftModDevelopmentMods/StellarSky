package stellarium.initials;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.Scanner;

import stellarium.stellars.background.BrStar;

import com.google.common.io.Files;

import cpw.mods.fml.relauncher.FMLInjectionData;

public class CConfiguration {
	protected File file = null;
	protected boolean NeedCreate = false;
	
	public CConfiguration(String name) throws IOException{
		File mcdir = (File)FMLInjectionData.data()[6];
		File stconf = new File(mcdir.getPath() + File.separator + "config" + File.separator + "Stellarium");
		
		file = new File(stconf, name + ".cfg");
		
		if(file.getParentFile() != null){
			file.getParentFile().mkdirs();
		}
		
		if(!file.exists()){
			file.createNewFile();
			NeedCreate = true;
		}
	}
	
	public byte[] Extract() throws IOException{
		byte[] result = Files.toByteArray(file);
		
		return result;
	}
	
	public void Initialize(byte[] b) throws IOException{
		FileOutputStream fo = new FileOutputStream(file);
		fo.write(b);
		fo.close();
	}
	
	public void Initialize(String str) throws IOException{
		Initialize(str.getBytes());
	}
	
	public void InitializebyFile(String filedir, int MaxRes) throws IOException{
		byte[] str = new byte[MaxRes];
		byte[] in;
		int i = 0;
		
		InputStream is = CConfiguration.class.getResourceAsStream("/"+filedir);
	    
	    Scanner scan = new Scanner(is);
	    
	    while(scan.hasNextByte())
	    	str[i++] = scan.nextByte();
	    
	    in = new byte[i];
	    
	    for(int j = 0; j < i; j++)
	    	in[j] = str[j];
	    
	    scan.close();
	    
	    Initialize(str);
	}
}
