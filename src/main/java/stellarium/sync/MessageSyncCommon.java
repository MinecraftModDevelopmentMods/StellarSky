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

public class MessageSyncCommon implements IMessage {

	private NBTTagCompound compoundInfo;
	
	public MessageSyncCommon() { }
	
	public MessageSyncCommon(NBTTagCompound info) {
		this.compoundInfo = info;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.compoundInfo = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, this.compoundInfo);
	}
	
	public static class MessageSyncCommonHandler implements IMessageHandler<MessageSyncCommon, IMessage> {

		@Override
		public IMessage onMessage(MessageSyncCommon message, MessageContext ctx) {
			StellarManager manager = StellarManager.getManager(ctx.getClientHandler().mapStorageOrigin);
			if(manager != null)
				manager.readSettings(message.compoundInfo);
			
			StellarEventHook.setupManager(StellarSky.proxy.getDefWorld(), manager);
			
			return null;
		}
		
	}

}
