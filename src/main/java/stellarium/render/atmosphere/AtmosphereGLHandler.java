package stellarium.render.atmosphere;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import stellarapi.api.lib.math.SpCoord;
import stellarapi.api.lib.math.Vector3;
import stellarium.render.shader.IShaderObject;
import stellarium.util.math.VectorHelper;

public class AtmosphereGLHandler {
	
	private Framebuffer dominateCache = null;
	private int dominateList = -1;
	private int renderedList = -1;
	
	public void prepareCache(int cacheLong, int cacheLat, int fragLong, int fragLat, double deepDepth) {
		if(dominateCache != null)
			dominateCache.deleteFramebuffer();

		this.dominateCache = new Framebuffer(cacheLong, cacheLat, false);
		dominateCache.setFramebufferFilter(GL11.GL_LINEAR);
		dominateCache.unbindFramebuffer();
		
		
		Vector3[][] displayvec = VectorHelper.createAndInitialize(fragLong, fragLat+1);
        
		for(int longc=0; longc<fragLong; longc++)
			for(int latc=0; latc<=fragLat; latc++) {
				displayvec[longc][latc].set(new SpCoord(longc*360.0/fragLong, 180.0 * latc / fragLat - 90.0).getVec());
				displayvec[longc][latc].scale(deepDepth);
			}
        
		if(this.dominateList != -1)
			GLAllocation.deleteDisplayLists(this.dominateList);
		
        this.dominateList = GLAllocation.generateDisplayLists(
        		OpenGlHelper.isFramebufferEnabled()? 1 : 2);
        
        if(OpenGlHelper.isFramebufferEnabled()) {
        	this.renderedList = this.dominateList + 1;
        	
            GL11.glNewList(this.renderedList, GL11.GL_COMPILE);
            this.drawDisplay(displayvec, fragLong, fragLat, deepDepth, true, true);
            GL11.glEndList();
        	
    		for(int longc=0; longc<fragLong; longc++)
    			for(int latc=0; latc<=fragLat; latc++)
    				displayvec[longc][latc].set(longc * 2.0 * Math.PI / fragLong, latc * Math.PI / fragLat, 0.0);
        }
        
        GL11.glNewList(this.dominateList, GL11.GL_COMPILE);
        this.drawDisplay(displayvec, fragLong, fragLat, deepDepth, false, !OpenGlHelper.isFramebufferEnabled());
        GL11.glEndList();
	}
	
	private void drawDisplay(Vector3[][] displayvec, int fragLong, int fragLat, double length, boolean hasTexture, boolean hasNormal) {
		GL11.glBegin(GL11.GL_QUADS);
		
		for(int longc=0; longc<fragLong; longc++) {
			for(int latc=0; latc<fragLat; latc++) {
				int longcd=(longc+1)%fragLong;
				
				if(hasTexture)
					GL11.glTexCoord2d((double)longc / fragLong, (double)latc / fragLat);
				if(hasNormal)
					GL11.glNormal3d(displayvec[longc][latc].getX()/length, displayvec[longc][latc].getY()/length, displayvec[longc][latc].getZ()/length);
				GL11.glVertex3d(displayvec[longc][latc].getX(), displayvec[longc][latc].getY(), displayvec[longc][latc].getZ());
				
				if(hasTexture)
					GL11.glTexCoord2d((double)longc / fragLong, (latc+1.0) / fragLat);
				if(hasNormal)
					GL11.glNormal3d(displayvec[longc][latc+1].getX()/length, displayvec[longc][latc+1].getY()/length, displayvec[longc][latc+1].getZ()/length);
				GL11.glVertex3d(displayvec[longc][latc+1].getX(), displayvec[longc][latc+1].getY(), displayvec[longc][latc+1].getZ());

				if(hasTexture)
					GL11.glTexCoord2d((double)longcd / fragLong, (latc+1.0) / fragLat);
				if(hasNormal)
					GL11.glNormal3d(displayvec[longcd][latc+1].getX()/length, displayvec[longcd][latc+1].getY()/length, displayvec[longcd][latc+1].getZ()/length);
				GL11.glVertex3d(displayvec[longcd][latc+1].getX(), displayvec[longcd][latc+1].getY(), displayvec[longcd][latc+1].getZ());

				if(hasTexture)
					GL11.glTexCoord2d((double)longcd / fragLong, (double)latc / fragLat);
				if(hasNormal)
					GL11.glNormal3d(displayvec[longcd][latc].getX()/length, displayvec[longcd][latc].getY()/length, displayvec[longcd][latc].getZ()/length);
				GL11.glVertex3d(displayvec[longcd][latc].getX(), displayvec[longcd][latc].getY(), displayvec[longcd][latc].getZ());
			}
		}
		
		GL11.glEnd();
	}
	
	public void renderAtmosphere(IPhasedRenderer renderer) {
		dominateCache.bindFramebuffer(false);
		
		boolean framebufferEnabled = OpenGlHelper.isFramebufferEnabled();

		if(framebufferEnabled) {
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0.0, 2.0 * Math.PI, 0.0, Math.PI, 100.0, 300.0);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
	        GL11.glLoadIdentity();
	        GL11.glTranslatef(0.0F, 0.0F, -200.0F);
		}
		
		for(int i = 0; i < renderer.numberDominators(); i++) {
			renderer.setupDominateShader(i, framebufferEnabled);
			GL11.glCallList(this.dominateList);
		}
		
        dominateCache.unbindFramebuffer();
        
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        
		if(framebufferEnabled) {
	        GL11.glDisable(GL11.GL_BLEND);
			dominateCache.bindFramebufferTexture();
			GL11.glCallList(this.renderedList);
			dominateCache.unbindFramebufferTexture();
			GL11.glEnable(GL11.GL_BLEND);
		}

		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		dominateCache.bindFramebufferTexture();
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);

		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
		
		boolean textured = true;
		IShaderObject object = renderer.setupShader(framebufferEnabled, false, textured);
		this.populateShader(object, renderer, textured);
		renderer.render(framebufferEnabled, false, textured);
		
		GL11.glShadeModel(GL11.GL_FLAT);
		
		textured = false;
		object = renderer.setupShader(framebufferEnabled, false, false);
		this.populateShader(object, renderer, textured);
		renderer.render(framebufferEnabled, false, textured);
		
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		
		textured = true;
		object = renderer.setupShader(framebufferEnabled, true, textured);
		this.populateShader(object, renderer, textured);
		renderer.render(framebufferEnabled, true, textured);
		
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
		GL11.glShadeModel(GL11.GL_FLAT);
		
		textured = false;
		object = renderer.setupShader(framebufferEnabled, true, textured);
		this.populateShader(object, renderer, textured);
		renderer.render(framebufferEnabled, true, textured);

		object.releaseShader();
		
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		dominateCache.unbindFramebufferTexture();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
		
        GL11.glDepthMask(true);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		dominateCache.framebufferClear();
	}
	
	private void populateShader(IShaderObject object, IPhasedRenderer renderer, boolean textured) {
		if(OpenGlHelper.isFramebufferEnabled()) {
			object.getField(IPhasedRenderer.dominationMapField).setInteger(1);
		} else object.getField(IPhasedRenderer.skyBrightnessField).setDouble(renderer.skyBrightness());
		
		if(textured)
			object.getField(IPhasedRenderer.defaultTexture).setInteger(0);
		
		object.getField(IPhasedRenderer.dominationScaleField).setDouble(renderer.dominationScale());
	}

}
