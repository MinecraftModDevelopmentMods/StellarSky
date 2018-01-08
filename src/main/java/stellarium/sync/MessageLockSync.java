package stellarium.sync;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import stellarium.StellarSky;
import stellarium.stellars.StellarManager;

public class MessageLockSync implements IMessage {

	private boolean locked;
	
	public MessageLockSync() { }
	
	public MessageLockSync(boolean locked) {
		this.locked = locked;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.locked = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(this.locked);
	}
	
	public static class MessageLockSyncHandler implements IMessageHandler<MessageLockSync, IMessage> {

		@Override
		public IMessage onMessage(final MessageLockSync message, MessageContext ctx) {
			StellarSky.proxy.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					StellarManager.getManager(StellarSky.proxy.getDefWorld()).setLocked(message.locked);
				}
			});
			return null;
		}
		
	}

}
