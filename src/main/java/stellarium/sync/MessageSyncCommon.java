package stellarium.sync;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import stellarium.StellarForgeEventHook;
import stellarium.StellarSky;
import stellarium.stellars.StellarManager;
import stellarium.world.StellarDimensionManager;

public class MessageSyncCommon implements IMessage {

	private NBTTagCompound compoundInfo;
	private NBTTagCompound dimensionInfo;
	
	public MessageSyncCommon() { }
	
	public MessageSyncCommon(NBTTagCompound commonInfo, NBTTagCompound dimInfo) {
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
	
	public static class MessageSyncCommonHandler implements IMessageHandler<MessageSyncCommon, IMessage> {

		@Override
		public IMessage onMessage(MessageSyncCommon message, MessageContext ctx) {
			StellarManager manager = StellarManager.getManager(true);
			manager.syncFromNBT(message.compoundInfo, true);
			
			World world = StellarSky.proxy.getDefWorld();
			
			StellarDimensionManager dimManager = null;
			
			if(!message.dimensionInfo.hasNoTags())
				dimManager = StellarDimensionManager.loadOrCreate(
						world, manager, world.provider.getDimensionName());
			
			if(dimManager != null)
				dimManager.syncFromNBT(message.dimensionInfo, true);
			
			StellarForgeEventHook.setupManager(world, manager);
			if(dimManager != null)
				StellarForgeEventHook.setupDimension(world, manager, dimManager);
			
			return null;
		}
		
	}

}
