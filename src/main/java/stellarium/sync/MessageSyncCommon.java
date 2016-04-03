package stellarium.sync;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import stellarium.StellarEventHook;
import stellarium.StellarSky;
import stellarium.stellars.StellarManager;
import stellarium.stellars.view.StellarDimensionManager;

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
		this.dimensionInfo = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, this.compoundInfo);
		ByteBufUtils.writeTag(buf, this.dimensionInfo);
	}
	
	public static class MessageSyncCommonHandler implements IMessageHandler<MessageSyncCommon, IMessage> {

		@Override
		public IMessage onMessage(MessageSyncCommon message, MessageContext ctx) {
			StellarManager manager = StellarManager.getManager(true);
			manager.syncFromNBT(message.compoundInfo, true);
			
			StellarDimensionManager dimManager = StellarDimensionManager.get(StellarSky.proxy.getDefWorld());
			if(dimManager != null)
				dimManager.syncFromNBT(message.compoundInfo, true);
			
			StellarEventHook.setupManager(StellarSky.proxy.getDefWorld(), manager);
			if(dimManager != null)
				StellarEventHook.setupDimension(StellarSky.proxy.getDefWorld(), manager, dimManager);
			
			return null;
		}
		
	}

}
