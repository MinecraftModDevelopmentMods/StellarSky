package stellarium.sleepwake;

import net.minecraft.world.World;
import stellarium.config.IConfigHandler;
import stellarium.stellars.StellarManager;

public interface IWakeHandler extends IConfigHandler {
	
	/**
	 * Gets wake time for specific sleep time.
	 * @param world the world to control wake and sleep
	 * @param manager the stellar manager for the world
	 * @param sleepTime specified sleep time in tick
	 * @return wake time in tick
	 * */
	public long getWakeTime(World world, StellarManager manager, long sleepTime);
	
	/**
	 * Determine if it is able to sleep on specific time.
	 * @param world the world to control wake and sleep
	 * @param manager the stellar manager for the world
	 * @param sleepTime specified sleep time in tick
	 * @return flag to determine possibility of sleep
	 * */
	public boolean canSleep(World world, StellarManager manager, long sleepTime);
	
}
