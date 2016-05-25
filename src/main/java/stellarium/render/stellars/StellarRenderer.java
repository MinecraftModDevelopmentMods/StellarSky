package stellarium.render.stellars;

import java.util.EnumMap;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Maps;

import net.minecraft.client.renderer.OpenGlHelper;
import stellarium.render.atmosphere.IPhasedRenderer;
import stellarium.render.base.IGenericRenderer;
import stellarium.render.shader.IShaderObject;
import stellarium.render.shader.SwitchableShaders;
import stellarium.render.sky.SkyRenderInformation;
import stellarium.render.stellars.atmosphere.AtmosphereRenderer;
import stellarium.render.stellars.phased.EnumStellarPass;
import stellarium.render.stellars.phased.StellarPhasedRenderer;

public class StellarRenderer implements IGenericRenderer<StellarRenderSettings, Void, StellarModel, SkyRenderInformation> {

	private AtmosphereRenderer atmosphere = new AtmosphereRenderer();
	private StellarPhasedRenderer phasedRenderer = new StellarPhasedRenderer();
	private EnumMap<EnumStellarPass, SwitchableShaders> shaderMap = Maps.newEnumMap(EnumStellarPass.class);

	@Override
	public void initialize(StellarRenderSettings settings) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preRender(StellarRenderSettings settings, SkyRenderInformation info) {
		// TODO Auto-generated method stub

	}

	@Override
	public void renderPass(StellarModel model, Void redundant, SkyRenderInformation info) {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        
		atmosphere.renderPass(model.getAtmosphereModel(), null, info);
		
		GL11.glEnable(GL11.GL_BLEND);

		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		dominateCache.bindFramebufferTexture();
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);

		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
		
		EnumStellarPass pass = EnumStellarPass.SurfaceScatter;
		IShaderObject object = renderer.setupShader(framebufferEnabled, false, textured);
		this.populateShader(object, renderer, textured);
		phasedRenderer.renderPass(model, pass, info);
		renderer.render(objects, framebufferEnabled, false, textured);
		
		GL11.glShadeModel(GL11.GL_FLAT);
		
		pass = EnumStellarPass.PointScatter;
		object = renderer.setupShader(framebufferEnabled, false, false);
		this.populateShader(object, renderer, textured);
		renderer.render(objects, framebufferEnabled, false, textured);
		
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		
		pass = EnumStellarPass.Opaque;
		object = renderer.setupShader(framebufferEnabled, true, textured);
		this.populateShader(object, renderer, textured);
		renderer.render(objects, framebufferEnabled, true, textured);
		
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
		GL11.glShadeModel(GL11.GL_FLAT);
		
		pass = EnumStellarPass.OpaqueScatter;
		object = renderer.setupShader(framebufferEnabled, true, textured);
		this.populateShader(object, renderer, textured);
		renderer.render(objects, framebufferEnabled, true, textured);

		object.releaseShader();
		
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		dominateCache.unbindFramebufferTexture();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
		
        GL11.glDepthMask(true);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	@Override
	public void postRender(StellarRenderSettings settings, SkyRenderInformation info) {
		
	}
	
	protected void setupShader(SwitchableShaders shader, EnumStellarPass pass) {		
		if(OpenGlHelper.isFramebufferEnabled()) {
			shader.switchShader(0);
			shader.bindShader();
			shader.getCurrent().getField(IPhasedRenderer.dominationMapField).setInteger(1);
		} else {
			shader.switchShader(1);
			shader.bindShader();
			shader.getCurrent().getField(IPhasedRenderer.skyBrightnessField).setDouble(renderer.skyBrightness());
		}

		if(pass.hasTexture)
			shader.getField(IPhasedRenderer.defaultTexture).setInteger(0);

		shader.getField(IPhasedRenderer.dominationScaleField).setDouble(renderer.dominationScale());
	}

}
