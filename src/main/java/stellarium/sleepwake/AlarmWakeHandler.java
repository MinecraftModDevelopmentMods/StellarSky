package stellarium.sleepwake;

import net.minecraft.world.World;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import stellarium.StellarSky;
import stellarium.util.math.Spmath;

public class AlarmWakeHandler implements IWakeHandler {
	
	private static final double DEFAULT_START_FROM_MIDNIGHT = 5000.0;
	//Wake time from midnight
	private int wakeTime;

	@Override
	public void setupConfig(Configuration config, String category) {
		Property pWakeTime = config.get(category, "Wake_Time_from_midnight", 10.0);
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
    			- dayLength * longitudeEffect - tickOffset + this.wakeTime;
    	while(modifiedWorldTime < sleepTime)
    		modifiedWorldTime += dayLength;
		return (long) modifiedWorldTime;
	}

	@Override
	public boolean canSleep(World world, int sleepTime) {
		double tickOffset = StellarSky.getManager().tickOffset;
		double dayLength = StellarSky.getManager().day;
		double longitudeEffect = StellarSky.getManager().longitudeOverworld / 360.0;
    	double worldTimeOffset = sleepTime % dayLength + dayLength * longitudeEffect + tickOffset;
    	worldTimeOffset = worldTimeOffset % dayLength;
    	
    	return !world.isDaytime() && worldTimeOffset > 0.5;
	}

}
