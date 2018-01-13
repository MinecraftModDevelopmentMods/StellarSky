package stellarium.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.IRenderHandler;
import stellarapi.api.ICelestialCoordinates;
import stellarapi.api.ICelestialWorld;
import stellarapi.api.ISkyEffect;
import stellarapi.api.SAPICapabilities;
import stellarapi.api.SAPIReferences;
import stellarapi.api.optics.IOpticalFilter;
import stellarapi.api.optics.IViewScope;
import stellarium.StellarSky;
import stellarium.client.ClientSettings;
import stellarium.render.sky.SkyModel;
import stellarium.render.sky.SkyRenderInformation;
import stellarium.render.sky.SkyRenderer;
import stellarium.view.ViewerInfo;

public class GenericSkyRenderer extends IRenderHandler {

	private final SkyModel model;

	public GenericSkyRenderer(SkyModel model) {
		this.model = model;
	}

	@Override
	public void render(float partialTicks, WorldClient world, Minecraft mc) {
		Entity viewer = mc.getRenderViewEntity(); // FIXME SLOWER ON DEDICATED GRAPHICS CARD!!!

		ICelestialWorld cWorld = world.getCapability(
				SAPICapabilities.CELESTIAL_CAPABILITY, null);
		ICelestialCoordinates coordinate = cWorld.getCoordinate();
		ISkyEffect sky = cWorld.getSkyEffect();	
		IViewScope scope = SAPIReferences.getScope(viewer);
		IOpticalFilter filter = SAPIReferences.getFilter(viewer);

		SkyRenderInformation info = new SkyRenderInformation(mc, world, partialTicks,
				new ViewerInfo(coordinate, sky, scope, filter, viewer));

		ClientSettings settings = StellarSky.PROXY.getClientSettings();
		SkyRenderer.INSTANCE.preRender(settings, info);
		SkyRenderer.INSTANCE.render(this.model, info);
	}

	
}