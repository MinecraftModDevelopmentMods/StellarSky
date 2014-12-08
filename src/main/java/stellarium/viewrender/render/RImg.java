package stellarium.viewrender.render;

import org.lwjgl.opengl.GL11;

import stellarium.lighting.CShade;

public class RImg extends RCSym {

	@Override
	public void render() {
		CRenderEngine cre = CRenderEngine.instance;
		cre.bindTexture("star.png");
		cre.useShader("img");
		cre.setValue("lum", Lum * cre.con);
		cre.setValue("size", Size);
		
	   	GL11.glBegin(GL11.GL_POINT);
	   	GL11.glVertex3d(Pos.getCoord(0).asDouble(), Pos.getCoord(1).asDouble(), Pos.getCoord(2).asDouble()); 
		GL11.glEnd();
	}

}
