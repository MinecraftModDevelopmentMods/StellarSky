package stellarium.render.stellars;

import java.util.EnumMap;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Maps;

import net.minecraft.client.renderer.OpenGlHelper;
import stellarium.StellarSkyResources;
import stellarium.render.base.IGenericRenderer;
import stellarium.render.shader.ShaderHelper;
import stellarium.render.shader.SwitchableShaders;
import stellarium.render.sky.SkyRenderInformation;
import stellarium.render.stellars.access.EnumStellarPass;
import stellarium.render.stellars.atmosphere.AtmosphereRenderer;
import stellarium.render.stellars.phased.StellarPhasedRenderer;
import stellarium.render.stellars.phased.StellarRenderInformation;

public class StellarRenderer implements IGenericRenderer<StellarRenderSettings, Void, StellarModel, SkyRenderInformation> {

	private AtmosphereRenderer atmosphere = new AtmosphereRenderer();
	private StellarPhasedRenderer phasedRenderer = new StellarPhasedRenderer();
	private EnumMap<EnumStellarPass, SwitchableShaders> shaderMap = Maps.newEnumMap(EnumStellarPass.class);

	private void reloadShaders() {
		SwitchableShaders scatterFuzzy = new SwitchableShaders(
				ShaderHelper.getInstance().buildShader("fuzzymapped", StellarSkyResources.vertexScatterFuzzyMapped, StellarSkyResources.fragmentScatterFuzzyMapped),
				ShaderHelper.getInstance().buildShader("fuzzyskylight", StellarSkyResources.vertexScatterFuzzySkylight, StellarSkyResources.fragmentScatterFuzzySkylight));

		SwitchableShaders scatterPoint = new SwitchableShaders(
				ShaderHelper.getInstance().buildShader("pointmapped", StellarSkyResources.vertexScatterPointMapped, StellarSkyResources.fragmentScatterPointMapped),
				ShaderHelper.getInstance().buildShader("pointskylight", StellarSkyResources.vertexScatterPointSkylight, StellarSkyResources.fragmentScatterPointSkylight));

		SwitchableShaders srcTexture = new SwitchableShaders(
				ShaderHelper.getInstance().buildShader("srctexturemapped", StellarSkyResources.vertexTextureMapped, StellarSkyResources.fragmentTextureMapped),
				ShaderHelper.getInstance().buildShader("srctextureskylight", StellarSkyResources.vertexTextureSkylight, StellarSkyResources.fragmentTextureSkylight));


		SwitchableShaders atmosphere = new SwitchableShaders(
				ShaderHelper.getInstance().buildShader("atmospherespcoord", StellarSkyResources.vertexAtmosphereSpCoord, StellarSkyResources.fragmentAtmosphereSpCoord),
				ShaderHelper.getInstance().buildShader("atmospheresimple", StellarSkyResources.vertexAtmosphereSimple, StellarSkyResources.fragmentAtmosphereSimple));


		shaderMap.put(EnumStellarPass.DominateScatter, atmosphere);
		shaderMap.put(EnumStellarPass.SurfaceScatter, srcTexture);
		shaderMap.put(EnumStellarPass.PointScatter, scatterPoint);
		shaderMap.put(EnumStellarPass.Opaque, srcTexture);
		shaderMap.put(EnumStellarPass.OpaqueScatter, scatterFuzzy);
	}

	@Override
	public void initialize(StellarRenderSettings settings) {
		this.reloadShaders();
		atmosphere.setupAtmShader(shaderMap.get(EnumStellarPass.DominateScatter));
		atmosphere.initialize(settings.getAtmosphereSettings());
	}

	@Override
	public void preRender(StellarRenderSettings settings, SkyRenderInformation info) {
		atmosphere.preRender(settings.getAtmosphereSettings(), info);
		phasedRenderer.preRender(null, new StellarRenderInformation(info));
	}

	@Override
	public void renderPass(StellarModel model, Void redundant, SkyRenderInformation info) {
		EnumStellarPass pass;
		SwitchableShaders object;
		StellarRenderInformation subInfo = new StellarRenderInformation(info);
		
		if(info.isFrameBufferEnabled) {
        	pass = EnumStellarPass.DominateScatter;
        	
        	object = shaderMap.get(pass);
        	this.setupAtmShader(object, true);
        	
        	subInfo.setActiveShader(object);
        	subInfo.setAtmCallList(atmosphere.beginCache());

        	phasedRenderer.renderPass(model.getStellarModel(), pass, subInfo);
        	atmosphere.endCache();
        	
			OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			atmosphere.bindCacheTexture();
			OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
		}
		
		subInfo.setAtmCallList(-1);
		
		GL11.glShadeModel(GL11.GL_SMOOTH);
		
		pass = EnumStellarPass.SurfaceScatter;
		object = shaderMap.get(pass);
    	subInfo.setActiveShader(object);
		this.setupShader(object, pass, info);
		phasedRenderer.renderPass(model.getStellarModel(), pass, subInfo);
		
		GL11.glShadeModel(GL11.GL_FLAT);
		
		pass = EnumStellarPass.PointScatter;
		object = shaderMap.get(pass);
    	subInfo.setActiveShader(object);
		this.setupShader(object, pass, info);
		phasedRenderer.renderPass(model.getStellarModel(), pass, subInfo);
		
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		
		pass = EnumStellarPass.Opaque;
		object = shaderMap.get(pass);
    	subInfo.setActiveShader(object);
		this.setupShader(object, pass, info);
		phasedRenderer.renderPass(model.getStellarModel(), pass, subInfo);
		
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
		GL11.glShadeModel(GL11.GL_FLAT);
		
		pass = EnumStellarPass.OpaqueScatter;
		object = shaderMap.get(pass);
    	subInfo.setActiveShader(object);
		this.setupShader(object, pass, info);
		phasedRenderer.renderPass(model.getStellarModel(), pass, subInfo);
		
		GL11.glShadeModel(GL11.GL_SMOOTH);
		
		atmosphere.renderPass(model.getAtmosphereModel(), null, info);
		
		if(!info.isFrameBufferEnabled) {
        	pass = EnumStellarPass.DominateScatter;
        	
        	object = shaderMap.get(pass);
        	this.setupAtmShader(object, false);
        	
        	subInfo.setActiveShader(object);
        	subInfo.setAtmCallList(atmosphere.beginRender());

        	phasedRenderer.renderPass(model.getStellarModel(), pass, subInfo);
        	atmosphere.endRender();
		} else {
			OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
			atmosphere.unbindCacheTexture();
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
		}
		
		object.releaseShader();
		
        GL11.glDepthMask(true);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	@Override
	public void postRender(StellarRenderSettings settings, SkyRenderInformation info) {
		phasedRenderer.postRender(null, new StellarRenderInformation(info));
		atmosphere.postRender(settings.getAtmosphereSettings(), info);
	}

	private void setupAtmShader(SwitchableShaders shader, boolean framebufferEnabled) {
		shader.switchShader(framebufferEnabled? 0 : 1);
		shader.bindShader();
	}


	private static final String dominationMapField = "skyDominationMap";
	private static final String skyBrightnessField = "skyBrightness";
	private static final String defaultTexture = "texture";
	private static final String dominationScaleField = "dominationscale";

	protected void setupShader(SwitchableShaders shader, EnumStellarPass pass, SkyRenderInformation info) {		
		if(info.isFrameBufferEnabled) {
			shader.switchShader(0);
			shader.bindShader();
			shader.getCurrent().getField(dominationMapField).setInteger(1);
		} else {
			shader.switchShader(1);
			shader.bindShader();
			shader.getCurrent().getField(skyBrightnessField).setDouble(atmosphere.getSkyBrightness(info));
		}

		if(pass.hasTexture)
			shader.getField(defaultTexture).setInteger(0);

		shader.getField(dominationScaleField).setDouble(atmosphere.dominationScale(info));
	}

}
