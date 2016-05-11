package stellarium.client.overlay.clock.model;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import stellarapi.lib.gui.IRectangleBound;
import stellarapi.lib.gui.IRenderModel;
import stellarapi.lib.gui.model.basic.ModelSimpleRect;
import stellarium.StellarSkyResources;

public class ModelGradientScrollRegion implements IRenderModel {
	
	private ModelSimpleRect selectModel = ModelSimpleRect.getInstance();
	private ResourceLocation resource;
	private float[] leftRGBA = new float[4];
	private float[] rightRGBA = new float[4];
	
	public ModelGradientScrollRegion() {
		this.resource = StellarSkyResources.scrollregion;
	}
	
	public ModelGradientScrollRegion setColorRGBALeft(float red, float green, float blue, float alpha) {
		this.leftRGBA[0] = red;
		this.leftRGBA[1] = green;
		this.leftRGBA[2] = blue;
		this.leftRGBA[3] = alpha;
		return this;
	}
	
	public ModelGradientScrollRegion setColorRGBARight(float red, float green, float blue, float alpha) {
		this.rightRGBA[0] = red;
		this.rightRGBA[1] = green;
		this.rightRGBA[2] = blue;
		this.rightRGBA[3] = alpha;
		return this;
	}

	@Override
	public void renderModel(String info, IRectangleBound totalBound, IRectangleBound clipBound, Tessellator tessellator,
			TextureManager textureManager, float[] color) {
		if(info.equals("select")) {
			color[3] *= 0.2f;
			selectModel.renderModel(info, totalBound, clipBound, tessellator, textureManager, color);
		} else if(info.equals("region")) {
			this.renderGradient(info, totalBound, clipBound, tessellator, textureManager, color);
		}
	}
	
	public void renderGradient(String info, IRectangleBound totalBound, IRectangleBound clipBound, Tessellator tessellator,
			TextureManager textureManager, float[] color) {
		float leftX = clipBound.getLeftX();
		float upY = clipBound.getUpY();
		float rightX = clipBound.getRightX();
		float downY = clipBound.getDownY();
		
		float minU = totalBound.getRatioX(leftX);
		float minV = totalBound.getRatioY(upY);
		float maxU = totalBound.getRatioX(rightX);
		float maxV = totalBound.getRatioY(downY);
		
		textureManager.bindTexture(this.resource);
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(
        		color[0]*(this.rightRGBA[0]*minU+this.leftRGBA[0]*(1.0f-minU)),
        		color[1]*(this.rightRGBA[1]*minU+this.leftRGBA[1]*(1.0f-minU)),
        		color[2]*(this.rightRGBA[2]*minU+this.leftRGBA[2]*(1.0f-minU)),
        		color[3]*(this.rightRGBA[3]*minU+this.leftRGBA[3]*(1.0f-minU)));
        tessellator.addVertexWithUV((double)(leftX), (double)(downY), 0.0, minU, maxV);
        tessellator.setColorRGBA_F(
        		color[0]*(this.rightRGBA[0]*maxU+this.leftRGBA[0]*(1.0f-maxU)),
        		color[1]*(this.rightRGBA[1]*maxU+this.leftRGBA[1]*(1.0f-maxU)),
        		color[2]*(this.rightRGBA[2]*maxU+this.leftRGBA[2]*(1.0f-maxU)),
        		color[3]*(this.rightRGBA[3]*maxU+this.leftRGBA[3]*(1.0f-maxU)));
        tessellator.addVertexWithUV((double)(rightX), (double)(downY), 0.0, maxU, maxV);
        tessellator.addVertexWithUV((double)(rightX), (double)(upY), 0.0, maxU, minV);
        tessellator.setColorRGBA_F(
        		color[0]*(this.rightRGBA[0]*minU+this.leftRGBA[0]*(1.0f-minU)),
        		color[1]*(this.rightRGBA[1]*minU+this.leftRGBA[1]*(1.0f-minU)),
        		color[2]*(this.rightRGBA[2]*minU+this.leftRGBA[2]*(1.0f-minU)),
        		color[3]*(this.rightRGBA[3]*minU+this.leftRGBA[3]*(1.0f-minU)));
        tessellator.addVertexWithUV((double)(leftX), (double)(upY), 0.0, minU, minV);
        
        GL11.glShadeModel(GL11.GL_SMOOTH);
        tessellator.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
	}
}
