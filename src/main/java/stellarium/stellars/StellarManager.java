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
	
	private static final String ID = "stellarskymanagerdata";
	
	public final double AU=1.496e+8;

	public Sun Sun = new Sun();
	public Earth Earth = new Earth();
	public Moon Moon = new Moon();
	
	private Planet Mercury = new Planet();
	private Planet Venus = new Planet();
	private Planet Mars = new Planet();
	private Planet Jupiter = new Planet();
	private Planet Saturn = new Planet();
	private Planet Uranus = new Planet();
	private Planet Neptune = new Planet();
	
	public Planet[] planets = {Mercury, Venus, Mars, Jupiter, Saturn, Uranus, Neptune};
	
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
			StellarManager manager = new StellarManager();
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
	
	public StellarManager() {
		super(ID);
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
	
	public void lock() {
		this.locked = true;
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
	
	
	public Planet[] getPlanets() {
		return this.planets;
	}
	
	//Initialization Fuction
	public void initialize(){
		System.out.println("[Stellarium]: "+"Initializing Stellar Transforms...");
		transforms.setup(this);
		
		////Solar System
		System.out.println("[Stellarium]: "+"Initializing Solar System...");
		///Sun
		System.out.println("[Stellarium]: "+"Initializing Sun...");
		Sun.radius=0.00465469;
		Sun.mass=1.0;
		Sun.initialize(this);
		
		///Earth System
		//Declaration
		System.out.println("[Stellarium]: "+"Initializing Earth...");
		Earth.addSatellite(this.Moon);
		Earth.radius.set(4.2634e-5);
		Earth.mass=3.002458398e-6;
		Moon.radius.set(4e-5 * settings.moonSizeMultiplier);
		
		//Initialization
		//-Earth
		Earth.a0=1.00000018;
		Earth.e0=0.01673163;
		Earth.I0=-0.00054346;
		Earth.L0=100.4669157;
		Earth.wbar0=102.9300589;
		Earth.Omega0=-5.11260389;
		Earth.ad=-0.00000003;
		Earth.ed=-0.00003661;
		Earth.Id=-0.01337178;
		Earth.Ld=35999.37306;
		Earth.wbard=0.3179526;
		Earth.Omegad=-0.24123856;
		
		//-Moon
		System.out.println("[Stellarium]: "+"Initializing Moon...");
		Moon.albedo=0.12 * settings.moonBrightnessMultiplier;
		Moon.a0=0.00257184;
		Moon.e0=0.0549006;
		Moon.I0=5.14;
		Moon.w0=318.15;
		Moon.Omega0=125.08;
		Moon.M0_0=135.27;
		Moon.wd=40.678;
		Moon.Omegad=-19.355;
		
		//Earth Initialize
		Earth.initialize(this);
		
		///Planets
		//Mercury
		System.out.println("[Stellarium]: "+"Initializing Mercury...");
		Mercury.albedo=0.119;
		Mercury.radius.set(1.630815508e-5);
		Mercury.mass=1.660147806e-7;
		Mercury.a0=0.38709843;
		Mercury.e0=0.20563661;
		Mercury.I0=7.00559432;
		Mercury.L0=252.2516672;
		Mercury.wbar0=77.45771895;
		Mercury.Omega0=48.33961819;
		Mercury.ad=0.0;
		Mercury.ed=0.00002123;
		Mercury.Id=-0.00590158;
		Mercury.Ld=149472.6749;
		Mercury.wbard=0.15940013;
		Mercury.Omegad=-0.12214182;
		
		Mercury.initialize(this);
		
		//Venus
		System.out.println("[Stellarium]: "+"Initizlizing Venus...");
		Venus.albedo=0.90;
		Venus.radius.set(4.0453208556e-5);
		Venus.mass=2.447589362e-6;
		Venus.a0=0.72332102;
		Venus.e0=0.00676399;
		Venus.I0=3.39777545;
		Venus.L0=181.9797085;
		Venus.wbar0=131.7675571;
		Venus.Omega0=76.67261496;
		Venus.ad=-0.00000026;
		Venus.ed=-0.00005107;
		Venus.Id=0.00043494;
		Venus.Ld=58571.8156;
		Venus.wbard=0.05679648;
		Venus.Omegad=-0.27274174;
		
		Venus.initialize(this);
		
		//Mars
		System.out.println("[Stellarium]: "+"Initializing Mars...");
		Mars.albedo=0.25;
		Mars.radius.set(2.26604278e-5);
		Mars.mass=3.22683626e-7;
		Mars.a0=1.52371243;
		Mars.e0=0.09336511;
		Mars.I0=1.85181869;
		Mars.L0=-4.56813164;
		Mars.wbar0=-23.91744784;
		Mars.Omega0=49.71320984;
		Mars.ad=0.00000097;
		Mars.ed=0.00009149;
		Mars.Id=-0.00724757;
		Mars.Ld=19140.29934;
		Mars.wbard=0.45223625;
		Mars.Omegad=-0.26852431;
		
		Mars.initialize(this);
		
		//Jupiter
		System.out.println("[Stellarium]: "+"Initializing Jupiter...");
		Jupiter.albedo=0.343;
		Jupiter.radius.set(4.673195187e-4);
		Jupiter.mass=9.54502036e-4;
		Jupiter.a0=5.20248019;
		Jupiter.e0=0.0485359;
		Jupiter.I0=1.29861416;
		Jupiter.L0=34.33479152;
		Jupiter.wbar0=14.27495244;
		Jupiter.Omega0=100.2928265;
		Jupiter.ad=-0.00002864;
		Jupiter.ed=0.00018026;
		Jupiter.Id=-0.00322699;
		Jupiter.Ld=3034.903718;
		Jupiter.wbard=0.18199196;
		Jupiter.Omegad=0.13024619;
		Jupiter.b=-0.00012452;
		Jupiter.c=0.0606406;
		Jupiter.s=-0.35635438;
		Jupiter.f=38.35125;
		
		Jupiter.initialize(this);
		
		//Saturn
		System.out.println("[Stellarium]: "+"Initializing Saturn...");
		Saturn.albedo=0.342;
		Saturn.radius.set(3.83128342e-4);
		Saturn.mass=2.8578754e-4;
		Saturn.a0=9.54149883;
		Saturn.e0=0.05550825;
		Saturn.I0=2.49424102;
		Saturn.L0=50.07571329;
		Saturn.wbar0=92.86136063;
		Saturn.Omega0=113.639987;
		Saturn.ad=-0.00003065;
		Saturn.ed=-0.00032044;
		Saturn.Id=0.00451969;
		Saturn.Ld=1222.114947;
		Saturn.wbard=0.54179478;
		Saturn.Omegad=-0.25015002;
		Saturn.b=0.00025899;
		Saturn.c=-0.13434469;
		Saturn.s=0.87320147;
		Saturn.f=38.35125;
		
		Saturn.initialize(this);
		
		//Uranus
		System.out.println("[Stellarium]: "+"Initializing Uranus...");
		Uranus.albedo=0.300;
		Uranus.radius.set(1.68890374e-4);
		Uranus.mass=4.3642853557e-5;
		Uranus.a0=19.1897948;
		Uranus.e0=0.0468574;
		Uranus.I0=0.77298127;
		Uranus.L0=314.2027663;
		Uranus.wbar0=172.4340444;
		Uranus.Omega0=73.96250215;
		Uranus.ad=-0.00020455;
		Uranus.ed=-0.0000155;
		Uranus.Id=-0.00180155;
		Uranus.Ld=428.495126;
		Uranus.wbard=0.09266985;
		Uranus.Omegad=0.05749699;
		Uranus.b=0.00058331;
		Uranus.c=-0.97731848;
		Uranus.s=0.17689245;
		Uranus.f=7.67025;
		
		Uranus.initialize(this);
		
		//Neptune
		System.out.println("[Stellarium]: "+"Initializing Neptune...");
		Neptune.albedo=0.290;
		Neptune.radius.set(1.641209893e-4);
		Neptune.mass=5.14956513e-5;
		Neptune.a0=30.06952752;
		Neptune.e0=0.00895439;
		Neptune.I0=1.7700552;
		Neptune.L0=304.2228929;
		Neptune.wbar0=46.68158724;
		Neptune.Omega0=131.7863585;
		Neptune.ad=0.00006447;
		Neptune.ed=0.00000818;
		Neptune.Id=0.000224;
		Neptune.Ld=218.4651531;
		Neptune.wbard=0.01009938;
		Neptune.Omegad=-0.00606302;
		Neptune.b=-0.00041348;
		Neptune.c=0.68346318;
		Neptune.s=-0.10162547;
		Neptune.f=7.67025;
		
		Neptune.initialize(this);
		
		System.out.println("[Stellarium]: "+"Solar System Initialized!");

		if(this.isRemote && BrStar.IsInitialized)
			BrStar.initializeAll(this);
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
