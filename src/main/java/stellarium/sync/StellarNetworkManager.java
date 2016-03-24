package stellarium.sync;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import stellarium.stellars.StellarManager;

public class StellarNetworkManager {
	
	private SimpleNetworkWrapper wrapper;
	
	public StellarNetworkManager() {
		this.wrapper = NetworkRegistry.INSTANCE.newSimpleChannel("stellarskychannel");
		
		wrapper.registerMessage(MessageSyncCommon.MessageSyncCommonHandler.class,
				MessageSyncCommon.class, 0, Side.CLIENT);
		wrapper.registerMessage(MessageLock.MessageLockHandler.class,
				MessageLock.class, 1, Side.SERVER);
	}
	
	@SubscribeEvent
	public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
		StellarManager manager = StellarManager.getManager(event.player.worldObj.isRemote);
		NBTTagCompound compound = new NBTTagCompound();
		manager.writeToNBT(compound);
		
		wrapper.sendTo(new MessageSyncCommon(compound), (EntityPlayerMP)event.player);
	}
	
	public void onTryLock() {
		wrapper.sendToServer(new MessageLock());
	}
	

}
