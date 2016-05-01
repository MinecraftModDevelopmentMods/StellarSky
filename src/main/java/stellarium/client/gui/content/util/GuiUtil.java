package stellarium.client.gui.content.util;

import net.minecraft.client.renderer.Tessellator;

public class GuiUtil {
	
	public static void drawTexturedRectSimple(int posX, int posY, int width, int height) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(posX + 0), (double)(posY + height), 0.0, 0.0, 1.0);
        tessellator.addVertexWithUV((double)(posX + width), (double)(posY + height), 0.0, 1.0, 1.0);
        tessellator.addVertexWithUV((double)(posX + width), (double)(posY + 0), 0.0, 1.0, 0.0);
        tessellator.addVertexWithUV((double)(posX + 0), (double)(posY + 0), 0.0, 0.0, 0.0);
        tessellator.draw();
	}
	
	public static void drawRectSimple(int posX, int posY, int width, int height) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertex((double)(posX + 0), (double)(posY + height), 0.0);
        tessellator.addVertex((double)(posX + width), (double)(posY + height), 0.0);
        tessellator.addVertex((double)(posX + width), (double)(posY + 0), 0.0);
        tessellator.addVertex((double)(posX + 0), (double)(posY + 0), 0.0);
        tessellator.draw();
	}

}
