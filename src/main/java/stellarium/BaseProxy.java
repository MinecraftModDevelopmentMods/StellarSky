package stellarium;

import cpw.mods.fml.relauncher.Side;
import stellarium.catalog.StellarCatalogRegistry;
import stellarium.stellars.StellarManager;

public class BaseProxy {
	
	public void InitSided(StellarManager m)
	{

	}
	
	public void endRegistry()
	{
		StellarCatalogRegistry.instance(Side.SERVER).endRegistry();
	}
	
}
