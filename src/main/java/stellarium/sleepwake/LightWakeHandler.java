package stellarium.sleepwake;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import sciapi.api.value.IValRef;
import stellarium.StellarSky;
import stellarium.util.math.SpCoord;
import stellarium.util.math.Spmath;
import stellarium.util.math.Transforms;
import stellarium.util.math.VecMath;

public class LightWakeHandler implements IWakeHandler {

	private double wakeAngle;
	
	@Override
	public int getWakeTime(int sleepTime) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean canSleep(boolean isDay, int sleepTime) {
		return isDay;
	}

	@Override
	public void setupConfig(Configuration config, String category) {
		//Property lightWakeEnabled = config.get(category, key, defaultValue);
		
		// TODO Auto-generated method stub
		Property wakeAngle = config.get(category, "Sun_Height_for_Wake", 10.0);
		wakeAngle.comment = "Solar azimuth(height) angle to wake up. (in degrees)";
		wakeAngle.setRequiresMcRestart(true);
		wakeAngle.setLanguageKey("config.property.server.wakeangle");
		this.wakeAngle = Spmath.Radians(wakeAngle.getDouble());
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
		return Math.acos((Math.sin(this.wakeAngle) - Math.sin(dec) * Math.sin(lat)) / (Math.cos(dec) * Math.cos(lat)));
	}

}
