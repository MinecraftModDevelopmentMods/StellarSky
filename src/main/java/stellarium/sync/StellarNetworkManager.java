package stellarium.sync;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import stellarium.stellars.StellarManager;
import stellarium.stellars.view.StellarDimensionManager;

public class StellarNetworkManager {
	
	private SimpleNetworkWrapper wrapper;
	protected String id = "stellarskychannel";
	
	public StellarNetworkManager() {
		this.wrapper = NetworkRegistry.INSTANCE.newSimpleChannel(this.id);
		
		wrapper.registerMessage(MessageSyncCommon.MessageSyncCommonHandler.class,
				MessageSyncCommon.class, 0, Side.CLIENT);
	}
	
	public void onSetManager(EntityPlayerMP player, World world) {
		StellarManager manager = StellarManager.getManager(false);
		StellarDimensionManager dimManager = StellarDimensionManager.get(world);
		
		NBTTagCompound compound = new NBTTagCompound();
		manager.writeToNBT(compound);
		
		NBTTagCompound dimComp = new NBTTagCompound();
		if(dimManager != null)
			dimManager.writeToNBT(dimComp);
		
		wrapper.sendTo(new MessageSyncCommon(compound, dimComp), player);
	}

}
