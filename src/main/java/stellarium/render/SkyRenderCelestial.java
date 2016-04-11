package stellarium.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;
import stellarium.StellarSky;
import stellarium.api.ICelestialRenderer;
import stellarium.client.ClientSettings;
import stellarium.stellars.StellarManager;
import stellarium.stellars.layer.CelestialManager;
import stellarium.stellars.view.StellarDimensionManager;

public class SkyRenderCelestial implements ICelestialRenderer {
	
	private ClientSettings settings;
	private CelestialRenderer renderer;
	private boolean updated = false;
	
	public SkyRenderCelestial() {
		this.settings = StellarSky.proxy.getClientSettings();
		this.renderer = new CelestialRenderer();
		
		CelestialManager celManager = StellarSky.proxy.getClientCelestialManager();
		renderer.refreshRenderer(celManager.getLayers());
		settings.checkDirty();
		celManager.reloadClientSettings(this.settings);
		this.onSettingsUpdated(Minecraft.getMinecraft());
	}
	
	public void renderCelestial(Minecraft mc, float bglight, float weathereff, float partialTicks) {
		CelestialManager manager = StellarSky.proxy.getClientCelestialManager();

		if(settings.checkDirty())
		{
			manager.reloadClientSettings(this.settings);
			this.updated = false;
		}
		
		if(!this.updated)
			this.onSettingsUpdated(mc);
		
		renderer.render(new StellarRenderInfo(mc, Tessellator.instance, bglight, weathereff, partialTicks), manager.getLayers());
	}
	
	private void onSettingsUpdated(Minecraft mc) {
		//Initialization update
		World world = mc.theWorld;
		
		if(world != null) {
			StellarManager manager = StellarManager.getManager(true);
			if(manager.getCelestialManager() != null) {
				this.updated = true;
				manager.update(world.getWorldTime());
				StellarDimensionManager dimManager = StellarDimensionManager.get(world);
				if(dimManager != null)
				{
					dimManager.update(world, world.getWorldTime());
					manager.updateClient(StellarSky.proxy.getClientSettings(),
							dimManager.getViewpoint());
				}
			}
		}
	}

}
