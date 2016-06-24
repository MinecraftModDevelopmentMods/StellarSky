package stellarium.util;

import org.lwjgl.opengl.ARBPointSprite;
import org.lwjgl.opengl.ARBTextureFloat;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;

import net.minecraft.client.renderer.GlStateManager;

public class OpenGlVersionUtil {
	
	private static int spriteFlag;
	private static int rgb16f;
	
	static {
		ContextCapabilities contextcapabilities = GLContext.getCapabilities();
		spriteFlag = contextcapabilities.OpenGL20? GL20.GL_POINT_SPRITE : ARBPointSprite.GL_POINT_SPRITE_ARB;
		rgb16f = contextcapabilities.OpenGL30? GL30.GL_RGB16F :
			(contextcapabilities.GL_ARB_texture_float? ARBTextureFloat.GL_RGB16F_ARB : GL11.GL_RGB);
	}

	public static void enablePointSprite() {
		GL11.glEnable(spriteFlag);
	}
	
	public static void disablePointSprite() {
		GL11.glDisable(spriteFlag);
	}
	
	public static int rgb16f() {
		return rgb16f;
	}
}
