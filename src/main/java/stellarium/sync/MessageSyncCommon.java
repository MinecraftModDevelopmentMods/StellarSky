package stellarium.sync;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
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
		public IMessage onMessage(final MessageSyncCommon message, MessageContext ctx) {
			StellarSky.proxy.addScheduledTask(new Runnable() {
				public void run() {
					StellarManager manager = StellarManager.getManager(StellarSky.proxy.getDefWorld().getMapStorage());
					if(manager != null)
						manager.readSettings(message.compoundInfo);
					
					StellarEventHook.setupManager(StellarSky.proxy.getDefWorld(), manager);
				}
			});
			return null;
		}
	}
}
