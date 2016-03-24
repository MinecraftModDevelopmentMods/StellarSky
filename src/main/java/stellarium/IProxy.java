package stellarium;

import java.io.IOException;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import stellarium.client.ClientSettings;

public interface IProxy {
	
	public void preInit(FMLPreInitializationEvent event);
	
    public void load(FMLInitializationEvent event) throws IOException;

    public void postInit(FMLPostInitializationEvent event);
    
    public World getDefWorld();
    public World getDefWorld(boolean isRemote);
    
    public ClientSettings getClientSettings();
    
	public void addScheduledTask(Runnable runnable);

}
