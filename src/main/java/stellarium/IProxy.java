package stellarium;

import java.io.IOException;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public interface IProxy {
	
	public void preInit(FMLPreInitializationEvent event) throws IOException;
	
    public void load(FMLInitializationEvent event);

    public void postInit(FMLPostInitializationEvent event);
    
    //World getter class for client.
    public World getDefWorld();

}
