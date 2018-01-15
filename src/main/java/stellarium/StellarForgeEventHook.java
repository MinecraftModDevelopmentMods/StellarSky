package stellarium;

import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import stellarium.stellars.StellarManager;
import stellarium.stellars.layer.CelestialManager;

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
	public void onSyncConfig(ConfigChangedEvent.OnConfigChangedEvent event) {
		if(StellarSkyReferences.MODID.equals(event.getModID()))
			StellarSky.INSTANCE.getCelestialConfigManager().syncFromGUI();
	}
}