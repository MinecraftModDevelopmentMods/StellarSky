package stellarium.render.stellars.atmosphere;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import stellarium.util.OpenGlVersionUtil;

// TODO Framebuffer Write custom code
public class FramebufferCustom extends Framebuffer {
	public FramebufferCustom(int width, int height, boolean useDepthIn) {
		super(width, height, useDepthIn);
	}

	private int internalFormat = GL11.GL_RGBA8, format = GL11.GL_RGBA, type = GL11.GL_UNSIGNED_BYTE;

	public void setupFormat(int internalFormat, int format, int type) {
		this.internalFormat = internalFormat;
		this.format = format;
		this.type = type;
        this.createBindFramebuffer(this.framebufferWidth, this.framebufferHeight);
	}

	@Override
	public void createFramebuffer(int width, int height)
	{
		this.framebufferWidth = width;
		this.framebufferHeight = height;
		this.framebufferTextureWidth = width;
		this.framebufferTextureHeight = height;

		if (!OpenGlHelper.isFramebufferEnabled())
		{
			this.framebufferClear();
		}
		else
		{
			this.framebufferObject = OpenGlHelper.glGenFramebuffers();
			this.framebufferTexture = TextureUtil.glGenTextures();

			if (this.useDepth)
			{
				this.depthBuffer = OpenGlHelper.glGenRenderbuffers();
			}

			this.setFramebufferFilter(GL11.GL_NEAREST);
			GlStateManager.bindTexture(this.framebufferTexture);
			GlStateManager.glTexImage2D(GL11.GL_TEXTURE_2D, 0, this.internalFormat, this.framebufferTextureWidth, this.framebufferTextureHeight, 0, this.format, this.type, (IntBuffer)null);
			OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, this.framebufferObject);
			OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, this.framebufferTexture, 0);

			if (this.useDepth)
			{
				OpenGlHelper.glBindRenderbuffer(OpenGlHelper.GL_RENDERBUFFER, this.depthBuffer);
				if (!this.isStencilEnabled())
				{
					OpenGlHelper.glRenderbufferStorage(OpenGlHelper.GL_RENDERBUFFER, 33190, this.framebufferTextureWidth, this.framebufferTextureHeight);
					OpenGlHelper.glFramebufferRenderbuffer(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_DEPTH_ATTACHMENT, OpenGlHelper.GL_RENDERBUFFER, this.depthBuffer);
				}
				else
				{
					OpenGlHelper.glRenderbufferStorage(OpenGlHelper.GL_RENDERBUFFER, org.lwjgl.opengl.EXTPackedDepthStencil.GL_DEPTH24_STENCIL8_EXT, this.framebufferTextureWidth, this.framebufferTextureHeight);
					OpenGlHelper.glFramebufferRenderbuffer(OpenGlHelper.GL_FRAMEBUFFER, org.lwjgl.opengl.EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, OpenGlHelper.GL_RENDERBUFFER, this.depthBuffer);
					OpenGlHelper.glFramebufferRenderbuffer(OpenGlHelper.GL_FRAMEBUFFER, org.lwjgl.opengl.EXTFramebufferObject.GL_STENCIL_ATTACHMENT_EXT, OpenGlHelper.GL_RENDERBUFFER, this.depthBuffer);
				}
			}

			this.framebufferClear();
			this.unbindFramebufferTexture();
		}
    }
}
