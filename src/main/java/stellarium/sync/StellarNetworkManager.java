package stellarium.sync;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import stellarium.stellars.StellarManager;
import stellarium.world.StellarDimensionManager;

public final class StellarNetworkManager {
	
	private SimpleNetworkWrapper wrapper;
	private String id = "stellarskychannel";
	
	public StellarNetworkManager() {
		this.wrapper = NetworkRegistry.INSTANCE.newSimpleChannel(this.id);
		
		wrapper.registerMessage(MessageInfoSync.MessageInfoSyncHandler.class,
				MessageInfoSync.class, 0, Side.CLIENT);
		wrapper.registerMessage(MessageInfoQuery.MessageInfoQueryHandler.class,
				MessageInfoQuery.class, 1, Side.SERVER);
	}

	public void queryInformation(World world) {
		wrapper.sendToServer(new MessageInfoQuery(world.provider.dimensionId));
	}
	
	public String getID() {
		return this.id;
	}
	
	public void sendSyncInformation(EntityPlayerMP player) {
		wrapper.sendTo(this.onQueryInformation(player.dimension), player);
	}

	public MessageInfoSync onQueryInformation(int dimensionId) {
		WorldServer world = MinecraftServer.getServer().worldServerForDimension(dimensionId);
		
		StellarManager manager = StellarManager.getManager(false);
		StellarDimensionManager dimManager = StellarDimensionManager.get(world);
		
		NBTTagCompound compound = new NBTTagCompound();
		manager.writeToNBT(compound);
		
		NBTTagCompound dimComp = new NBTTagCompound();
		if(dimManager != null)
			dimManager.writeToNBT(dimComp);
		
		return new MessageInfoSync(compound, dimComp);
	}

}
