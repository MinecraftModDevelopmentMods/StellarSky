package stellarium.viewrender.render;

import stellarium.lighting.*;

public abstract class RCSym extends RCShape {
	
	CShade shades[];
	String ImgLoc;
	
	public void SetShade(CShade[] sh){
		shades=sh;
	}
	public void SetImgLoc(String imgloc){
		ImgLoc=imgloc;
	}

	@Override
	public abstract void render();

}
