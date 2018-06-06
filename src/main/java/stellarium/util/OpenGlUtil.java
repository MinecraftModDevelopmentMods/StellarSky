package stellarium.util;

import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.ARBPixelBufferObject;
import org.lwjgl.opengl.ARBPointSprite;
import org.lwjgl.opengl.ARBTextureFloat;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;

public class OpenGlUtil {
	
	private static final int POINT_SPRITE;
	public static final int PIXEL_PACK_BUFFER;
	public static final int RGB16F;
	public static final int TEXTURE_FLOAT;

	public static final boolean FRAMEBUFFER_SUPPORTED;
	public static final int FRAMEBUFFER_GL;
	public static final int FRAMEBUFFER_BINDING;

	public static final int RENDERBUFFER_GL;

	public static final int COLOR_ATTACHMENT0;
	public static final int DEPTH_ATTACHMENT;
	public static final int STENCIL_ATTACHMENT;

	private static enum FramebufferMode { CORE, ARB, EXT; }
	public static final FramebufferMode MODE;

	static {
		ContextCapabilities caps = GLContext.getCapabilities();
		POINT_SPRITE = caps.OpenGL20? GL20.GL_POINT_SPRITE : ARBPointSprite.GL_POINT_SPRITE_ARB;
		PIXEL_PACK_BUFFER = caps.OpenGL21? GL21.GL_PIXEL_PACK_BUFFER : ARBPixelBufferObject.GL_PIXEL_PACK_BUFFER_ARB;

		RGB16F = caps.GL_ARB_texture_float?
				(caps.OpenGL30? GL30.GL_RGB16F : ARBTextureFloat.GL_RGB16F_ARB)
				: GL11.GL_RGB16;
		TEXTURE_FLOAT = caps.GL_ARB_texture_float? GL11.GL_FLOAT : GL11.GL_UNSIGNED_SHORT;

		if(caps.OpenGL30) {
			FRAMEBUFFER_SUPPORTED = true;
			FRAMEBUFFER_GL = GL30.GL_FRAMEBUFFER;
			FRAMEBUFFER_BINDING = GL30.GL_DRAW_FRAMEBUFFER_BINDING;

			COLOR_ATTACHMENT0 = GL30.GL_COLOR_ATTACHMENT0;
			DEPTH_ATTACHMENT = GL30.GL_DEPTH_ATTACHMENT;
			STENCIL_ATTACHMENT = GL30.GL_STENCIL_ATTACHMENT;

			RENDERBUFFER_GL = GL30.GL_RENDERBUFFER;

			MODE = FramebufferMode.CORE;
		} else if(caps.GL_ARB_framebuffer_object) {
			FRAMEBUFFER_SUPPORTED = true;
			FRAMEBUFFER_GL = ARBFramebufferObject.GL_FRAMEBUFFER;
			FRAMEBUFFER_BINDING = ARBFramebufferObject.GL_DRAW_FRAMEBUFFER_BINDING;

			COLOR_ATTACHMENT0 = ARBFramebufferObject.GL_COLOR_ATTACHMENT0;
			DEPTH_ATTACHMENT = ARBFramebufferObject.GL_DEPTH_ATTACHMENT;
			STENCIL_ATTACHMENT = ARBFramebufferObject.GL_STENCIL_ATTACHMENT;

			RENDERBUFFER_GL = ARBFramebufferObject.GL_RENDERBUFFER;

			MODE = FramebufferMode.ARB;
		} else if(caps.GL_EXT_framebuffer_object) {
			FRAMEBUFFER_SUPPORTED = true;
			FRAMEBUFFER_GL = EXTFramebufferObject.GL_FRAMEBUFFER_EXT;
			FRAMEBUFFER_BINDING = EXTFramebufferObject.GL_FRAMEBUFFER_BINDING_EXT;

			COLOR_ATTACHMENT0 = EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT;
			DEPTH_ATTACHMENT =  EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT;
			STENCIL_ATTACHMENT = EXTFramebufferObject.GL_STENCIL_ATTACHMENT_EXT;

			RENDERBUFFER_GL = EXTFramebufferObject.GL_RENDERBUFFER_EXT;

			MODE = FramebufferMode.EXT;
		} else {
			FRAMEBUFFER_SUPPORTED = false;
			FRAMEBUFFER_GL = 0;
			FRAMEBUFFER_BINDING = 0;

			COLOR_ATTACHMENT0 = 0;
			DEPTH_ATTACHMENT = 0;
			STENCIL_ATTACHMENT = 0;

			RENDERBUFFER_GL = 0;

			MODE = null;
		}
	}

	public static void enablePointSprite() {
		GL11.glEnable(POINT_SPRITE);
	}

	public static void disablePointSprite() {
		GL11.glDisable(POINT_SPRITE);
	}


	public static int genFramebuffers() {
		if(!FRAMEBUFFER_SUPPORTED) return -1;

		switch(MODE) {
		case CORE:
			return GL30.glGenFramebuffers();
		case ARB:
			return ARBFramebufferObject.glGenFramebuffers();
		case EXT:
			return EXTFramebufferObject.glGenFramebuffersEXT();
		default:
			return -1;
		}
	}

	public static void deleteFramebuffers(int framebufferIn)
	{
		if (FRAMEBUFFER_SUPPORTED) {
			switch (MODE) {
			case CORE:
				GL30.glDeleteFramebuffers(framebufferIn);
				break;
			case ARB:
				ARBFramebufferObject.glDeleteFramebuffers(framebufferIn);
				break;
			case EXT:
				EXTFramebufferObject.glDeleteFramebuffersEXT(framebufferIn);
			}
		}
	}

	public static void bindFramebuffer(int target, int framebuffer) {
		if (FRAMEBUFFER_SUPPORTED) {
			switch (MODE) {
			case CORE:
				GL30.glBindFramebuffer(target, framebuffer);
				break;
			case ARB:
				ARBFramebufferObject.glBindFramebuffer(target, framebuffer);
				break;
			case EXT:
				EXTFramebufferObject.glBindFramebufferEXT(target, framebuffer);
				break;
			}
		}
	}

	public static void framebufferTexture2D(int target, int attachment, int textarget, int texture, int level) {
        if (FRAMEBUFFER_SUPPORTED)
        {
            switch (MODE)
            {
                case CORE:
                    GL30.glFramebufferTexture2D(target, attachment, textarget, texture, level);
                    break;
                case ARB:
                    ARBFramebufferObject.glFramebufferTexture2D(target, attachment, textarget, texture, level);
                    break;
                case EXT:
                    EXTFramebufferObject.glFramebufferTexture2DEXT(target, attachment, textarget, texture, level);
                    break;
            }
        }
	}


	public static int genRenderbuffers() {
		if(!FRAMEBUFFER_SUPPORTED) return -1;

		switch(MODE) {
		case CORE:
			return GL30.glGenRenderbuffers();
		case ARB:
			return ARBFramebufferObject.glGenRenderbuffers();
		case EXT:
			return EXTFramebufferObject.glGenRenderbuffersEXT();
		default:
			return -1;
		}
	}

	public static void deleteRenderbuffers(int renderbuffer)
	{
		if (FRAMEBUFFER_SUPPORTED) {
			switch (MODE) {
			case CORE:
				GL30.glDeleteRenderbuffers(renderbuffer);
				break;
			case ARB:
				ARBFramebufferObject.glDeleteRenderbuffers(renderbuffer);
				break;
			case EXT:
				EXTFramebufferObject.glDeleteRenderbuffersEXT(renderbuffer);
				break;
			}
		}
	}

	public static void bindRenderbuffer(int target, int renderbuffer) {
		if (FRAMEBUFFER_SUPPORTED) {
			switch (MODE) {
			case CORE:
				GL30.glBindRenderbuffer(target, renderbuffer);
				break;
			case ARB:
				ARBFramebufferObject.glBindRenderbuffer(target, renderbuffer);
				break;
			case EXT:
				EXTFramebufferObject.glBindRenderbufferEXT(target, renderbuffer);
			}
		}
	}

	public static void renderbufferStorage(int target, int internalFormat, int width, int height)
	{
		if (FRAMEBUFFER_SUPPORTED) {
			switch (MODE) {
			case CORE:
				GL30.glRenderbufferStorage(target, internalFormat, width, height);
				break;
			case ARB:
				ARBFramebufferObject.glRenderbufferStorage(target, internalFormat, width, height);
				break;
			case EXT:
				EXTFramebufferObject.glRenderbufferStorageEXT(target, internalFormat, width, height);
				break;
			}
		}
	}

	public static void framebufferRenderbuffer(int target, int attachment, int renderBufferTarget, int renderBuffer)
	{
		if (FRAMEBUFFER_SUPPORTED) {
			switch (MODE) {
			case CORE:
				GL30.glFramebufferRenderbuffer(target, attachment, renderBufferTarget, renderBuffer);
				break;
			case ARB:
				ARBFramebufferObject.glFramebufferRenderbuffer(target, attachment, renderBufferTarget, renderBuffer);
				break;
			case EXT:
				EXTFramebufferObject.glFramebufferRenderbufferEXT(target, attachment, renderBufferTarget, renderBuffer);
				break;
			}
		}
	}

	public static void generateMipmap(int target) {
		if (FRAMEBUFFER_SUPPORTED) {
			switch (MODE) {
			case CORE:
				GL30.glGenerateMipmap(target);
				break;
			case ARB:
				ARBFramebufferObject.glGenerateMipmap(target);
				break;
			case EXT:
				EXTFramebufferObject.glGenerateMipmapEXT(target);
				break;
			}
		}
	}
}
