package stellarium.util;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class RenderUtil {
	public static void renderFullQuad() {
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder buff = tess.getBuffer();

		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();

		GlStateManager.disableCull();

		buff.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		buff.pos(-1.0, 1.0, 0.0).tex(0.0, 1.0).endVertex();
		buff.pos(-1.0, -1.0, 0.0).tex(0.0, 0.0).endVertex();
		buff.pos(1.0, -1.0, 0.0).tex(1.0, 0.0).endVertex();
		buff.pos(1.0, 1.0, 0.0).tex(1.0, 1.0).endVertex();
		tess.draw();

		GlStateManager.enableCull();

		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.popMatrix();
	}
}
