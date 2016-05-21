package stellarium.sync;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
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
		wrapper.registerMessage(MessageLockSync.MessageLockSyncHandler.class,
				MessageLockSync.class, 2, Side.CLIENT);
	}

	public void queryInformation(World world) {
		wrapper.sendToServer(new MessageInfoQuery(world.provider.getDimension()));
	}
	
	public String getID() {
		return this.id;
	}
	
	public void sendSyncInformation(EntityPlayerMP player) {
		wrapper.sendTo(this.onQueryInformation(player.mcServer, player.dimension), player);
	}

	public MessageInfoSync onQueryInformation(MinecraftServer server, int dimensionId) {
		WorldServer world = server.worldServerForDimension(dimensionId);
		
		StellarManager manager = StellarManager.getServerManager(server);
		StellarDimensionManager dimManager = StellarDimensionManager.get(world);
		
		NBTTagCompound compound = new NBTTagCompound();
		manager.writeToNBT(compound);
		
		NBTTagCompound dimComp = new NBTTagCompound();
		if(dimManager != null)
			dimManager.writeToNBT(dimComp);
		
		return new MessageInfoSync(compound, dimComp);
	}

	public void sendLockInformation(boolean lock) {
		wrapper.sendToAll(new MessageLockSync(lock));
	}

}
