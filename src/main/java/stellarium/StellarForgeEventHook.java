package stellarium;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarium.api.IAdaptiveRenderer;
import stellarium.api.StellarSkyAPI;
import stellarium.client.RendererHolder;

public class StellarForgeEventHook {

	@SubscribeEvent
	public void onAttachCapabilities(AttachCapabilitiesEvent<World> event) {
		if(event.getObject().isRemote)
			event.addCapability(new ResourceLocation(
					StellarSkyReferences.modid, "SkyRenderer"), new RendererHolder());
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.LOW)
	public void onSkyRender(RenderWorldLastEvent event) {
		World world = StellarSky.proxy.getDefWorld();
		if(world.hasCapability(StellarSkyAPI.SKY_RENDER_HOLDER, null)) {
			IAdaptiveRenderer renderer = world.getCapability(StellarSkyAPI.SKY_RENDER_HOLDER, null).getRenderer();
			if(renderer != null && !(world.provider.getSkyRenderer() instanceof IAdaptiveRenderer)) {
				renderer.setReplacedRenderer(world.provider.getSkyRenderer());
				world.provider.setSkyRenderer(renderer);
			}
		}
	}

}