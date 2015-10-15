package stellarium.sleepwake;

import net.minecraftforge.common.config.Configuration;
import stellarium.stellars.StellarManager;

public interface IWakeHandler {
	
	/**
	 * Gets wake time for specific sleep time.
	 * @param sleepTime specified sleep time in tick
	 * @return wake time in tick
	 * */
	public int getWakeTime(int sleepTime);
	
	/**
	 * Determine if it is able to sleep on specific time.
	 * @param isDay flag for daytime
	 * @param sleepTime specified sleep time in tick
	 * @return flag to determine possibility of sleep
	 * */
	public boolean canSleep(boolean isDay, int sleepTime);
	
	/**
	 * Sets up handler using configuration.
	 * @param config the specified configuration
	 * @param category the specified category
	 * */
	public void setupConfig(Configuration config, String category);
}
