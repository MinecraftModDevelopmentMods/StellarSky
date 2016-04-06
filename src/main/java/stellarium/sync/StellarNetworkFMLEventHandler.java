package stellarium.sync;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import stellarium.StellarEventHook;

public class StellarNetworkFMLEventHandler {

	private StellarNetworkManager netManager;
	
	public StellarNetworkFMLEventHandler(StellarNetworkManager manager) {
		this.netManager = manager;
	}
	
	@SubscribeEvent
	public void onPlayerLoad(PlayerEvent.PlayerLoggedInEvent event) {
		EntityPlayerMP player = (EntityPlayerMP) event.player;
		netManager.onSetManager(player, player.worldObj);
	}
	
	@SubscribeEvent
	public void onPlayerJoinDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		netManager.onSetManager((EntityPlayerMP) event.player, event.player.worldObj);
	}
	
	@SubscribeEvent
	public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
		EntityPlayerMP player = (EntityPlayerMP) event.player;
		netManager.onSetManager(player, player.worldObj);
	}
	
	@SubscribeEvent
	public void handleNotModded(FMLNetworkEvent.ClientConnectedToServerEvent event) {
		if(!event.getConnectionType().equals("MODDED"))
			this.handleNotHave();
	}
	
	@SubscribeEvent
	public void handleNotHave(FMLNetworkEvent.CustomPacketRegistrationEvent event) {
		if(event.getOperation().equals("REGISTER") && !event.getRegistrations().contains(netManager.id)
				&& event.getHandler() instanceof NetHandlerPlayClient)
			this.handleNotHave();
	}
	
	private void handleNotHave() {
		StellarEventHook.markNotHave();
	}
	
}
