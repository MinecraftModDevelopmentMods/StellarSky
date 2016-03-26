package stellarium.sleepwake;

import net.minecraft.world.World;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import sciapi.api.value.IValRef;
import stellarium.common.CommonSettings;
import stellarium.stellars.StellarManager;
import stellarium.util.math.SpCoord;
import stellarium.util.math.Spmath;
import stellarium.util.math.VecMath;

public class LightWakeHandler implements IWakeHandler {

	private static final int DEFAULT_OFFSET = 1000;
	private double sinWakeAngle;
	
	@Override
	public long getWakeTime(World world, StellarManager manager, long sleepTime) {
		CommonSettings settings = manager.getSettings();
		double tickOffset = settings.tickOffset;
		double dayLength = settings.day;
		double longitudeEffect = settings.longitudeOverworld / 360.0;
		double wakeTimeFromNoon = this.wakeHourAngle(manager) / (2.0 * Math.PI) * dayLength;
    	double modifiedWorldTime = sleepTime - sleepTime % dayLength
    			- dayLength * (longitudeEffect - 0.5) - tickOffset - wakeTimeFromNoon - DEFAULT_OFFSET;
    	while(modifiedWorldTime < sleepTime)
    		modifiedWorldTime += dayLength;
		return (long) modifiedWorldTime;
	}

	@Override
	public boolean canSleep(World world, StellarManager manager, long sleepTime) {
		return !world.isDaytime();
	}

	@Override
	public void setupConfig(Configuration config, String category) {
		Property wakeAngle = config.get(category, "Sun_Height_for_Wake", 10.0);
		wakeAngle.setComment("Solar azimuth(height) angle to wake up. (in degrees)");
		wakeAngle.setRequiresMcRestart(true);
		wakeAngle.setLanguageKey("config.property.server.wakeangle");
	}
	

	@Override
	public void loadFromConfig(Configuration config, String category) {
		ConfigCategory cfgCategory = config.getCategory(category);
		this.sinWakeAngle = Spmath.sind(cfgCategory.get("Sun_Height_for_Wake").getDouble());
	}
	
	private double wakeHourAngle(StellarManager manager) {
		double radLatitude = Spmath.Radians(manager.getSettings().latitudeOverworld);
		
		IValRef pvec=(IValRef)VecMath.mult(-1.0, manager.Earth.EcRPos);
		pvec = manager.transforms.ZTEctoNEc.transform(pvec);
		pvec = manager.transforms.EctoEq.transform(pvec);
		SpCoord coord = new SpCoord();
		coord.setWithVec(pvec);
		
		return this.wakeHourAngle(Spmath.Radians(coord.y), radLatitude);
	}
	
	private double wakeHourAngle(double dec, double lat) {
		return Math.acos((this.sinWakeAngle - Math.sin(dec) * Math.sin(lat)) / (Math.cos(dec) * Math.cos(lat)));
	}

}
