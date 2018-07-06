package stellarium.render.stellars.atmosphere;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarium.render.shader.IShaderObject;
import stellarium.render.shader.ShaderHelper;
import stellarium.render.stellars.StellarRI;
import stellarium.render.stellars.layer.LayerRHelper;
import stellarium.render.util.EnumIndexType;
import stellarium.render.util.FramebufferCustom;
import stellarium.render.util.IVertexBuffer;
import stellarium.render.util.VertexBufferEx;
import stellarium.render.util.VertexReferences;
import stellarium.stellars.util.ExtinctionRefraction;
import stellarium.util.OpenGlUtil;
import stellarium.util.math.Allocator;

public enum AtmosphereRenderer {
	INSTANCE;

	private static final int STRIDE_IN_FLOAT = 8;

	private FramebufferCustom stellar = null;
	private int prevWidth = 0, prevHeight = 0;
	private int prevFramebufferBound = -1;

	private boolean vboEnabled;

	private ByteBuffer scrPartIndicesBuffer = null;
	private int scrPartList = -1;
	private VertexBufferEx scrPartBuffer = null;

	private ByteBuffer sphereIndicesBuffer = null;
	private int sphereList = -1;
	private VertexBufferEx sphereBuffer = null;

	private AtmosphereShader atmShader;

	AtmosphereRenderer() {
		this.atmShader = new AtmosphereShader();
	}

	public void initialize(AtmosphereSettings settings) {
		atmShader.reloadShaders();

		if(!settings.checkChange())
			return;

		this.vboEnabled = OpenGlHelper.useVbo();

		int bufferSize = settings.fragLong * settings.fragLat * 4; // 4 Indices for Quad
		if(this.sphereIndicesBuffer == null || sphereIndicesBuffer.capacity() != bufferSize * EnumIndexType.INT.size)
			this.sphereIndicesBuffer = ByteBuffer.allocateDirect(bufferSize * EnumIndexType.INT.size).order(ByteOrder.nativeOrder()); 
		this.setupSphereIndices(settings.fragLong, settings.fragLat);

		settings.fragScreen = 64;
		bufferSize = settings.fragScreen * settings.fragScreen * 4; // 4 Indices for Quad
		if(this.scrPartIndicesBuffer == null || scrPartIndicesBuffer.capacity() != bufferSize * EnumIndexType.INT.size)
			this.scrPartIndicesBuffer = ByteBuffer.allocateDirect(bufferSize * EnumIndexType.INT.size).order(ByteOrder.nativeOrder());
		this.setupScrPartIndices(settings.fragScreen);

		this.reCache(settings, LayerRHelper.DEEP_DEPTH);
	}

	public void setupFramebuffer(int width, int height) {
		if(this.stellar != null)
			stellar.deleteFramebuffer();

		this.stellar = FramebufferCustom.builder()
				.texFormat(OpenGlUtil.RGB32F, GL11.GL_RGB, OpenGlUtil.TEXTURE_FLOAT)
				.depthStencil(true, false)
				.build(width, height);
	}

	public void preRender(AtmosphereSettings settings, StellarRI info) {
		atmShader.updateWorldInfo(info);

		if(this.vboEnabled != OpenGlHelper.useVbo()) {
			this.vboEnabled = OpenGlHelper.useVbo();
			this.reCache(settings, LayerRHelper.DEEP_DEPTH);
		}

		Framebuffer mcBuffer = info.minecraft.getFramebuffer();
		if(mcBuffer.framebufferWidth != this.prevWidth || mcBuffer.framebufferHeight != this.prevHeight) {
			this.setupFramebuffer(mcBuffer.framebufferWidth, mcBuffer.framebufferHeight);
			this.prevWidth = mcBuffer.framebufferWidth;
			this.prevHeight = mcBuffer.framebufferHeight;
		}
	}

	/** Gets refraction amount on boundary */
	public double getBoundaryRefraction(StellarRI info) {
		Entity viewer = info.minecraft.getRenderViewEntity();
		SpCoord coord = new SpCoord(0.0, 0.0);
		double boundHeight = Math.toDegrees(Math.atan(info.relativeHeight / 2));

		double height = coord.y = -viewer.rotationPitch + boundHeight;
		ExtinctionRefraction.refraction(coord, false);
		double ref1 = height - coord.y;

		height = coord.y = -viewer.rotationPitch - boundHeight;
		ExtinctionRefraction.refraction(coord, false);
		double ref2 = height - coord.y;

		return (ref1 + ref2) * 0.5;
	}

	public void render(AtmosphereModel model, EnumAtmospherePass pass, StellarRI info) {
		switch(pass) {
		case Prepare:
			this.prevFramebufferBound = GlStateManager.glGetInteger(OpenGlUtil.FRAMEBUFFER_BINDING);

			stellar.bindFramebuffer(false);
			GlStateManager.depthMask(true);
			stellar.framebufferClear();
			GlStateManager.depthMask(false);

			GlStateManager.pushMatrix();

			// Pitch up to adjust the frustrum in favor of refraction
			double ref = this.getBoundaryRefraction(info);
			Entity viewer = info.minecraft.getRenderViewEntity();
			GlStateManager.rotate(270.0f - viewer.rotationYaw, 0.0f, 0.0f, 1.0f);
			GlStateManager.rotate((float)ref, 0.0f, 1.0f, 0.0f);
			GlStateManager.rotate(-(270.0f - viewer.rotationYaw), 0.0f, 0.0f, 1.0f);
			break;

		case Finalize:
			GlStateManager.popMatrix();

			OpenGlUtil.bindFramebuffer(OpenGlUtil.FRAMEBUFFER_GL, this.prevFramebufferBound);

			// TODO Do this with UVs computed on CPU side & Fix glitch from refraction
			// Apply refraction & Convert to RGBM (Rendering ends here, postprocessing all the way down)
			IShaderObject refractor = atmShader.bindRefractionShader(model);
			refractor.getField("pitch").setDouble(Math.toRadians(-info.minecraft.player.rotationPitch));
			refractor.getField("preRotated").setDouble(Math.toRadians(this.getBoundaryRefraction(info)));
			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
			stellar.bindFramebufferTexture();
			if(this.vboEnabled)
				this.renderScrPart(this.scrPartBuffer);
			else GlStateManager.callList(this.scrPartList);

			ShaderHelper.getInstance().releaseCurrentShader();

			break;

		case SetupDominateScatter:
			// TODO Handle fog and rain correctly - which exists near ground

			// Extinction first - strangely, this drains performance
			GlStateManager.blendFunc(GL11.GL_ZERO, GL11.GL_SRC_COLOR);
			atmShader.bindExtinctionShader(model);
			// MAYBE Faster evaluation of extinction
			if(this.vboEnabled)
				sphereBuffer.drawElements(EnumIndexType.INT, this.sphereIndicesBuffer);
			else GlStateManager.callList(this.sphereList);
			GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);

			// Prepare for dominate rendering
			IShaderObject shader = atmShader.bindAtmShader(model);
			info.setDominateRenderer((lightDir, red, green, blue) -> {
				if(shader != null) { // Dummy check for debug
					shader.getField("lightDir").setDouble3(
							lightDir.getX(), lightDir.getY(), lightDir.getZ());
					shader.getField("lightColor").setDouble3(
							red, green, blue);
				}

				if(this.vboEnabled)
					sphereBuffer.drawElements(EnumIndexType.INT, this.sphereIndicesBuffer);
				else GlStateManager.callList(this.sphereList);
			});
			break;
		default:
			break;
		}
	}

	public void reCache(AtmosphereSettings settings, double deepDepth) {
		Vector3[][] displayvec = Allocator.createAndInitialize(settings.fragLong + 1, settings.fragLat + 1);

		for(int longc=0; longc<=settings.fragLong; longc++)
			for(int latc=0; latc<=settings.fragLat; latc++)
				displayvec[longc][latc].set(new SpCoord(longc*360.0/settings.fragLong, 180.0 * latc / settings.fragLat - 90.0).getVec());


		if(this.scrPartBuffer != null)
			scrPartBuffer.deleteGlBuffers();
		if(this.scrPartList != -1) {
			GLAllocation.deleteDisplayLists(this.scrPartList);
			this.scrPartList = -1;
		}

		if(this.sphereBuffer != null)
			sphereBuffer.deleteGlBuffers();
		if(this.sphereList != -1) {
			GLAllocation.deleteDisplayLists(this.sphereList);
			this.sphereList = -1;
		}

		if(this.vboEnabled) {
			this.scrPartBuffer = new VertexBufferEx();
			this.uploadScrPart(this.scrPartBuffer, settings.fragScreen);

			this.sphereBuffer = new VertexBufferEx();
			this.uploadSphere(this.sphereBuffer,
					displayvec, settings.fragLong, settings.fragLat, deepDepth);
		} else {
			this.scrPartList = GLAllocation.generateDisplayLists(1);
			GlStateManager.glNewList(this.scrPartList, GL11.GL_COMPILE);
			this.uploadScrPart(VertexReferences.getRenderer(), settings.fragScreen);
			this.renderScrPart(VertexReferences.getRenderer());
			GlStateManager.glEndList();

			this.sphereList = GLAllocation.generateDisplayLists(1);
			GlStateManager.glNewList(this.sphereList, GL11.GL_COMPILE);
			this.uploadSphere(VertexReferences.getRenderer(),
					displayvec, settings.fragLong, settings.fragLat, deepDepth);
			VertexReferences.getRenderer().drawElements(EnumIndexType.INT, this.sphereIndicesBuffer);
			GlStateManager.glEndList();
		}
	}


	private void renderScrPart(IVertexBuffer buffer) {
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();

		buffer.drawElements(EnumIndexType.INT, this.scrPartIndicesBuffer);

		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.popMatrix();
	}

	private void setupScrPartIndices(int fragScreen) {
		scrPartIndicesBuffer.clear();
		for(int i=0; i < fragScreen; i++) {
			for(int j=0; j < fragScreen; j++) {
				scrPartIndicesBuffer.putInt(((fragScreen + 1) * i + j));
				scrPartIndicesBuffer.putInt(((fragScreen + 1) * (i + 1) + j));
				scrPartIndicesBuffer.putInt(((fragScreen + 1) * (i + 1) + j + 1));
				scrPartIndicesBuffer.putInt(((fragScreen + 1) * i + j + 1));
			}
		}
	}

	private void uploadScrPart(IVertexBuffer buffer, int fragScreen) {
		BufferBuilder builder = VertexReferences.getBuilder();

		builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		for(int i = 0; i <= fragScreen; i++)
			for(int j = 0; j <= fragScreen; j++) {
				builder.pos(-1.0 + 2.0 * i / fragScreen, -1.0 + 2.0 * j / fragScreen, 0.0);
				builder.tex(1.0 * i / fragScreen, 1.0 * j / fragScreen);
				builder.endVertex();
			}

		builder.finishDrawing();
		buffer.upload(builder);
	}

	private void setupSphereIndices(int fragLong, int fragLat) {
		sphereIndicesBuffer.clear();
		for(int longc=0; longc < fragLong; longc++) {
			for(int latc=0; latc < fragLat; latc++) {
				sphereIndicesBuffer.putInt(((fragLat + 1) * longc + latc));
				sphereIndicesBuffer.putInt(((fragLat + 1) * longc + latc + 1));
				sphereIndicesBuffer.putInt(((fragLat + 1) * (longc + 1) + latc + 1));
				sphereIndicesBuffer.putInt(((fragLat + 1) * (longc + 1) + latc));
			}
		}
	}

	Vector3 temporal = new Vector3();
	private void uploadSphere(IVertexBuffer buffer, Vector3[][] displayvec, int fragLong, int fragLat, double length) {
		BufferBuilder builder = VertexReferences.getBuilder();

		builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);

		short longc = 0, latc;
		for(Vector3[] vertRow : displayvec) {
			latc = 0;
			for(Vector3 pos : vertRow) {
				temporal.set(pos).scale(length);

				builder.pos(temporal.getX(), temporal.getY(), temporal.getZ());
				builder.tex(((double)longc) / fragLong, ((double)latc) / fragLat);
				builder.normal((float)pos.getX(), (float)pos.getY(), (float)pos.getZ());
				builder.endVertex();

				latc++;
			}
			longc++;
		}

		builder.finishDrawing();
		buffer.upload(builder);
	}
}
