package stellarium.sync;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import stellarium.stellars.StellarManager;

public class StellarNetworkManager {
	
	private SimpleNetworkWrapper wrapper;
	
	public StellarNetworkManager() {
		this.wrapper = NetworkRegistry.INSTANCE.newSimpleChannel("stellarskychannel");
		
		wrapper.registerMessage(MessageSyncCommon.MessageSyncCommonHandler.class,
				MessageSyncCommon.class, 0, Side.CLIENT);
	}
	
	@SubscribeEvent
	public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
		StellarManager manager = StellarManager.getManager(event.player.worldObj);
		NBTTagCompound compound = new NBTTagCompound();
		manager.writeToNBT(compound);
		
		wrapper.sendTo(new MessageSyncCommon(compound), (EntityPlayerMP)event.player);
	}
	

}
