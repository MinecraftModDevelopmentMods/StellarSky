package stellarium.render.stellars.atmosphere;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.shader.Framebuffer;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarium.lib.render.IGenericRenderer;
import stellarium.render.shader.IShaderObject;
import stellarium.render.sky.SkyRenderInformation;
import stellarium.util.math.Allocator;

public class AtmosphereRenderer implements IGenericRenderer<AtmosphereRenderSettings, Void, AtmosphereModel, SkyRenderInformation> {
	
	private static final int STRIDE_IN_FLOAT = 8;
	
	private Framebuffer dominateCache = null;
	private FloatBuffer renderBuffer;
	private ByteBuffer indicesBuffer;
	
	private int renderToCacheList = -1;
	private int renderedList = -1;
	
	private boolean previousFlag;
	private boolean initialFlag = true;
	
	//private AtmShaderManager shaderManager;
	
	public void setupAtmShader(IShaderObject shader) {
		shaderManager.initialize(shader);
	}
	
	@Override
	public void initialize(AtmosphereRenderSettings settings) {
		if(dominateCache != null)
			dominateCache.deleteFramebuffer();

		this.dominateCache = new Framebuffer(settings.cacheLong, settings.cacheLat, false);
		dominateCache.setFramebufferFilter(GL11.GL_LINEAR);
		dominateCache.unbindFramebuffer();
		
		this.renderBuffer = FloatBuffer.allocate(settings.fragLong * settings.fragLat * STRIDE_IN_FLOAT);
		this.indicesBuffer = ByteBuffer.allocate((settings.fragLong * settings.fragLat * 4) << 1); // 4 Indices for Quad
		this.setupIndicesBuffer(settings.fragLong, settings.fragLat);
	}

	@Override
	public void preRender(AtmosphereRenderSettings settings, SkyRenderInformation info) {
		if(this.initialFlag || this.previousFlag != info.isFrameBufferEnabled) {
			this.reallocList(settings, info.isFrameBufferEnabled, info.deepDepth);
			this.previousFlag = info.isFrameBufferEnabled;
			this.initialFlag = false;
		}
		
		shaderManager.updateWorldInfo(info);
	}
	
	public int beginCache() {
		dominateCache.bindFramebuffer(false);

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0, 2.0 * Math.PI, 0.0, Math.PI, 100.0, 300.0);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glTranslatef(0.0F, 0.0F, -200.0F);
		
		shaderManager.configureAtmosphere();
		
		return this.renderToCacheList;
	}

	public void endCache() {
		dominateCache.unbindFramebuffer();
	}

	@Override
	public void renderPass(AtmosphereModel model, Void pass, SkyRenderInformation info) {
		if(info.isFrameBufferEnabled) {
			dominateCache.bindFramebufferTexture();
			GL11.glCallList(this.renderedList);
			dominateCache.unbindFramebufferTexture();
		}
	}
	
	public int beginRender() {
		shaderManager.configureAtmosphere();
		return this.renderedList;
	}

	public void endRender() { }

	@Override
	public void postRender(AtmosphereRenderSettings settings, SkyRenderInformation info) {
		if(info.isFrameBufferEnabled)
			dominateCache.framebufferClear();
	}
	
	
	public void bindCacheTexture() {
		dominateCache.bindFramebufferTexture();
	}

	public void unbindCacheTexture() {
		dominateCache.unbindFramebufferTexture();
	}


	public double getSkyBrightness(SkyRenderInformation info) {
		return info.world.getSunBrightnessFactor(info.partialTicks);
	}

	public double dominationScale(SkyRenderInformation info) {
		return 0.8;
	}
	
	/*
	public float brightnessScale(SpCoord curPos, ISkyEffect sky) {
		double newYAngle = 90.0 * (curPos.y + this.angle) / (90.0 + this.angle);
		float airmass = sky.calculateAirmass(new SpCoord(curPos.x, newYAngle));
		
		return Optics.getAlphaFromMagnitude(airmass, 0.0f);
	}*/


	public void reallocList(AtmosphereRenderSettings settings, boolean isFramebufferEnabled, double deepDepth) {
		Vector3[][] displayvec = Allocator.createAndInitialize(settings.fragLong, settings.fragLat+1);

		for(int longc=0; longc<settings.fragLong; longc++)
			for(int latc=0; latc<=settings.fragLat; latc++)
				displayvec[longc][latc].set(new SpCoord(longc*360.0/settings.fragLong, 180.0 * latc / settings.fragLat - 90.0).getVec());
        
		if(this.renderedList != -1)
			GLAllocation.deleteDisplayLists(this.renderedList);
		
        this.renderedList = GLAllocation.generateDisplayLists(
        		isFramebufferEnabled? 1 : 2);
        
        GL11.glNewList(this.renderedList, GL11.GL_COMPILE);
        this.drawDisplay(displayvec, settings.fragLong, settings.fragLat, deepDepth, isFramebufferEnabled, true);
        GL11.glEndList();
        
        if(isFramebufferEnabled) {
        	this.renderToCacheList = this.renderedList + 1;
        	
    		for(int longc=0; longc<settings.fragLong; longc++)
    			for(int latc=0; latc<=settings.fragLat; latc++)
    				displayvec[longc][latc].set(longc * 2.0 * Math.PI / settings.fragLong, latc * Math.PI / settings.fragLat, 0.0);
        	
            GL11.glNewList(this.renderToCacheList, GL11.GL_COMPILE);
            this.drawDisplay(displayvec, settings.fragLong, settings.fragLat, 1.0, false, false);
            GL11.glEndList();
        }
	}
	
	Vector3 temporal = new Vector3();
	
	private void setupIndicesBuffer(int fragLong, int fragLat) {
		indicesBuffer.clear();
		for(int longc=0; longc < fragLong; longc++) {
			for(int latc=0; latc < fragLat; latc++) {
				int longcd = (longc + 1) % fragLong;
				indicesBuffer.putShort((short) (fragLong * longc + latc));
				indicesBuffer.putShort((short) (fragLong * longc + latc + 1));
				indicesBuffer.putShort((short) (fragLong * longcd + latc + 1));
				indicesBuffer.putShort((short) (fragLong * longcd + latc));
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
				
				renderBuffer.put((float)longc / fragLong);
				renderBuffer.put((float)latc / fragLat);
				
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

		GL11.glDrawElements(GL11.GL_QUADS, fragLong * fragLat * 4, GL11.GL_UNSIGNED_SHORT, this.indicesBuffer);

		if(hasNormal) {
			GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		}

		if(hasTexture) {
			GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
		}
		
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
	}
}
