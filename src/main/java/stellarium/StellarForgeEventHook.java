package stellarium;

import javax.annotation.Nonnull;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarium.api.IAdaptiveRenderer;
import stellarium.api.StellarSkyAPI;
import stellarium.client.RendererHolder;
import stellarium.stellars.StellarManager;
import stellarium.stellars.layer.CelestialManager;
import stellarium.util.WorldUtil;
import stellarium.world.StellarScene;

public class StellarForgeEventHook {
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void preAttachCapabilities(AttachCapabilitiesEvent<World> event) {
		World world = event.getObject();
		// Check if it's initial
		if(!world.isRemote && world.provider.getDimension() != 0)
			return;

		// Now setup StellarManager here
		StellarManager manager = StellarManager.loadOrCreateManager(world);
		if(!world.isRemote)
			manager.setup(new CelestialManager(false));
		// On client and when the server does not exist
		else if(!StellarSky.INSTANCE.existOnServer()) {
			manager.handleServerWithoutMod();
			if(manager.getCelestialManager() == null)
				manager.setup(StellarSky.PROXY.getClientCelestialManager().copyFromClient());
		}
	}

	@SubscribeEvent
	public void onAttachCapabilities(AttachCapabilitiesEvent<World> event) {
		if(event.getObject().isRemote)
			event.addCapability(new ResourceLocation(
					StellarSkyReferences.MODID, "SkyRenderer"), new RendererHolder());
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		if(event.getWorld().isRemote) {
			World world = event.getWorld();
			// World capability should've been established here.
			StellarScene scene = StellarScene.getScene(world);
			if(scene != null)
				StellarSky.PROXY.setupSkyRenderer(world, scene.getSettings().getSkyRendererType());
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.LOW)
	public void onSkyRender(RenderWorldLastEvent event) {
		World world = StellarSky.PROXY.getDefWorld();
		if(world.hasCapability(StellarSkyAPI.SKY_RENDER_HOLDER, null)) {
			IAdaptiveRenderer renderer = world.getCapability(StellarSkyAPI.SKY_RENDER_HOLDER, null).getRenderer();
			if(renderer != null && !(world.provider.getSkyRenderer() instanceof IAdaptiveRenderer)) {
				StellarScene dimManager = StellarScene.getScene(world);
				if(dimManager.getSettings().renderPrevSky())
					renderer.setReplacedRenderer(world.provider.getSkyRenderer());
				else renderer.setReplacedRenderer(null);
				world.provider.setSkyRenderer(renderer);
			}
		}
	}

	@SubscribeEvent
	public void onSyncConfig(ConfigChangedEvent.OnConfigChangedEvent event) {
		if(StellarSkyReferences.MODID.equals(event.getModID()))
			StellarSky.INSTANCE.getCelestialConfigManager().syncFromGUI();
	}
}