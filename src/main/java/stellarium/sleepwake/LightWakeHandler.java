package stellarium.sleepwake;

import net.minecraft.world.World;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import sciapi.api.value.IValRef;
import stellarium.StellarSky;
import stellarium.util.math.SpCoord;
import stellarium.util.math.Spmath;
import stellarium.util.math.Transforms;
import stellarium.util.math.VecMath;

public class LightWakeHandler implements IWakeHandler {

	private double sinWakeAngle;
	
	@Override
	public long getWakeTime(World world, long sleepTime) {
		double tickOffset = StellarSky.getManager().tickOffset;
		double dayLength = StellarSky.getManager().day;
		double longitudeEffect = StellarSky.getManager().longitudeOverworld / 360.0;
		double wakeTimeFromNoon = this.wakeHourAngle() / (2.0 * Math.PI) * dayLength;
    	double modifiedWorldTime = sleepTime - sleepTime % dayLength
    			- dayLength * (longitudeEffect - 0.5) - tickOffset - wakeTimeFromNoon;
    	while(modifiedWorldTime < sleepTime)
    		modifiedWorldTime += dayLength;
		return (long) modifiedWorldTime;
	}

	@Override
	public boolean canSleep(World world, int sleepTime) {
		return !world.isDaytime() && Spmath.fmod(world.getCelestialAngle(1.0f) - 0.75f, 1.0f) > 0.5f;
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
		this.sinWakeAngle = Spmath.sind(cfgCategory.get("Sun_Height_for_Wake").getDouble());
	}
	
	private double wakeHourAngle() {
		double radLatitude = Spmath.Radians(StellarSky.getManager().lattitudeOverworld);
		
		IValRef pvec=(IValRef)VecMath.mult(-1.0, StellarSky.getManager().Earth.EcRPos);
		pvec = Transforms.ZTEctoNEc.transform(pvec);
		pvec = Transforms.EctoEq.transform(pvec);
		SpCoord coord = new SpCoord();
		coord.setWithVec(pvec);
		
		return this.wakeHourAngle(coord.y, radLatitude);
	}
	
	private double wakeHourAngle(double dec, double lat) {
		return Math.acos((this.sinWakeAngle - Math.sin(dec) * Math.sin(lat)) / (Math.cos(dec) * Math.cos(lat)));
	}

}
