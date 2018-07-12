package stellarium.stellars.system;

import java.io.IOException;
import java.util.Collection;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarapi.api.celestials.EnumCollectionType;
import stellarium.StellarSky;
import stellarium.StellarSkyReferences;
import stellarium.stellars.layer.StellarCollection;
import stellarium.stellars.layer.StellarLayer;
import stellarium.stellars.render.ICelestialLayerRenderer;

public class LayerSolarSystem extends StellarLayer<SolarObject, SolarSystemClientSettings, SolarSystemSettings> {
	public LayerSolarSystem() {
		super(new ResourceLocation(StellarSkyReferences.MODID, "solarsystem"),
				EnumCollectionType.System, 0);
	}

	protected static int planetRenderId = -1;
	protected static int sunRenderId = -1;
	protected static int moonRenderId = -1;

	public final double AU=1.496e+8;

	@Override
	public void initializeCommon(SolarSystemSettings settings, StellarCollection<SolarObject> container) throws IOException {		
		double yearUnit = container.getManager().getYearPeriod().getPeriodLength();

		////Solar System
		StellarSky.INSTANCE.getLogger().info("Initializing Solar System...");
		///Sun
		StellarSky.INSTANCE.getLogger().info("Initializing Sun...");
		Sun sun = new Sun("sun", yearUnit);
		sun.radius=0.00465469 * 4;
		sun.mass=1.0;
		sun.initialize();
		container.loadObject("sun", sun);
		container.loadObject("system", sun);
		container.addRenderCache(sun, new SunRenderCache());

		///Earth System
		//Declaration		
		StellarSky.INSTANCE.getLogger().info("Initializing Earth...");
		Earth earth = new Earth("earth", sun, yearUnit);
		Moon moon = new Moon("moon", earth, yearUnit);

		earth.radius=4.2634e-5;
		earth.mass=3.002458398e-6;
		moon.radius = 4 * 1.158e-5 * settings.propMoonSize.getDouble();

		//Initialization
		//-Earth
		earth.a0=1.00000018;
		earth.e0=0.01673163;
		earth.I0=-0.00054346;
		earth.L0=100.4669157;
		earth.wbar0=102.9300589;
		earth.Omega0=-5.11260389;
		earth.ad=-0.00000003;
		earth.ed=-0.00003661;
		earth.Id=-0.01337178;
		earth.Ld=35999.37306;
		earth.wbard=0.3179526;
		earth.Omegad=-0.24123856;

		//-Moon
		StellarSky.INSTANCE.getLogger().info("Initializing Moon...");
		moon.albedo=0.12;
		moon.brightnessFactor = settings.propMoonBrightness.getDouble();
		moon.a0=0.00257184;
		moon.e0=0.0549006;
		moon.I0=5.14;
		moon.w0=318.15;
		moon.Omega0=125.08;
		moon.M0_0=135.27;
		moon.wd=40.678;
		moon.Omegad=-19.355;

		//Earth Initialize
		earth.initialize();
		container.loadObject("earth", earth);
		container.loadObject("system", earth);

		//Moon Initialize
		moon.initialize();
		container.loadObject("moon", moon);
		container.loadObject("system", moon);
		container.addRenderCache(moon, new MoonRenderCache());

		///Planets
		//Mercury
		StellarSky.INSTANCE.getLogger().info("Initializing Mercury...");
		Planet mercury = new Planet("mercury", sun, yearUnit);
		mercury.albedo=0.119;
		mercury.radius=1.630815508e-5;
		mercury.mass=1.660147806e-7;
		mercury.a0=0.38709843;
		mercury.e0=0.20563661;
		mercury.I0=7.00559432;
		mercury.L0=252.2516672;
		mercury.wbar0=77.45771895;
		mercury.Omega0=48.33961819;
		mercury.ad=0.0;
		mercury.ed=0.00002123;
		mercury.Id=-0.00590158;
		mercury.Ld=149472.6749;
		mercury.wbard=0.15940013;
		mercury.Omegad=-0.12214182;

		mercury.initialize();
		container.loadObject("system", mercury);
		container.addRenderCache(mercury, new PlanetRenderCache());

		//Venus
		StellarSky.INSTANCE.getLogger().info("Initizlizing Venus...");
		Planet venus = new Planet("venus", sun, yearUnit);
		venus.albedo=0.90;
		venus.radius=4.0453208556e-5;
		venus.mass=2.447589362e-6;
		venus.a0=0.72332102;
		venus.e0=0.00676399;
		venus.I0=3.39777545;
		venus.L0=181.9797085;
		venus.wbar0=131.7675571;
		venus.Omega0=76.67261496;
		venus.ad=-0.00000026;
		venus.ed=-0.00005107;
		venus.Id=0.00043494;
		venus.Ld=58571.8156;
		venus.wbard=0.05679648;
		venus.Omegad=-0.27274174;

		venus.initialize();
		container.loadObject("system", venus);
		container.addRenderCache(venus, new PlanetRenderCache());

		//Mars
		StellarSky.INSTANCE.getLogger().info("Initializing Mars...");
		Planet mars = new Planet("mars", sun, yearUnit);
		mars.albedo=0.25;
		mars.radius=2.26604278e-5;
		mars.mass=3.22683626e-7;
		mars.a0=1.52371243;
		mars.e0=0.09336511;
		mars.I0=1.85181869;
		mars.L0=-4.56813164;
		mars.wbar0=-23.91744784;
		mars.Omega0=49.71320984;
		mars.ad=0.00000097;
		mars.ed=0.00009149;
		mars.Id=-0.00724757;
		mars.Ld=19140.29934;
		mars.wbard=0.45223625;
		mars.Omegad=-0.26852431;

		mars.initialize();
		container.loadObject("system", mars);
		container.addRenderCache(mars, new PlanetRenderCache());

		//Jupiter
		StellarSky.INSTANCE.getLogger().info("Initializing Jupiter...");
		Planet jupiter = new Planet("jupiter", sun, yearUnit);
		jupiter.albedo=0.343;
		jupiter.radius=4.673195187e-4;
		jupiter.mass=9.54502036e-4;
		jupiter.a0=5.20248019;
		jupiter.e0=0.0485359;
		jupiter.I0=1.29861416;
		jupiter.L0=34.33479152;
		jupiter.wbar0=14.27495244;
		jupiter.Omega0=100.2928265;
		jupiter.ad=-0.00002864;
		jupiter.ed=0.00018026;
		jupiter.Id=-0.00322699;
		jupiter.Ld=3034.903718;
		jupiter.wbard=0.18199196;
		jupiter.Omegad=0.13024619;
		jupiter.b=-0.00012452;
		jupiter.c=0.0606406;
		jupiter.s=-0.35635438;
		jupiter.f=38.35125;

		jupiter.initialize();
		container.loadObject("system", jupiter);
		container.addRenderCache(jupiter, new PlanetRenderCache());

		//Saturn
		StellarSky.INSTANCE.getLogger().info("Initializing Saturn...");
		Planet saturn = new Planet("saturn", sun, yearUnit);
		saturn.albedo=0.342;
		saturn.radius=3.83128342e-4;
		saturn.mass=2.8578754e-4;
		saturn.a0=9.54149883;
		saturn.e0=0.05550825;
		saturn.I0=2.49424102;
		saturn.L0=50.07571329;
		saturn.wbar0=92.86136063;
		saturn.Omega0=113.639987;
		saturn.ad=-0.00003065;
		saturn.ed=-0.00032044;
		saturn.Id=0.00451969;
		saturn.Ld=1222.114947;
		saturn.wbard=0.54179478;
		saturn.Omegad=-0.25015002;
		saturn.b=0.00025899;
		saturn.c=-0.13434469;
		saturn.s=0.87320147;
		saturn.f=38.35125;

		saturn.initialize();
		container.loadObject("system", saturn);
		container.addRenderCache(saturn, new PlanetRenderCache());

		//Uranus
		StellarSky.INSTANCE.getLogger().info("Initializing Uranus...");
		Planet uranus = new Planet("uranus", sun, yearUnit);
		uranus.albedo=0.300;
		uranus.radius=1.68890374e-4;
		uranus.mass=4.3642853557e-5;
		uranus.a0=19.1897948;
		uranus.e0=0.0468574;
		uranus.I0=0.77298127;
		uranus.L0=314.2027663;
		uranus.wbar0=172.4340444;
		uranus.Omega0=73.96250215;
		uranus.ad=-0.00020455;
		uranus.ed=-0.0000155;
		uranus.Id=-0.00180155;
		uranus.Ld=428.495126;
		uranus.wbard=0.09266985;
		uranus.Omegad=0.05749699;
		uranus.b=0.00058331;
		uranus.c=-0.97731848;
		uranus.s=0.17689245;
		uranus.f=7.67025;

		uranus.initialize();
		container.loadObject("system", uranus);
		container.addRenderCache(uranus, new PlanetRenderCache());

		//Neptune
		StellarSky.INSTANCE.getLogger().info("Initializing Neptune...");
		Planet neptune = new Planet("neptune", sun, yearUnit);
		neptune.albedo=0.290;
		neptune.radius=1.641209893e-4;
		neptune.mass=5.14956513e-5;
		neptune.a0=30.06952752;
		neptune.e0=0.00895439;
		neptune.I0=1.7700552;
		neptune.L0=304.2228929;
		neptune.wbar0=46.68158724;
		neptune.Omega0=131.7863585;
		neptune.ad=0.00006447;
		neptune.ed=0.00000818;
		neptune.Id=0.000224;
		neptune.Ld=218.4651531;
		neptune.wbard=0.01009938;
		neptune.Omegad=-0.00606302;
		neptune.b=-0.00041348;
		neptune.c=0.68346318;
		neptune.s=-0.10162547;
		neptune.f=7.67025;

		neptune.initialize();
		container.loadObject("system", neptune);
		container.addRenderCache(neptune, new PlanetRenderCache());

		StellarSky.INSTANCE.getLogger().info("Solar System Initialized!");
	}

	@Override
	public void initialUpdate(StellarCollection<SolarObject> container) {
		for(SolarObject object : container.getLoadedObjects("system"))
			object.initialUpdate();
	}

	@Override
	public void updateLayer(StellarCollection<SolarObject> container, double year) {
		for(SolarObject object : container.getLoadedObjects("system"))
			object.updatePre(year);
		for(SolarObject object : container.getLoadedObjects("system"))
			object.updateModulate();
		for(SolarObject object : container.getLoadedObjects("system"))
			object.updatePos(container.getLoadedSingleton("sun"), container.getLoadedSingleton("earth"));
		for(SolarObject object : container.getLoadedObjects("system"))
			object.updatePost(container.getLoadedSingleton("earth"));
	}

	@Override
	public Collection<SolarObject> getSuns(StellarCollection<SolarObject> container) {
		return container.getLoadedObjects("sun");
	}

	@Override
	public Collection<SolarObject> getMoons(StellarCollection<SolarObject> container) {
		return container.getLoadedObjects("moon");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ICelestialLayerRenderer getLayerRenderer() {
		return LayerSolarSystemRenderer.INSTANCE;
	}
}
