package stellarium.sync;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

@Deprecated
public class MessageLock implements IMessage {

	@Override
	public void fromBytes(ByteBuf buf) { }

	@Override
	public void toBytes(ByteBuf buf) { }
	
	public static class MessageLockHandler implements IMessageHandler<MessageLock, IMessage> {

		@Override
		public IMessage onMessage(MessageLock message, MessageContext ctx) {
			EntityPlayer player = ctx.getServerHandler().playerEntity;
			
			return null;
		}
		
	}

}
