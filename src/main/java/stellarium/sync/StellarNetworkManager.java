package stellarium.sync;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public final class StellarNetworkManager {
	
	private SimpleNetworkWrapper wrapper;
	private String id = "stellarskychannel";
	
	public StellarNetworkManager() {
		this.wrapper = NetworkRegistry.INSTANCE.newSimpleChannel(this.id);

		wrapper.registerMessage(MessageLockSync.MessageLockSyncHandler.class,
				MessageLockSync.class, 0, Side.CLIENT);
	}

	public String getID() {
		return this.id;
	}

	public void sendLockInformation(boolean lock) {
		wrapper.sendToAll(new MessageLockSync(lock));
	}

}
