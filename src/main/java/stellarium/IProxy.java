package stellarium;

import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import stellarapi.api.lib.config.ConfigManager;
import stellarapi.api.lib.config.HierarchicalConfig;
import stellarium.client.ClientSettings;
import stellarium.common.ServerSettings;
import stellarium.stellars.StellarManager;
import stellarium.stellars.layer.CelestialManager;
import stellarium.world.StellarDimensionManager;

public interface IProxy {
	
	public void preInit(FMLPreInitializationEvent event);
	
    public void load(FMLInitializationEvent event) throws IOException;

    public void postInit(FMLPostInitializationEvent event);
    
	public World getDefWorld();
	public Entity getDefViewerEntity();
	
	public void setupCelestialConfigManager(ConfigManager manager);
	
    public ClientSettings getClientSettings();
	public ServerSettings getServerSettings();
	public HierarchicalConfig getDimensionSettings();
    
	public CelestialManager getClientCelestialManager();
	
	public void setupSkyRenderer(World world, WorldProvider provider, String skyRenderType);

	public void updateTick();

	public void addScheduledTask(Runnable runnable);
	public float getScreenWidth();

	public void setupStellarLoad(StellarManager manager);
	public void setupDimensionLoad(StellarDimensionManager dimManager);

}
