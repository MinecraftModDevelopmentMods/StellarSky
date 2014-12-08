package stellarium.viewrender.render;

import org.lwjgl.opengl.GL11;

import stellarium.stellars.Color;

public class RFuzzy extends RCShape {
	public void SetColor(Color c){
		color=c;
	}
	
	Color color;

	@Override
	public void render() {
		CRenderEngine cre = CRenderEngine.instance;
		cre.bindTexture("star.png");
		cre.useShader("fuzzy");
		cre.setValue("lum", Lum * cre.con);
		cre.setValue("color", color);
		cre.setValue("size", Size);
		
	   	GL11.glBegin(GL11.GL_POINT);
	   	GL11.glVertex3d(Pos.getCoord(0).asDouble(), Pos.getCoord(1).asDouble(), Pos.getCoord(2).asDouble()); 
		GL11.glEnd();
	}

}
