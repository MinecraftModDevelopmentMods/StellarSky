package stellarium.stellars;

import java.io.IOException;

import org.lwjgl.util.vector.Vector3f;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EVector;
import sciapi.api.value.euclidian.EVectorSet;
import stellarium.StellarSky;
import stellarium.api.ISkyProvider;
import stellarium.common.CommonSettings;
import stellarium.stellars.background.BrStar;
import stellarium.util.math.SpCoord;
import stellarium.util.math.Spmath;
import stellarium.util.math.VecMath;

public class StellarManager extends WorldSavedData implements ISkyProvider {
	//Render in Spherical Coordinate!
	private static final String ID = "stellarskymanagerdata";
	
	public final double AU=1.496e+8;

	private CommonSettings settings;
	private boolean locked = false;
	
	public StellarTransforms transforms = new StellarTransforms();
	
	private boolean isRemote;
	
	//Checks
	private boolean setup = false;
	private long timeOfManager;
	
	public static StellarManager loadOrCreateManager(World world) {
		WorldSavedData data = world.mapStorage.loadData(StellarManager.class, ID);
		
		if(!(data instanceof StellarManager))
		{
			StellarManager manager = new StellarManager(ID);
			world.mapStorage.setData(ID, manager);
			
			manager.loadSettingsFromConfig();
			
			data = manager;
		}
		
		return (StellarManager) data;
	}
	
	public static StellarManager getManager(World world) {
		return getManager(world.mapStorage);
	}

	public static StellarManager getManager(MapStorage mapStorage) {
		WorldSavedData data = mapStorage.loadData(StellarManager.class, ID);
		
		if(!(data instanceof StellarManager)) {
			throw new IllegalStateException(
					String.format("There is illegal data %s in storage!", data));
		}
		
		return (StellarManager)data;
	}
	
	public StellarManager(String id){
		super(id);
	}
	
	public void setRemote(boolean isRemote) {
		this.isRemote = isRemote;
	}
	
	public boolean isRemote() {
		return this.isRemote;
	}
	
	public boolean isLocked() {
		return this.locked;
	}
	
	public void lock(boolean lock) {
		this.locked = lock;
		this.markDirty();
	}
	
	public CommonSettings getSettings() {
		return this.settings;
	}
	
	//This is called on client only.
	public void readSettings(NBTTagCompound compound) {
		if(compound.hasKey("locked")) {
			this.locked = compound.getBoolean("locked");
			settings.readFromNBT(compound);
		}
	}
	
	private void loadSettingsFromConfig() {
		this.settings = new CommonSettings(StellarSky.proxy.commonSettings);
		this.markDirty();
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		this.locked = compound.getBoolean("locked");
		if(this.locked)
		{
			this.settings = new CommonSettings();
			settings.readFromNBT(compound);
		} else {
			this.loadSettingsFromConfig();
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		compound.setBoolean("locked", this.locked);
		settings.writeToNBT(compound);
	}

	
	//Initialization Fuction
	public void initialize(){

	}
	
	public double getSkyTime(double currentTick) {
		return currentTick + (settings.yearOffset * settings.year + settings.dayOffset)
				* settings.day + settings.tickOffset;
	}
	
	public boolean isSetupComplete() {
		return this.setup;
	}
	
	public long getCurrentUpdatedTime() {
		return this.timeOfManager;
	}
	
	//Update Objects
	public final void update(double time, boolean isOverWorld){
		double longitude = isOverWorld? settings.longitudeOverworld : settings.longitudeEnder;
		this.timeOfManager = (long) Math.floor(time);
		time = this.getSkyTime(time);
		
        long cur = System.currentTimeMillis();
		
		//Must be first
        transforms.update(time, longitude, isOverWorld);
		
		//Must be second
		Earth.update();
		
		for(StellarObj obj : this.planets)
			obj.update();
		
		Sun.update();
		
		if(this.isRemote && BrStar.IsInitialized)
			BrStar.UpdateAll();
		
		this.setup = true;
	}
	
	
	@Override
	public double getDayLength() {
		return settings.day;
	}

	@Override
	public double getLunarMonthLength() {
		double period = Moon.getPeriod();
		return period / (1.0 - period) * settings.year;
	}

	@Override
	public double getYearLength() {
		return settings.year;
	}

	@Override
	public double getDaytimeOffset() {
		return Spmath.fmod((this.timeOfManager + settings.tickOffset) / settings.day, 1.0) + settings.longitudeOverworld + 0.5;
	}
	
	@Override
	public double getDaytimeOffset(long tick) {
		return Spmath.fmod((tick + settings.tickOffset) / settings.day, 1.0) + settings.longitudeOverworld + 0.5;
	}

	@Override
	public double getYearlyOffset() {
		return Spmath.fmod(((this.timeOfManager + settings.tickOffset) / settings.day + settings.dayOffset) / settings.year, 1.0);
	}
	
	@Override
	public double getYearlyOffset(long tick) {
		return Spmath.fmod(((tick + settings.tickOffset) / settings.day + settings.dayOffset) / settings.year, 1.0);
	}

	@Override
	public Vector3f getCurrentSunPosition() {
    	EVector sun = EVectorSet.ins(3).getNew();
    	
    	sun.set(Sun.getAtmPos());
    	sun.set(VecMath.normalize(sun));
    	
    	return new Vector3f(sun.getCoord(0).asFloat(),
    			sun.getCoord(1).asFloat(),
    			sun.getCoord(2).asFloat());
	}

	@Override
	public Vector3f getCurrentMoonPosition() {
    	EVector moon = EVectorSet.ins(3).getNew();
    	
    	moon.set(Moon.getAtmPos());
    	moon.set(VecMath.normalize(moon));
    	
    	return new Vector3f(moon.getCoord(0).asFloat(),
    			moon.getCoord(1).asFloat(),
    			moon.getCoord(2).asFloat());
	}

	@Override
	public double getHighestSunHeightAngle() {
		IValRef pvec=(IValRef)VecMath.mult(-1.0, Earth.EcRPos);
		
		pvec=transforms.ZTEctoNEc.transform(pvec);
		pvec=transforms.EctoEq.transform(pvec);
		
		SpCoord crd = new SpCoord();
		crd.setWithVec(pvec);
		
		return 90.0 - Math.abs(settings.latitudeOverworld - crd.y);
	}

	@Override
	public double getHighestMoonHeightAngle() {
		IValRef vector = new EVector(3).set(Moon.EcRPos);
		
		vector = transforms.ZTEctoNEc.transform(vector);
		vector = transforms.EctoEq.transform(vector);
		
		SpCoord crd = new SpCoord();
		crd.setWithVec(vector);
		
		return 90.0 - Math.abs(settings.latitudeOverworld - crd.y);
	}
}
