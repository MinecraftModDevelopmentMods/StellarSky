package stellarium.sync;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import stellarium.StellarSky;

public class MessageInfoQuery implements IMessage {

	private int dimensionId;
	
	public MessageInfoQuery() { }
	
	public MessageInfoQuery(int dimensionId) {
		this.dimensionId = dimensionId;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.dimensionId = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.dimensionId);
	}
	
	public static class MessageInfoQueryHandler implements IMessageHandler<MessageInfoQuery, IMessage> {

		@Override
		public IMessage onMessage(MessageInfoQuery message, MessageContext ctx) {
			//Just a callback, will be okay
			return StellarSky.instance.getNetworkManager().onQueryInformation(
					ctx.getServerHandler().playerEntity.mcServer,
					message.dimensionId);
		}
		
	}

}
