package stellarium.render.stellars.atmosphere;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarium.lib.render.IGenericRenderer;
import stellarium.render.shader.ShaderHelper;
import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.phased.StellarRenderInformation;
import stellarium.util.OpenGlVersionUtil;
import stellarium.util.math.Allocator;

public enum AtmosphereRenderer implements IGenericRenderer<AtmosphereSettings, EnumAtmospherePass, AtmosphereModel, StellarRenderInformation> {
	
	INSTANCE;

	private static final int STRIDE_IN_FLOAT = 8;

	private Framebuffer dominateCache = null;
	private FloatBuffer renderBuffer;
	private ByteBuffer indicesBuffer;
	private ByteBuffer toTest = null;

	private int renderToCacheList = -1;
	private int renderedList = -1;
	
	private boolean previousFlag;
	private boolean cacheChangedFlag = false;

	private int prevFramebufferBound;
	
	private AtmShaderManager shaderManager;
	
	AtmosphereRenderer() {
		this.shaderManager = new AtmShaderManager();
	}
	
	@Override
	public void initialize(AtmosphereSettings settings) {
		shaderManager.reloadShaders();
		
		if(!settings.checkChange())
			return;

		if(dominateCache != null)
			dominateCache.deleteFramebuffer();

		this.dominateCache = new FramebufferCustom(settings.cacheLong, settings.cacheLat, true);
		if(settings.isInterpolated) {
			dominateCache.setFramebufferFilter(GL11.GL_LINEAR);
		}
		dominateCache.unbindFramebuffer();
		
		int renderBufferNewSize = (settings.fragLong + 1) * (settings.fragLat + 1) * STRIDE_IN_FLOAT;
		if(this.renderBuffer == null || renderBuffer.capacity() < renderBufferNewSize)
			this.renderBuffer = ByteBuffer.allocateDirect(renderBufferNewSize << 2).order(ByteOrder.nativeOrder()).asFloatBuffer();

		int indicesBufferNewSize = (settings.fragLong * settings.fragLat * 4) << 2; // 4 Indices for Quad
		if(this.indicesBuffer == null || indicesBuffer.capacity() < indicesBufferNewSize)
			this.indicesBuffer = ByteBuffer.allocateDirect(indicesBufferNewSize).order(ByteOrder.nativeOrder()); 

		this.setupIndicesBuffer(settings.fragLong, settings.fragLat);

		this.cacheChangedFlag = true;
	}

	@Override
	public void preRender(AtmosphereSettings settings, StellarRenderInformation info) {
		if(this.cacheChangedFlag || this.previousFlag != info.isFrameBufferEnabled) {
			this.reallocList(settings, info.isFrameBufferEnabled, info.deepDepth);
			this.previousFlag = info.isFrameBufferEnabled;
			this.cacheChangedFlag = false;
		}
		
		shaderManager.updateWorldInfo(info);
	}

	@Override
	public void renderPass(AtmosphereModel model, EnumAtmospherePass pass, StellarRenderInformation info) {
		switch(pass) {
		case PrepareDominateScatter:			
			if(info.isFrameBufferEnabled) {
				this.prevFramebufferBound = GL11.glGetInteger(OpenGlVersionUtil.framebufferBinding());			
				dominateCache.bindFramebuffer(true);

		        GlStateManager.clearColor(0.0f, 0.0f, 0.0f, 1.0f);
		        GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		        GlStateManager.matrixMode(GL11.GL_PROJECTION);
		        GlStateManager.pushMatrix();
		        GlStateManager.loadIdentity();
		        GlStateManager.ortho(0, 2 * Math.PI, -Math.PI/2, Math.PI/2, -1.0, 1.0);
				GlStateManager.matrixMode(GL11.GL_MODELVIEW);
				GlStateManager.pushMatrix();
				GlStateManager.loadIdentity();
				info.setAtmCallList(this.renderToCacheList);
			}
			else info.setAtmCallList(this.renderedList);

			info.setActiveShader(shaderManager.bindShader(model, EnumStellarPass.DominateScatter));
			break;
		case FinalizeDominateScatter:
			if(info.isFrameBufferEnabled) {
				GlStateManager.matrixMode(GL11.GL_PROJECTION);
				GlStateManager.popMatrix();
				GlStateManager.matrixMode(GL11.GL_MODELVIEW);
				GlStateManager.popMatrix();

				Framebuffer mcBuffer = info.minecraft.getFramebuffer();

				GlStateManager.viewport(0, 0, mcBuffer.framebufferWidth, mcBuffer.framebufferHeight);
	            OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, this.prevFramebufferBound);
			}
			info.setActiveShader(null);
			break;
		
		case RenderCachedDominate:
			ShaderHelper.getInstance().releaseCurrentShader();

			dominateCache.bindFramebufferTexture();

			GlStateManager.callList(this.renderedList);

			dominateCache.unbindFramebufferTexture();

			break;
		
		case TestAtmCache:
			dominateCache.bindFramebufferTexture();
			GlStateManager.pushMatrix();
			GlStateManager.translate(100, 50, 0);
			//GuiUtil.drawTexturedRectSimple(0, 0, -100, -50);
			GlStateManager.popMatrix();
			dominateCache.unbindFramebufferTexture();
			break;

		case SetupOpaque:
			info.setActiveShader(shaderManager.bindShader(model, EnumStellarPass.Opaque));
			break;
		case SetupOpaqueScatter:
			info.setActiveShader(shaderManager.bindShader(model, EnumStellarPass.OpaqueScatter));
			break;
		case SetupPointScatter:
			info.setActiveShader(shaderManager.bindShader(model, EnumStellarPass.PointScatter));
			break;
		case SetupSurfaceScatter:
			info.setActiveShader(shaderManager.bindShader(model, EnumStellarPass.SurfaceScatter));
			break;
		
		case BindDomination:
			GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
			this.prevEnabled = GlStateManager.glGetInteger(GL11.GL_TEXTURE_2D);
			if(this.prevEnabled == GL11.GL_FALSE)
				GlStateManager.enableTexture2D();

			this.prevTexture = GlStateManager.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
			GlStateManager.bindTexture(dominateCache.framebufferTexture);

			GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
			break;
		case UnbindDomination:
			GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
			
			GlStateManager.bindTexture(this.prevTexture);
			
			if(this.prevEnabled == GL11.GL_FALSE)
				GlStateManager.disableTexture2D();
			
			GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

			break;
		default:
			break;
		}
	}
	
	private int prevEnabled = GL11.GL_FALSE;
	private int prevTexture = 0;
	
	@Override
	public void postRender(AtmosphereSettings settings, StellarRenderInformation info) {

	}


	public void reallocList(AtmosphereSettings settings, boolean isFramebufferEnabled, double deepDepth) {
		Vector3[][] displayvec = Allocator.createAndInitialize(settings.fragLong + 1, settings.fragLat+1);

		for(int longc=0; longc<=settings.fragLong; longc++)
			for(int latc=0; latc<=settings.fragLat; latc++)
				displayvec[longc][latc].set(new SpCoord(longc*360.0/settings.fragLong, 180.0 * latc / settings.fragLat - 90.0).getVec());
        
		if(this.renderedList != -1)
			GLAllocation.deleteDisplayLists(this.renderedList);
		
        this.renderedList = GLAllocation.generateDisplayLists(
        		isFramebufferEnabled? 1 : 2);
        
        GlStateManager.glNewList(this.renderedList, GL11.GL_COMPILE);
        this.drawDisplay(displayvec, settings.fragLong, settings.fragLat, deepDepth, isFramebufferEnabled, true);
        GlStateManager.glEndList();
        
        if(isFramebufferEnabled) {
        	this.renderToCacheList = this.renderedList + 1;
        	
    		for(int longc=0; longc<=settings.fragLong; longc++)
    			for(int latc=0; latc<=settings.fragLat; latc++)
    				displayvec[longc][latc].set(longc * 2.0 * Math.PI / settings.fragLong, latc * Math.PI / settings.fragLat - Math.PI / 2, 0.0);
        	
    		GlStateManager.glNewList(this.renderToCacheList, GL11.GL_COMPILE);
    		GL11.glFrontFace(GL11.GL_CW);
            this.drawDisplay(displayvec, settings.fragLong, settings.fragLat, 1.0, false, false);
            GL11.glFrontFace(GL11.GL_CCW);
            GlStateManager.glEndList();
        }
	}
	
	Vector3 temporal = new Vector3();
	
	private void setupIndicesBuffer(int fragLong, int fragLat) {
		indicesBuffer.clear();
		for(int longc=0; longc < fragLong; longc++) {
			for(int latc=0; latc < fragLat; latc++) {
				indicesBuffer.putInt(((fragLat + 1) * longc + latc));
				indicesBuffer.putInt(((fragLat + 1) * longc + latc + 1));
				indicesBuffer.putInt(((fragLat + 1) * (longc + 1) + latc + 1));
				indicesBuffer.putInt(((fragLat + 1) * (longc + 1) + latc));
			}
		}
	}
	
	private void drawDisplay(Vector3[][] displayvec, int fragLong, int fragLat, double length, boolean hasTexture, boolean hasNormal) {
		renderBuffer.clear();

		short longc = 0, latc;
		for(Vector3[] vertRow : displayvec) {
			latc = 0;
			for(Vector3 pos : vertRow) {
				temporal.set(pos).scale(length);
				renderBuffer.put((float)temporal.getX());
				renderBuffer.put((float)temporal.getY());
				renderBuffer.put((float)temporal.getZ());
				
				renderBuffer.put(((float)longc) / fragLong);
				renderBuffer.put(((float)latc) / fragLat);
				
				renderBuffer.put((float)pos.getX());
				renderBuffer.put((float)pos.getY());
				renderBuffer.put((float)pos.getZ());
				
				latc++;
			}
			longc++;
		}

		renderBuffer.position(0);
		GL11.glVertexPointer(3, STRIDE_IN_FLOAT << 2, this.renderBuffer);
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);

		if(hasTexture) {
			renderBuffer.position(3);
			GL11.glTexCoordPointer(2, STRIDE_IN_FLOAT << 2, this.renderBuffer);
			GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		}

		if(hasNormal) {
			renderBuffer.position(5);
			GL11.glNormalPointer(STRIDE_IN_FLOAT << 2, this.renderBuffer);
			GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
		}

		indicesBuffer.position(0);
		GL11.glDrawElements(GL11.GL_QUADS, fragLong * fragLat * 4, GL11.GL_UNSIGNED_INT, this.indicesBuffer);

		if(hasNormal) {
			GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
		}

		if(hasTexture) {
			GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		}
		
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
	}
}
