package stellarium.viewrender.render;

import org.lwjgl.opengl.GL11;

import stellarium.stellars.Color;

//Rendering Deep Sky Objects
public class RDSObj extends RBase {
	
	public double Size;
	public String ImgLoc;
	
	public RDSObj SetSize(double size){
		Size=size;
		return this;
	}
	
	public RDSObj SetImgLoc(String imgloc){
		ImgLoc=imgloc;
		return this;
	}

	@Override
	public void render() {
		CRenderEngine cre = CRenderEngine.instance;
		
		if(Size < cre.res){
			cre.bindTexture("star.png");
			cre.useShader("pointy");
			cre.setValue("lum", Lum * cre.con);
			cre.setValue("color", new Color());
			
		   	GL11.glBegin(GL11.GL_POINT);
		   	GL11.glVertex3d(Pos.getCoord(0).asDouble(), Pos.getCoord(1).asDouble(), Pos.getCoord(2).asDouble()); 
			GL11.glEnd();
		}
			
		else{
			cre.bindTexture(ImgLoc);
			cre.useShader("dsobj");
			cre.setValue("lum", Lum * cre.con);
			cre.setValue("size", Size);
		
			GL11.glBegin(GL11.GL_POINT);
		   	GL11.glVertex3d(Pos.getCoord(0).asDouble(), Pos.getCoord(1).asDouble(), Pos.getCoord(2).asDouble()); 
			GL11.glEnd();
		}
	}

}
