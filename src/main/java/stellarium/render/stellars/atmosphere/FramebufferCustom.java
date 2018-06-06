package stellarium.render.stellars.atmosphere;

import java.nio.IntBuffer;

import org.lwjgl.opengl.EXTPackedDepthStencil;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import stellarium.util.OpenGlUtil;

public class FramebufferCustom {
	private int width, height;
	private int textureWidth, textureHeight;
	private int renderX, renderY, renderWidth, renderHeight;

	private int internalFormat, format, type;

	private int minFilter, magFilter;

	private boolean useDepth, useStencil;

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private boolean useDepth = true, useStencil = false;

		private int textureWidth = -1, textureHeight = -1;
		private int internalFormat = GL11.GL_RGBA8, format = GL11.GL_RGBA, type = GL11.GL_UNSIGNED_BYTE;
		private int minFilter = GL11.GL_NEAREST, magFilter = GL11.GL_NEAREST;
		private int renderX = 0, renderY = 0, renderWidth = -1, renderHeight = -1;

		public Builder depthStencil(boolean useDepth, boolean useStencil) {
			this.useDepth = useDepth;
			this.useStencil = useStencil;
			return this;
		}

		public Builder texFormat(int internalFormat, int format, int type) {
			this.internalFormat = internalFormat;
			this.format = format;
			this.type = type;
			return this;
		}

		public Builder texture(int texWidth, int texHeight) {
			this.textureWidth = texWidth;
			this.textureHeight = texHeight;
			return this;
		}

		public Builder texMinMagFilter(int minFilter, int magFilter) {
			this.minFilter = minFilter;
			this.magFilter = magFilter;
			return this;
		}

		public Builder renderRegion(int x, int y, int width, int height) {
			this.renderX = x;
			this.renderY = y;
			this.renderWidth = width;
			this.renderHeight = height;
			return this;
		}

		public FramebufferCustom build(int width, int height) {
			FramebufferCustom built = new FramebufferCustom();
			built.width = width;
			built.height = height;

			built.useDepth = this.useDepth;
			built.useStencil = this.useStencil;

			built.textureWidth = this.textureWidth > 0? this.textureWidth : built.width;
			built.textureHeight = this.textureHeight > 0? this.textureHeight : built.height;

			built.minFilter = this.minFilter;
			built.magFilter = this.magFilter;

			built.renderX = this.renderX;
			built.renderY = this.renderY;
			built.renderWidth = this.renderWidth > 0? this.renderWidth : built.width;
			built.renderHeight = this.renderHeight > 0? this.renderHeight : built.height;

			built.internalFormat = internalFormat;
			built.format = format;
			built.type = type;

			built.createFramebuffer();
			return built;
		}
	}

	private int framebufferPointer = -1;

	private int frameTexturePointer = -1;
	private int depthBufferPointer = -1;

	private float[] clearColor = new float[] {0.0f, 0.0f, 0.0f, 0.0f};
	private float clearDepth = 1.0f;

	private FramebufferCustom() { }

	private void createFramebuffer()
	{
		this.framebufferPointer = OpenGlUtil.genFramebuffers();
		this.frameTexturePointer = TextureUtil.glGenTextures();

		if (this.useDepth) {
			this.depthBufferPointer = OpenGlUtil.genRenderbuffers();
			//this.depthBufferPointer = TextureUtil.glGenTextures();
		}

		GlStateManager.bindTexture(this.frameTexturePointer);
		this.setTexMinMagFilter(this.minFilter, this.magFilter);
		GlStateManager.glTexImage2D(GL11.GL_TEXTURE_2D, 0, this.internalFormat, this.textureWidth, this.textureHeight, 0, this.format, this.type, (IntBuffer)null);
		OpenGlUtil.bindFramebuffer(OpenGlUtil.FRAMEBUFFER_GL, this.framebufferPointer);
		OpenGlUtil.framebufferTexture2D(OpenGlUtil.FRAMEBUFFER_GL, OpenGlUtil.COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, this.frameTexturePointer, 0);

		if (this.useDepth) {
			OpenGlUtil.bindRenderbuffer(OpenGlUtil.RENDERBUFFER_GL, this.depthBufferPointer);
			//GlStateManager.bindTexture(this.depthBufferPointer);
			if (!this.useStencil) {
				OpenGlUtil.renderbufferStorage(OpenGlUtil.RENDERBUFFER_GL, GL14.GL_DEPTH_COMPONENT24, this.textureWidth, this.textureHeight);
				OpenGlUtil.framebufferRenderbuffer(OpenGlUtil.FRAMEBUFFER_GL, OpenGlUtil.DEPTH_ATTACHMENT, OpenGlUtil.RENDERBUFFER_GL, this.depthBufferPointer);
				//GlStateManager.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT24, this.textureWidth, this.textureHeight, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (IntBuffer)null);
				//OpenGlUtil.framebufferTexture2D(OpenGlUtil.FRAMEBUFFER_GL, OpenGlUtil.DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, this.depthBufferPointer, 0);
			} else {
				OpenGlUtil.renderbufferStorage(OpenGlUtil.RENDERBUFFER_GL, EXTPackedDepthStencil.GL_DEPTH24_STENCIL8_EXT, this.textureWidth, this.textureHeight);
				OpenGlUtil.framebufferRenderbuffer(OpenGlUtil.FRAMEBUFFER_GL, OpenGlUtil.DEPTH_ATTACHMENT, OpenGlUtil.RENDERBUFFER_GL, this.depthBufferPointer);
				OpenGlUtil.framebufferRenderbuffer(OpenGlUtil.FRAMEBUFFER_GL, OpenGlUtil.STENCIL_ATTACHMENT, OpenGlUtil.RENDERBUFFER_GL, this.depthBufferPointer);
			}
		}

		this.framebufferClear();
		OpenGlUtil.bindFramebuffer(OpenGlUtil.FRAMEBUFFER_GL, 0);
		GlStateManager.bindTexture(0);
		// TODO AA Check framebuffer complete-ness
	}

	public void deleteFramebuffer() {
		if (this.depthBufferPointer > -1)
		{
			OpenGlUtil.deleteRenderbuffers(this.depthBufferPointer);
			this.depthBufferPointer = -1;
		}

		if (this.frameTexturePointer > -1)
		{
			TextureUtil.deleteTexture(this.frameTexturePointer);
			this.frameTexturePointer = -1;
		}

		if (this.framebufferPointer > -1)
		{
			OpenGlUtil.bindFramebuffer(OpenGlUtil.FRAMEBUFFER_GL, 0);
			OpenGlUtil.deleteFramebuffers(this.framebufferPointer);
			this.framebufferPointer = -1;
		}
	}


	public void bindFramebufferTexture() {
		GlStateManager.bindTexture(this.frameTexturePointer);
	}

	public void unbindFramebufferTexture() {
		GlStateManager.bindTexture(0);
	}

	public void bindFramebuffer(boolean updateViewPort) {
		OpenGlUtil.bindFramebuffer(OpenGlUtil.FRAMEBUFFER_GL, this.framebufferPointer);

		if (updateViewPort) {
			GlStateManager.viewport(this.renderX, this.renderY, this.renderWidth, this.renderHeight);
		}
	}


	public void framebufferClear() {
		GlStateManager.clearColor(this.clearColor[0], this.clearColor[1], this.clearColor[2], this.clearColor[3]);
		int flag = GL11.GL_COLOR_BUFFER_BIT;

		if (this.useDepth) {
			GlStateManager.clearDepth(this.clearDepth);
			flag |= GL11.GL_DEPTH_BUFFER_BIT;
		}

		GlStateManager.clear(flag);
	}


	public void renderFullQuad() {
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder buff = tess.getBuffer();

		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();

		buff.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		buff.pos(-1.0, 1.0, 0.0).tex(
				(double)this.renderX / this.width,
				(double)(this.renderY + this.renderHeight) / this.height).endVertex();
		buff.pos(-1.0, -1.0, 0.0).tex(
				(double)this.renderX / this.width,
				(double)this.renderY / this.height).endVertex();
		buff.pos(1.0, -1.0, 0.0).tex(
				(double)(this.renderX + this.renderWidth) / this.width,
				(double)this.renderY / this.height).endVertex();
		buff.pos(1.0, 1.0, 0.0).tex(
				(double)(this.renderX + this.renderWidth) / this.width,
				(double)(this.renderY + this.renderHeight) / this.height).endVertex();
		tess.draw();

		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.popMatrix();
	}


	public void setClearColor(float red, float green, float blue, float alpha) {
		this.clearColor[0] = red;
		this.clearColor[1] = green;
		this.clearColor[2] = blue;
		this.clearColor[3] = alpha;
	}

	public void setClearDepth(float depth) {
		this.clearDepth = depth;
	}

	private void setTexMinMagFilter(int minFilter, int magFilter) {
		GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, minFilter);
		GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, magFilter);
		GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
		GlStateManager.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
	}

}
