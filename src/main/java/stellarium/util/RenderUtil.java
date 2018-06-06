package stellarium.util;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class RenderUtil {	
	private static final Method GET_FOV_MODIFIER = ReflectionHelper.findMethod(EntityRenderer.class,
			"getFOVModifier", "func_78481_a", float.class, boolean.class);
	private static MethodHandle GFM_HANDLE = null;

	static {
		//GET_FOV_MODIFIER.setAccessible(true);
		try {
			GFM_HANDLE = MethodHandles.lookup().unreflect(GET_FOV_MODIFIER);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public static float getFOVModifier(EntityRenderer renderer, float partialTicks, boolean useFOVSetting) {
		try {
			return (float) GFM_HANDLE.invokeExact(renderer, partialTicks, useFOVSetting);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

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
