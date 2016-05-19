package stellarium;

import java.io.IOException;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import stellarapi.api.lib.config.ConfigManager;
import stellarapi.api.lib.config.HierarchicalConfig;
import stellarium.client.ClientSettings;
import stellarium.common.ServerSettings;
import stellarium.stellars.layer.CelestialManager;
import stellarium.world.landscape.LandscapeCache;

public interface IProxy {
	
	public void preInit(FMLPreInitializationEvent event);
	
    public void load(FMLInitializationEvent event) throws IOException;

    public void postInit(FMLPostInitializationEvent event);
    
	public World getDefWorld();
	public Entity getDefViewerEntity();
	public int getRenderDistanceSettings();
	
	public void setupCelestialConfigManager(ConfigManager manager);    
    public ClientSettings getClientSettings();
	public ServerSettings getServerSettings();
	public HierarchicalConfig getDimensionSettings();
    
	public CelestialManager getClientCelestialManager();
	
	public void setupSkyRenderer(WorldProvider provider, CelestialManager celestialManager, String skyType, LandscapeCache cache);

	public void updateTick();

}
