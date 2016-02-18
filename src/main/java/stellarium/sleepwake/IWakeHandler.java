package stellarium.sleepwake;

import net.minecraft.world.World;
import stellarium.config.IConfigHandler;

public interface IWakeHandler extends IConfigHandler {
	
	/**
	 * Gets wake time for specific sleep time.
	 * @param world the world to control wake and sleep
	 * @param sleepTime specified sleep time in tick
	 * @return wake time in tick
	 * */
	public long getWakeTime(World world, long sleepTime);
	
	/**
	 * Determine if it is able to sleep on specific time.
	 * NOTE: Players cannot sleep on daytime even if canSleep gives true.
	 * @param world the world to control wake and sleep
	 * @param sleepTime specified sleep time in tick
	 * @return flag to determine possibility of sleep
	 * */
	public boolean canSleep(World world, long sleepTime);
	
}
