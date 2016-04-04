package stellarium.sync;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class StellarNetworkFMLEventHandler {

	private StellarNetworkManager manager;
	
	public StellarNetworkFMLEventHandler(StellarNetworkManager manager) {
		this.manager = manager;
	}
	
	@SubscribeEvent
	public void onPlayerLoad(PlayerEvent.PlayerLoggedInEvent event) {
		EntityPlayerMP player = (EntityPlayerMP) event.player;
		
		manager.onSetManager(player, player.worldObj);
		//((NetHandlerPlayServer)event.handler).playerEntity
	}
	
	@SubscribeEvent
	public void onPlayerJoinDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		manager.onSetManager((EntityPlayerMP) event.player, event.player.worldObj);
	}
	
}
