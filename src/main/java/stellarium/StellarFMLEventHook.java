package stellarium;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class StellarFMLEventHook {
	@SubscribeEvent
	public void onSyncConfig(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if(StellarSkyReferences.modid.equals(event.getModID()))
			StellarSky.INSTANCE.getCelestialConfigManager().syncFromGUI();
	}
	
	@SubscribeEvent
	public void handleNotModded(FMLNetworkEvent.ClientConnectedToServerEvent event) {
		if(!event.getConnectionType().equals("MODDED"))
			this.handleNotHave();
	}

	@SubscribeEvent
	public void handleNotHave(FMLNetworkEvent.CustomPacketRegistrationEvent event) {
		if(event.getOperation().equals("REGISTER") && !event.getRegistrations().contains(
				StellarSky.INSTANCE.getNetworkManager().getID())
				&& event.getSide().isClient())
			this.handleNotHave();
	}
	
	@SubscribeEvent
	public void handleDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
		StellarForgeEventHook.clearState();
	}

	private void handleNotHave() {
		StellarForgeEventHook.markNotHave();
	}
}
