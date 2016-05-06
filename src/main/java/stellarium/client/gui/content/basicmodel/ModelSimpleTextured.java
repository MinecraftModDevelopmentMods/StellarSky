package stellarium.client.gui.content.basicmodel;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import stellarium.client.gui.content.IRectangleBound;
import stellarium.client.gui.content.IRenderModel;

public class ModelSimpleTextured implements IRenderModel {
	
	private ResourceLocation location;
	
	public ModelSimpleTextured(ResourceLocation location) {
		this.location = location;
	}

	@Override
	public void renderModel(String info, IRectangleBound totalBound, IRectangleBound clipBound, Tessellator tessellator,
			TextureManager textureManager) {
		float leftX = clipBound.getLeftX();
		float upY = clipBound.getUpY();
		float rightX = clipBound.getRightX();
		float downY = clipBound.getDownY();
		
		float minU = totalBound.getRatioX(leftX);
		float minV = totalBound.getRatioY(upY);
		float maxU = totalBound.getRatioX(rightX);
		float maxV = totalBound.getRatioY(downY);
		
		textureManager.bindTexture(this.location);
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(leftX), (double)(downY), 0.0, minU, maxV);
        tessellator.addVertexWithUV((double)(rightX), (double)(downY), 0.0, maxU, maxV);
        tessellator.addVertexWithUV((double)(rightX), (double)(upY), 0.0, maxU, minV);
        tessellator.addVertexWithUV((double)(leftX), (double)(upY), 0.0, minU, minV);
        tessellator.draw();
	}

}
