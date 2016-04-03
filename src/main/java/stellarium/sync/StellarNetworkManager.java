package stellarium.sync;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import stellarium.stellars.StellarManager;
import stellarium.stellars.view.StellarDimensionManager;

public class StellarNetworkManager {
	
	private SimpleNetworkWrapper wrapper;
	
	public StellarNetworkManager() {
		this.wrapper = NetworkRegistry.INSTANCE.newSimpleChannel("stellarskychannel");
		
		wrapper.registerMessage(MessageSyncCommon.MessageSyncCommonHandler.class,
				MessageSyncCommon.class, 0, Side.CLIENT);
	}
	
	@SubscribeEvent
	public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
		this.onSetManager(event);
	}
	
	@SubscribeEvent
	public void onPlayerJoin(PlayerEvent.PlayerChangedDimensionEvent event) {
		this.onSetManager(event);
	}
	
	public void onSetManager(PlayerEvent event) {
		StellarManager manager = StellarManager.getManager(false);
		StellarDimensionManager dimManager = StellarDimensionManager.get(event.player.worldObj);
		
		NBTTagCompound compound = new NBTTagCompound();
		manager.writeToNBT(compound);
		
		NBTTagCompound dimComp = new NBTTagCompound();
		if(dimManager != null)
			dimManager.writeToNBT(dimComp);
		
		
		wrapper.sendTo(new MessageSyncCommon(compound, dimComp), (EntityPlayerMP)event.player);
	}

}
