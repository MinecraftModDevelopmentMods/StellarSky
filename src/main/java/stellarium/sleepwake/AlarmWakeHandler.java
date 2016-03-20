package stellarium.sleepwake;

import net.minecraft.world.World;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import stellarium.StellarSky;

public class AlarmWakeHandler implements IWakeHandler {
	
	private static final int DEFAULT_OFFSET = 1000;
	//Wake time from midnight
	private int wakeTime;

	@Override
	public void setupConfig(Configuration config, String category) {
		Property pWakeTime = config.get(category, "Wake_Time_from_midnight", 6000);
		pWakeTime.comment = "Wake-up time from midnight, in tick.";
		pWakeTime.setRequiresMcRestart(true);
		pWakeTime.setLanguageKey("config.property.server.waketime");
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		ConfigCategory cfgCategory = config.getCategory(category);
		this.wakeTime = cfgCategory.get("Wake_Time_from_midnight").getInt();
	}

	@Override
	public long getWakeTime(World world, long sleepTime) {
		double tickOffset = StellarSky.getManager().tickOffset;
		double dayLength = StellarSky.getManager().day;
		double longitudeEffect = StellarSky.getManager().longitudeOverworld / 360.0;
    	double modifiedWorldTime = sleepTime - sleepTime % dayLength
    			- dayLength * longitudeEffect - tickOffset - DEFAULT_OFFSET + this.wakeTime;
    	while(modifiedWorldTime < sleepTime)
    		modifiedWorldTime += dayLength;
		return (long) modifiedWorldTime;
	}

	@Override
	public boolean canSleep(World world, long sleepTime) {
		double tickOffset = StellarSky.getManager().tickOffset;
		double dayLength = StellarSky.getManager().day;
		double longitudeEffect = StellarSky.getManager().longitudeOverworld / 360.0;
    	double worldTimeOffset = sleepTime % dayLength + dayLength * longitudeEffect + tickOffset
    			- DEFAULT_OFFSET;
    	worldTimeOffset = worldTimeOffset % dayLength;
    	
    	return (!world.isDaytime()) && worldTimeOffset > 0.5;
	}

}
