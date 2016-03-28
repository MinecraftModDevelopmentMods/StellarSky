package stellarium.render;

import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.CrossUtil;
import sciapi.api.value.euclidian.EVector;
import sciapi.api.value.euclidian.IEVector;
import sciapi.api.value.util.VOp;
import stellarium.StellarSky;
import stellarium.client.ClientSettings;
import stellarium.stellars.Optics;
import stellarium.stellars.StellarManager;
import stellarium.stellars.StellarObj;
import stellarium.stellars.StellarTransforms;
import stellarium.stellars.background.BrStar;
import stellarium.stellars.util.ExtinctionRefraction;
import stellarium.util.Color;
import stellarium.util.math.SpCoord;
import stellarium.util.math.Spmath;
import stellarium.util.math.VecMath;

public class SkyLayerCelestial implements ISkyRenderLayer {

	private Minecraft mc;
	private ClientSettings settings;
	
	private List<ICelestialLayer> layers = Lists.newArrayList();
	
	public SkyLayerCelestial() {
		this.settings = StellarSky.proxy.getClientSettings();
		
		layers.add(new CelestialLayerMilkyway());
		layers.add(new CelestialLayerStar());
		layers.add(new CelestialLayerSun());
		layers.add(new CelestialLayerPlanet());
		layers.add(new CelestialLayerMoon());
		
		for(ICelestialLayer layer : this.layers)
			layer.init(this.settings);
	}
	
	@Override
	public void render(float partialTicks, WorldClient world, Minecraft mc) {
		Vec3 skyColor = world.getSkyColor(mc.renderViewEntity, partialTicks);
		float f1 = (float)skyColor.xCoord;
		float f2 = (float)skyColor.yCoord;
		float f3 = (float)skyColor.zCoord;
		TextureManager renderEngine = mc.renderEngine;
		this.mc = mc;
		
		GL11.glPushMatrix();
		float weathereff = 1.0F - world.getRainStrength(partialTicks);

		float bglight=f1+f2+f3;

		GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f); //e,n,z

		GL11.glColor4f(1.0F, 1.0F, 1.0F, weathereff);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);


		double time=(double)world.getWorldTime()+partialTicks;
		
		StellarManager manager = StellarManager.getManager(world);
		
		if(!manager.isSetupComplete())
			manager.update(time, true);
		
		for(ICelestialLayer layer : this.layers)
			layer.render(mc, manager, bglight, weathereff, time);


		GL11.glDisable(GL11.GL_TEXTURE_2D);


		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_FOG);
		GL11.glPopMatrix();
	}

}
