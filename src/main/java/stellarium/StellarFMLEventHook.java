package stellarium;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayerMP;

public class StellarFMLEventHook {
	@SubscribeEvent
	public void onSyncConfig(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if(event.modID == StellarSky.modid)
			StellarSky.proxy.getCfgManager().syncFromGUI();
	}
	
	@SubscribeEvent
	public void handleLogin(PlayerEvent.PlayerLoggedInEvent event) {
		StellarSky.instance.getNetworkManager().sendSyncInformation((EntityPlayerMP) event.player);
	}
	
	@SubscribeEvent
	public void handleNotModded(FMLNetworkEvent.ClientConnectedToServerEvent event) {
		if(!event.connectionType.equals("MODDED"))
			this.handleNotHave();
	}
	
	@SubscribeEvent
	public void handleNotHave(FMLNetworkEvent.CustomPacketRegistrationEvent event) {
		if(event.operation.equals("REGISTER") && !event.registrations.contains(
				StellarSky.instance.getNetworkManager().getID())
				&& event.handler instanceof NetHandlerPlayClient)
			this.handleNotHave();
	}
	
	private void handleNotHave() {
		StellarForgeEventHook.markNotHave();
	}
}
