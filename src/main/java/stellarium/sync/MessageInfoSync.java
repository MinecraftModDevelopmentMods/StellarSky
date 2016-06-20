package stellarium.sync;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import stellarium.StellarAPIEventHook;
import stellarium.StellarSky;
import stellarium.stellars.StellarManager;
import stellarium.util.WorldUtil;
import stellarium.world.StellarDimensionManager;

public class MessageInfoSync implements IMessage {

	private NBTTagCompound compoundInfo;
	private NBTTagCompound dimensionInfo;
	
	public MessageInfoSync() { }
	
	public MessageInfoSync(NBTTagCompound commonInfo, NBTTagCompound dimInfo) {
		this.compoundInfo = commonInfo;
		this.dimensionInfo = dimInfo;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.compoundInfo = ByteBufUtils.readTag(buf);
		this.dimensionInfo = compoundInfo.getCompoundTag("dimension");
	}

	@Override
	public void toBytes(ByteBuf buf) {
		compoundInfo.setTag("dimension", this.dimensionInfo);
		ByteBufUtils.writeTag(buf, this.compoundInfo);
	}
	
	public static class MessageInfoSyncHandler implements IMessageHandler<MessageInfoSync, IMessage> {

		@Override
		public IMessage onMessage(final MessageInfoSync message, MessageContext ctx) {
			StellarSky.proxy.addScheduledTask(new Runnable() {
				public void run() {
					StellarManager manager = StellarManager.getClientManager();
					manager.syncFromNBT(message.compoundInfo, true);
			
					World world = StellarSky.proxy.getDefWorld();
					
					StellarDimensionManager dimManager = null;
			
					if(!message.compoundInfo.hasNoTags())
						manager.syncFromNBT(message.compoundInfo, true);

					if(!message.dimensionInfo.hasNoTags())
						dimManager = StellarDimensionManager.loadOrCreate(
								world, manager, WorldUtil.getWorldName(world));

					if(dimManager != null)
						dimManager.syncFromNBT(message.dimensionInfo, manager, true);

					manager.setup(StellarSky.proxy.getClientCelestialManager().copyFromClient());

					if(dimManager != null)
						StellarAPIEventHook.setupDimension(world, manager, dimManager);
				}
			});
			return null;
		}
		
	}

}
