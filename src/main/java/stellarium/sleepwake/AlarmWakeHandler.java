package stellarium.sleepwake;

import net.minecraft.world.World;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import stellarium.api.ISkyProvider;

public class AlarmWakeHandler implements IWakeHandler {

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
	public long getWakeTime(World world, ISkyProvider skyProvider, long sleepTime) {
		double currentOffset = skyProvider.getDaytimeOffset(sleepTime);
		double dayLength = skyProvider.getDayLength();
		double modifiedWorldTime = this.wakeTime - (0.25 + currentOffset) * dayLength;
    	while(modifiedWorldTime < sleepTime)
    		modifiedWorldTime += dayLength;
		return (long) modifiedWorldTime;
	}

	@Override
	public boolean canSleep(World world, ISkyProvider skyProvider, long sleepTime) {    	
    	return !world.isDaytime() && skyProvider.getDaytimeOffset(sleepTime) > 0.5;
	}

}
