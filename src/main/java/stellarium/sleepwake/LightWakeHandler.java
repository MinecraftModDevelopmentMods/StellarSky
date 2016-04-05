package stellarium.sleepwake;

import net.minecraft.world.World;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import stellarium.api.ISkyProvider;

public class LightWakeHandler implements IWakeHandler {

	private static final int DEFAULT_OFFSET = 1000;
	private double wakeAngle;
	
	@Override
	public long getWakeTime(World world, ISkyProvider skyProvider, long sleepTime) {
		double wakeDayOffset = skyProvider.dayOffsetUntilSunReach(this.wakeAngle);
		double currentDayOffset = skyProvider.getDaytimeOffset(sleepTime);
		double dayLength = skyProvider.getDayLength();

    	double modifiedWorldTime = sleepTime + (-wakeDayOffset-currentDayOffset) * dayLength;
    	while(modifiedWorldTime < sleepTime)
    		modifiedWorldTime += dayLength;
		return (long) modifiedWorldTime;
	}

	@Override
	public boolean canSleep(World world, ISkyProvider skyProvider, long sleepTime) {
		return !world.isDaytime();
	}

	@Override
	public void setupConfig(Configuration config, String category) {
		Property wakeAngle = config.get(category, "Sun_Height_for_Wake", 10.0);
		wakeAngle.comment = "Solar azimuth(height) angle to wake up. (in degrees)";
		wakeAngle.setRequiresMcRestart(true);
		wakeAngle.setLanguageKey("config.property.server.wakeangle");
	}
	

	@Override
	public void loadFromConfig(Configuration config, String category) {
		ConfigCategory cfgCategory = config.getCategory(category);
		this.wakeAngle = cfgCategory.get("Sun_Height_for_Wake").getDouble();
	}

}
