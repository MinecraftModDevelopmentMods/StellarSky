package stellarium.sync;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class StellarNetworkEventHandler {

	private StellarNetworkManager manager;
	
	public StellarNetworkEventHandler(StellarNetworkManager manager) {
		this.manager = manager;
	}
}
