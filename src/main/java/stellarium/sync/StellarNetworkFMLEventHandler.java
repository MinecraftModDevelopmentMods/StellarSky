package stellarium.sync;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import stellarium.StellarForgeEventHook;
import stellarium.StellarSky;
import stellarium.stellars.StellarManager;
import stellarium.world.StellarDimensionManager;

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
	
	/*@SubscribeEvent
	public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
		EntityPlayerMP player = (EntityPlayerMP) event.player;
		netManager.onSetManager(player, player.worldObj);
	}*/
	
	@SubscribeEvent
	public void handleNotModded(FMLNetworkEvent.ClientConnectedToServerEvent event) {
		if(!event.connectionType.equals("MODDED"))
			this.handleNotHave();
	}
	
	@SubscribeEvent
	public void handleNotHave(FMLNetworkEvent.CustomPacketRegistrationEvent event) {
		if(event.operation.equals("REGISTER") && !event.registrations.contains(netManager.id)
				&& event.handler instanceof NetHandlerPlayClient)
			this.handleNotHave();
	}
	
	private void handleNotHave() {
		StellarForgeEventHook.markNotHave();
	}
	
}
