package stellarium.construct;

public class CPropLangStrsCBody {

	//--------------------------------- Orbit Types --------------------------------//
	//Stationary Orbit (Root-Default)
	public static String storb = "St";
	
	public static void onRegister()
	{
		CPropLangRegistry.instance().register(storb, "cmv.orbtype.stationary");

	}
	
}
