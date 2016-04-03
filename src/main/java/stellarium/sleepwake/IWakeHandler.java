package stellarium.sleepwake;

import net.minecraft.world.World;
import stellarium.api.ISkyProvider;
import stellarium.config.IConfigHandler;
import stellarium.stellars.StellarManager;

public interface IWakeHandler extends IConfigHandler {
	
	/**
	 * Gets wake time for specific sleep time.
	 * @param world the world to control wake and sleep
	 * @param skyProvider the sky provider for the world
	 * @param sleepTime specified sleep time in tick
	 * @return wake time in tick
	 * */
	public long getWakeTime(World world, ISkyProvider skyProvider, long sleepTime);
	
	/**
	 * Determine if it is able to sleep on specific time.
	 * @param world the world to control wake and sleep
	 *  @param skyProvider the sky provider for the world
	 * @param sleepTime specified sleep time in tick
	 * @return flag to determine possibility of sleep
	 * */
	public boolean canSleep(World world, ISkyProvider skyProvider, long sleepTime);
	
}
