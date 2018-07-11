package stellarium.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.IRenderHandler;
import stellarapi.api.SAPICapabilities;
import stellarapi.api.view.IAtmosphereEffect;
import stellarapi.api.view.ICCoordinates;
import stellarapi.api.world.ICelestialWorld;
import stellarium.StellarSky;
import stellarium.client.ClientSettings;
import stellarium.view.ViewerInfo;

public class GenericSkyRenderer extends IRenderHandler {

	private final SkyModel model;

	public GenericSkyRenderer(SkyModel model) {
		this.model = model;
	}

	@Override
	public void render(float partialTicks, WorldClient world, Minecraft mc) {
		Entity viewer = mc.getRenderViewEntity();

		ICelestialWorld cWorld = world.getCapability(
				SAPICapabilities.CELESTIAL_CAPABILITY, null);
		ICCoordinates coordinate = cWorld.getCoordinate();
		IAtmosphereEffect sky = cWorld.getSkyEffect();

		SkyRI info = new SkyRI(mc, world, partialTicks,
				new ViewerInfo(coordinate, sky, viewer, partialTicks));

		ClientSettings settings = StellarSky.PROXY.getClientSettings();
		SkyRenderer.INSTANCE.preRender(settings, info);
		SkyRenderer.INSTANCE.render(this.model, info);
	}

	
}