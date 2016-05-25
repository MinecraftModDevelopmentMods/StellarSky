package stellarium.render.stellars.atmosphere;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarium.render.base.IGenericRenderer;
import stellarium.render.sky.SkyRenderInformation;
import stellarium.util.math.Allocator;

public class AtmosphereRenderer implements IGenericRenderer<AtmosphereRenderSettings, Void, AtmosphereModel, SkyRenderInformation> {
	
	private Framebuffer dominateCache = null;
	private int renderToCacheList = -1;
	private int renderedList = -1;
	
	private boolean previousFlag;
	private boolean initialFlag = true;

	@Override
	public void preRender(AtmosphereRenderSettings settings, SkyRenderInformation info) {
		if(this.initialFlag || this.previousFlag != info.isFrameBufferEnabled) {
			this.reallocList(settings, info.isFrameBufferEnabled, info.deepDepth);
			this.previousFlag = info.isFrameBufferEnabled;
			this.initialFlag = false;
		}
		
		if(info.isFrameBufferEnabled) {
			dominateCache.bindFramebuffer(false);

			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0.0, 2.0 * Math.PI, 0.0, Math.PI, 100.0, 300.0);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glLoadIdentity();
			GL11.glTranslatef(0.0F, 0.0F, -200.0F);

			for(int i = 0; i < renderer.numberDominators(); i++) {
				renderer.setupDominateShader(i, true);
				GL11.glCallList(this.renderToCacheList);
			}

			dominateCache.unbindFramebuffer();
		}
	}

	@Override
	public void renderPass(AtmosphereModel model, Void pass, SkyRenderInformation info) {
		if(info.isFrameBufferEnabled) {
			dominateCache.bindFramebufferTexture();
			GL11.glCallList(this.renderedList);
			dominateCache.unbindFramebufferTexture();
		} else {
			for(int i = 0; i < renderer.numberDominators(); i++) {
				renderer.setupDominateShader(i, false);
				GL11.glCallList(this.renderedList);
			}
		}
	}

	@Override
	public void postRender(AtmosphereRenderSettings settings, SkyRenderInformation info) {
		if(info.isFrameBufferEnabled)
			dominateCache.framebufferClear();
	}

	
	@Override
	public void initialize(AtmosphereRenderSettings settings) {
		if(dominateCache != null)
			dominateCache.deleteFramebuffer();

		this.dominateCache = new Framebuffer(settings.cacheLong, settings.cacheLat, false);
		dominateCache.setFramebufferFilter(GL11.GL_LINEAR);
		dominateCache.unbindFramebuffer();
	}
	
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
	
	private void drawDisplay(Vector3[][] displayvec, int fragLong, int fragLat, double length, boolean hasTexture, boolean hasNormal) {
		GL11.glBegin(GL11.GL_QUADS);
		
		for(int longc=0; longc<fragLong; longc++) {
			for(int latc=0; latc<fragLat; latc++) {
				int longcd=(longc+1)%fragLong;
				
				if(hasTexture)
					GL11.glTexCoord2d((double)longc / fragLong, (double)latc / fragLat);
				if(hasNormal)
					GL11.glNormal3d(displayvec[longc][latc].getX(), displayvec[longc][latc].getY(), displayvec[longc][latc].getZ());
				GL11.glVertex3d(displayvec[longc][latc].getX()*length, displayvec[longc][latc].getY()*length, displayvec[longc][latc].getZ()*length);
				
				if(hasTexture)
					GL11.glTexCoord2d((double)longc / fragLong, (latc+1.0) / fragLat);
				if(hasNormal)
					GL11.glNormal3d(displayvec[longc][latc+1].getX(), displayvec[longc][latc+1].getY(), displayvec[longc][latc+1].getZ());
				GL11.glVertex3d(displayvec[longc][latc+1].getX()*length, displayvec[longc][latc+1].getY()*length, displayvec[longc][latc+1].getZ()*length);

				if(hasTexture)
					GL11.glTexCoord2d((double)longcd / fragLong, (latc+1.0) / fragLat);
				if(hasNormal)
					GL11.glNormal3d(displayvec[longcd][latc+1].getX(), displayvec[longcd][latc+1].getY(), displayvec[longcd][latc+1].getZ());
				GL11.glVertex3d(displayvec[longcd][latc+1].getX()*length, displayvec[longcd][latc+1].getY()*length, displayvec[longcd][latc+1].getZ()*length);

				if(hasTexture)
					GL11.glTexCoord2d((double)longcd / fragLong, (double)latc / fragLat);
				if(hasNormal)
					GL11.glNormal3d(displayvec[longcd][latc].getX(), displayvec[longcd][latc].getY(), displayvec[longcd][latc].getZ());
				GL11.glVertex3d(displayvec[longcd][latc].getX()*length, displayvec[longcd][latc].getY()*length, displayvec[longcd][latc].getZ()*length);
			}
		}
		
		GL11.glEnd();
	}
}
