package stellarium.stellars;

import java.io.IOException;
import java.security.Timestamp;
import java.sql.Time;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarium.initials.*;
import stellarium.stellars.background.BrStar;
import stellarium.stellars.background.StellarBgManager;
import stellarium.stellars.cbody.StarBody;
import stellarium.stellars.moving.StellarMvManager;
import stellarium.stellars.orbit.*;
import stellarium.util.math.Spmath;
import stellarium.util.math.Transforms;
import stellarium.viewrender.render.CRenderEngine;
import stellarium.viewrender.render.StellarRenders;
import stellarium.viewrender.viewer.*;
import stellarium.world.StellarWorldManager;

public class StellarManager {
	
	
	public static Sun Sun=new Sun();
	public static Earth Earth=new Earth();
	public static Moon Moon=new Moon();
	public static Planet Mercury=new Planet();
	public static Planet Venus=new Planet();
	public static Planet Mars=new Planet();
	public static Planet Jupiter=new Planet();
	public static Planet Saturn=new Planet();
	public static Planet Uranus=new Planet();
	public static Planet Neptune=new Planet();
	
	
	//Managers
	public StellarInitManager initmanager;
	public StellarMvManager mvmanager;
	public StellarBgManager bgmanager;
	//public StellarDataManager datamanager;
	public StellarWorldManager worldmanager;

	
	public StellarRenders render;
	public Viewer viewer;
	
	
	public StarBody Star;
	
	
	
	
	public static final double AU=1.496e+8;
	
	
	public static final int frac=4;
	
	
	public Side side;
	
	
	public static float Mag_Limit;
	
	public static int ImgFrac;
	
	public static float Turb;

	

	
	public StellarManager(){
		CRenderEngine.instance=new CRenderEngine();
		
		initmanager = new StellarInitManager(this);
		mvmanager = new StellarMvManager(this);
		bgmanager = new StellarBgManager(this);
		worldmanager = new StellarWorldManager(this);
	}
	
	
	public void OpenWorld() throws IOException{
		OrbitSt CSystem;
		
		if(!side.isClient()){
			CSystem = initmanager.ConstructSystem();
		}
		else{
			render = new StellarRenders();
			viewer = new Viewer();
			viewer.SetViewer(this);
			CSystem = initmanager.ConstructSystem();
		}
		
		mvmanager.OpenWorld(CSystem);
	}
	
	public void OpenWorld(String[] CWorld){
		OrbitSt CSystem;
		
		if(side.isClient()){
			CSystem = initmanager.ConstructSystem(CWorld);
			
			mvmanager.OpenWorld(CSystem);
		}
	}
	
	public void CloseWold(){
		mvmanager.CloseWorld();
	}
	

	
	//Initialization Fuction
	public static final void Initialize(){
		
		System.out.println("[Stellarium]: "+"Initialization Starting...");
		System.out.println("[Stellarium]: "+"Initializing Math class...");
		//Initializing Spmath
		Spmath.Initialize();
		System.out.println("[Stellarium]: "+"Math Class Initialized!");
		
		////Solar System
		System.out.println("[Stellarium]: "+"Initializing Solar System...");
		///Sun
		System.out.println("[Stellarium]: "+"Initializing Sun...");
		Sun.Radius=0.00465469;
		Sun.Mass=1.0;
		Sun.Initialize();
		
		///Earth System
		//Declaration
		System.out.println("[Stellarium]: "+"Initializing Earth...");
		Earth.AddSatellite(Moon);
		Earth.Radius.set(4.2634e-5);
		Earth.Mass=3.002458398e-6;
		Moon.Radius.set(4e-5);
		
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
		Moon.Albedo=0.12;
		Moon.a0=0.00257184;
		Moon.e0=0.0549006;
		Moon.I0=5.14;
		Moon.w0=318.15;
		Moon.Omega0=125.08;
		Moon.M0_0=135.27;
		Moon.wd=40.678;
		Moon.Omegad=-19.355;
		
		//Earth Initialize
		Earth.Initialize();
		
		///Planets
		//Mercury
		System.out.println("[Stellarium]: "+"Initializing Mercury...");
		Mercury.Albedo=0.119;
		Mercury.Radius.set(1.630815508e-5);
		Mercury.Mass=1.660147806e-7;
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
		
		Mercury.Initialize();
		
		//Venus
		System.out.println("[Stellarium]: "+"Initizlizing Venus...");
		Venus.Albedo=0.90;
		Venus.Radius.set(4.0453208556e-5);
		Venus.Mass=2.447589362e-6;
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
		
		Venus.Initialize();
		
		//Mars
		System.out.println("[Stellarium]: "+"Initializing Mars...");
		Mars.Albedo=0.25;
		Mars.Radius.set(2.26604278e-5);
		Mars.Mass=3.22683626e-7;
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
		
		Mars.Initialize();
		
		//Jupiter
		System.out.println("[Stellarium]: "+"Initializing Jupiter...");
		Jupiter.Albedo=0.343;
		Jupiter.Radius.set(4.673195187e-4);
		Jupiter.Mass=9.54502036e-4;
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
		
		Jupiter.Initialize();
		
		//Saturn
		System.out.println("[Stellarium]: "+"Initializing Saturn...");
		Saturn.Albedo=0.342;
		Saturn.Radius.set(3.83128342e-4);
		Saturn.Mass=2.8578754e-4;
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
		
		Saturn.Initialize();
		
		//Uranus
		System.out.println("[Stellarium]: "+"Initializing Uranus...");
		Uranus.Albedo=0.300;
		Uranus.Radius.set(1.68890374e-4);
		Uranus.Mass=4.3642853557e-5;
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
		
		Uranus.Initialize();
		
		//Neptune
		System.out.println("[Stellarium]: "+"Initializing Neptune...");
		Neptune.Albedo=0.290;
		Neptune.Radius.set(1.641209893e-4);
		Neptune.Mass=5.14956513e-5;
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
		
		Neptune.Initialize();
		
		System.out.println("[Stellarium]: "+"Solar System Initialized!");

	}
	
	public static final void InitializeStars() throws IOException{
		///Stars
		System.out.println("[Stellarium]: "+"Initializing Stars...");
    	BrStar.InitializeAll();
    	System.out.println("[Stellarium]: "+"Stars Initialized!");
	}
	
	//Update Objects
	public static final void Update(double time, boolean IsOverWorld){
		time=time+5000.0;
		
        long cur = System.currentTimeMillis();
		
		//Must be first
		Transforms.Update(time, IsOverWorld);
		
		//Must be second
		Earth.Update();
		
		Mercury.Update();
		Venus.Update();
		Mars.Update();
		Jupiter.Update();
		Saturn.Update();
		Uranus.Update();
		Neptune.Update();
		
		if(BrStar.IsInitialized) BrStar.UpdateAll();
		
        //System.out.println(System.currentTimeMillis() - cur);

	}
}
