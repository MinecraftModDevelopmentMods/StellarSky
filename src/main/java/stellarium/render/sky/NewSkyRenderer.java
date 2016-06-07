package stellarium.render.sky;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.IRenderHandler;
import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.ISkyEffect;
import stellarapi.api.StellarAPIReference;
import stellarapi.api.optics.IOpticalFilter;
import stellarapi.api.optics.IViewScope;
import stellarium.StellarSky;
import stellarium.lib.render.IGenericRenderer;
import stellarium.lib.render.RendererRegistry;
import stellarium.view.ViewerInfo;

public class NewSkyRenderer extends IRenderHandler {

	private SkyModel model;
	
	public NewSkyRenderer(SkyModel model) {
		this.model = model;
	}

	@Override
	public void render(float partialTicks, WorldClient world, Minecraft mc) {
		IGenericRenderer renderer = RendererRegistry.INSTANCE.evaluateRenderer(SkyModel.class);

		EntityLivingBase viewer = mc.renderViewEntity;

		ICelestialCoordinate coordinate = StellarAPIReference.getCoordinate(world);
		ISkyEffect sky = StellarAPIReference.getSkyEffect(world);
		IViewScope scope = StellarAPIReference.getScope(viewer);
		IOpticalFilter filter = StellarAPIReference.getFilter(viewer);
		
		SkyRenderInformation info = new SkyRenderInformation(mc, world, partialTicks,
				new ViewerInfo(coordinate, sky, scope, filter, viewer));

		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		renderer.preRender(StellarSky.proxy.getClientSettings(), info);
		renderer.renderPass(this.model, null, info);
		renderer.postRender(StellarSky.proxy.getClientSettings(), info);
	}
}