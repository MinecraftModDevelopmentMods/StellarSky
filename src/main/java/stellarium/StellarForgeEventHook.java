package stellarium;

import javax.annotation.Nonnull;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.world.WorldEvent;
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
		if(world.isRemote)
			this.onClientLoad(world, manager);
		else this.onServerLoad(world, manager);
	}

	private void onClientLoad(World world, StellarManager manager) {
		if(mark)
			handleNotHaveModOnServer(world, manager);
	}

	private void onServerLoad(World world, StellarManager manager) {
		manager.setup(new CelestialManager(false));
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		if(event.getWorld().isRemote) {
			this.populateRenderer(event.getWorld(), StellarManager.getManager(event.getWorld()));
		}
	}

	private static void populateRenderer(World world, StellarManager manager) {
		// This is separate as world capability should've been established.
		StellarScene scene = StellarScene.getScene(world);
		if(scene != null)
			StellarSky.PROXY.setupSkyRenderer(world, scene.getSettings().getSkyRendererType());
	}


	private static boolean mark = false;
	
	public static void markNotHave() {
		mark = true;
	}
	
	public static void clearState() {
		mark = false;
	}


	private void handleNotHaveModOnServer(@Nonnull World world, @Nonnull StellarManager manager) {
		manager.handleServerWithoutMod();
		
		if(manager.getCelestialManager() == null) {
			manager.setup(StellarSky.PROXY.getClientCelestialManager().copyFromClient());
			handleDimOnServerDisabled(world, manager);
		}
	}

	private void handleDimOnServerDisabled(@Nonnull World world, @Nonnull StellarManager manager) {
		this.populateRenderer(world, manager);
	}

	@SubscribeEvent
	public void onAttachCapabilities(AttachCapabilitiesEvent<World> event) {
		if(event.getObject().isRemote)
			event.addCapability(new ResourceLocation(
					StellarSkyReferences.modid, "SkyRenderer"), new RendererHolder());
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

}