package stellarium;

import java.io.IOException;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.world.World;
import stellarium.client.ClientSettings;

public interface IProxy {
	
	public void preInit(FMLPreInitializationEvent event);
	
    public void load(FMLInitializationEvent event) throws IOException;

    public void postInit(FMLPostInitializationEvent event);
    
    //World getter class for client.
    public World getDefWorld();
    
    public ClientSettings getClientSettings();

}
