package stellarium.client.gui.content.basicmodel;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import stellarium.client.gui.content.IRectangleBound;
import stellarium.client.gui.content.IRenderModel;
import stellarium.client.gui.content.util.GuiUtil;

public class ModelSimpleRect implements IRenderModel {
	
	private static final ModelSimpleRect instance = new ModelSimpleRect();
	
	public static ModelSimpleRect getInstance() {
		return instance;
	}
	
	private float red, green, blue, alpha;

	@Override
	public void renderModel(String info, IRectangleBound totalBound, IRectangleBound clipBound, Tessellator tessellator,
			TextureManager textureManager) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		float posX = clipBound.getLeftX();
		float posY = clipBound.getUpY();
		float width = clipBound.getWidth();
		float height = clipBound.getHeight();
		
        tessellator.startDrawingQuads();
        if(alpha != 1.0f)
        	tessellator.setColorRGBA_F(red, green, blue, alpha);
        tessellator.addVertex((double)(posX), (double)(posY + height), 0.0);
        tessellator.addVertex((double)(posX + width), (double)(posY + height), 0.0);
        tessellator.addVertex((double)(posX + width), (double)(posY), 0.0);
        tessellator.addVertex((double)(posX), (double)(posY), 0.0);
        tessellator.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		this.red = this.green = this.blue = this.alpha = 1.0f;
	}

	public void setColor(float red, float green, float blue, float alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}

}
