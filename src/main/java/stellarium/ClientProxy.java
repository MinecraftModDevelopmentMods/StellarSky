package stellarium;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import stellarium.catalog.StellarCatalogRegistry;
import stellarium.stellars.StellarManager;


public class ClientProxy extends BaseProxy{

	@Override
	public void InitSided(StellarManager m) {
		m.side = Side.CLIENT;
		
		FMLCommonHandler.instance().bus().register(new StellarTickHandler(m.side));

		
	}
	    
	@Override
	public void endRegistry()
	{
		StellarCatalogRegistry.instance(Side.CLIENT).endRegistry();
	}
}
