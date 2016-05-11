package stellarium.sync;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
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
		public IMessage onMessage(MessageLockSync message, MessageContext ctx) {
			StellarManager.getClientManager().setLocked(message.locked);

			return null;
		}
		
	}

}
