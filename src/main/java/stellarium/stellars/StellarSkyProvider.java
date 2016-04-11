package stellarium.stellars;

import org.lwjgl.util.vector.Vector3f;

import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import sciapi.api.value.euclidian.EVector;
import stellarium.api.ISkyProvider;
import stellarium.stellars.view.StellarDimensionManager;
import stellarium.util.math.SpCoord;
import stellarium.util.math.Spmath;

public class StellarSkyProvider implements ISkyProvider {

	private static final int DEFAULT_OFFSET = 1000;

	private World world;
	private WorldProvider parProvider;
	private StellarManager manager;
	private StellarDimensionManager dimManager;

	public StellarSkyProvider(World world, WorldProvider originalProvider,
			StellarManager manager, StellarDimensionManager dimManager) {
		this.world = world;
		this.parProvider = originalProvider;
		this.manager = manager;
		this.dimManager = dimManager;
	}
	
	@Override
	public double getDayLength() {
		return manager.getSettings().day;
	}

	@Override
	public double getLunarMonthLength() {
		double period = dimManager.moonFactors[0];
		return period / (1.0 - period) * manager.getSettings().year;
	}

	@Override
	public double getYearLength() {
		return manager.getSettings().year;
	}

	@Override
	public double getDaytimeOffset() {
		return this.getDaytimeOffset(world.getWorldTime());
	}
	
	@Override
	public double getDaytimeOffset(long tick) {
		return Spmath.fmod((tick + manager.getSettings().tickOffset + DEFAULT_OFFSET) / manager.getSettings().day, 1.0) + dimManager.getSettings().longitude / 360.0 + 0.5;
	}

	@Override
	public double getYearlyOffset() {
		return this.getYearlyOffset(world.getWorldTime());
	}
	
	@Override
	public double getYearlyOffset(long tick) {
		return Spmath.fmod(((tick + manager.getSettings().tickOffset + DEFAULT_OFFSET) / manager.getSettings().day + manager.getSettings().dayOffset) / manager.getSettings().year, 1.0);
	}
	
	@Override
	public double dayOffsetUntilSunReach(double heightAngle) {
		double radLatitude = Spmath.Radians(dimManager.getSettings().latitude);

		SpCoord coord = new SpCoord();
		coord.setWithVec(dimManager.sunEquatorPos);
		
		return this.hourAngleForHeight(heightAngle, Spmath.Radians(coord.y), radLatitude) / (2 * Math.PI);
	}
	
	@Override
	public double dayOffsetUntilMoonReach(double heightAngle) {
		double radLatitude = Spmath.Radians(dimManager.getSettings().latitude);

		SpCoord coord = new SpCoord();
		coord.setWithVec(dimManager.moonEquatorPos);
		
		return this.hourAngleForHeight(heightAngle, Spmath.Radians(coord.y), radLatitude) / (2 * Math.PI);
	}
	
	private double hourAngleForHeight(double heightAngle, double dec, double lat) {
		return Math.acos((Spmath.sind(heightAngle) - Math.sin(dec) * Math.sin(lat)) / (Math.cos(dec) * Math.cos(lat)));
	}

	@Override
	public Vector3f getCurrentSunPosition() {
		EVector sun = dimManager.sunAppPos.getVec();
    	
    	return new Vector3f(sun.getCoord(0).asFloat(),
    			sun.getCoord(1).asFloat(),
    			sun.getCoord(2).asFloat());
	}

	@Override
	public Vector3f getCurrentMoonPosition() {
    	EVector moon = dimManager.moonAppPos.getVec();
    	
    	return new Vector3f(moon.getCoord(0).asFloat(),
    			moon.getCoord(1).asFloat(),
    			moon.getCoord(2).asFloat());
	}

	@Override
	public double getHighestSunHeightAngle() {
		SpCoord crd = new SpCoord();
		crd.setWithVec(dimManager.sunEquatorPos);
		
		return 90.0 - Math.abs(dimManager.getSettings().latitude - crd.y);
	}

	@Override
	public double getHighestMoonHeightAngle() {
		SpCoord crd = new SpCoord();
		crd.setWithVec(dimManager.moonEquatorPos);
		
		return 90.0 - Math.abs(dimManager.getSettings().latitude - crd.y);
	}

	@Override
	public float calculateCelestialAngle(long worldTime, float partialTicks) {
		double dayLength = manager.getSettings().day;
		double longitude = dimManager.getSettings().longitude / 360.0;
		double skyTime = manager.getSkyTime(worldTime + partialTicks);
		double angle = skyTime / dayLength + longitude + 0.5;
		return (float) (angle - Math.floor(angle));
	}

	@Override
	public float calculateSunHeight(float partialTicks) {
		if(!manager.updated())
    	{
    		manager.update(world.getWorldTime() + partialTicks);
    		dimManager.update(this.world, world.getWorldTime() + partialTicks);
    	}
   	
    	return (float)(Spmath.sind(dimManager.sunAppPos.y));
	}

	@Override
	public float calculateSunlightFactor(float partialTicks) {
		return (float) ((2.0*this.calculateSunHeight(partialTicks)+0.5)*dimManager.getSettings().sunlightMultiplier);
	}

	@Override
	public float calculateSunriseSunsetFactor(float partialTicks) {
		return (float) Math.sqrt(dimManager.getSettings().sunlightMultiplier);
	}

	@Override
	public int getCurrentMoonPhase(long worldTime) {
		if(!manager.updated())
    		return parProvider.getMoonPhase(worldTime);
    	
    	return (int)(dimManager.moonFactors[2]*8);
	}

	@Override
	public float getCurrentMoonPhaseFactor() {
		if(!manager.updated())
    		return parProvider.getCurrentMoonPhaseFactor();
		return (float) dimManager.moonFactors[1];
	}

}
