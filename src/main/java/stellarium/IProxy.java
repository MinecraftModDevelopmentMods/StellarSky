package stellarium;

import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import stellarapi.api.lib.config.ConfigManager;
import stellarapi.api.lib.config.HierarchicalConfig;
import stellarapi.api.render.IAdaptiveRenderer;
import stellarapi.api.world.worldset.WorldSet;
import stellarium.client.ClientSettings;
import stellarium.common.ServerSettings;
import stellarium.stellars.StellarManager;
import stellarium.stellars.layer.CelestialManager;
import stellarium.world.StellarScene;

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

	public IAdaptiveRenderer setupSkyRenderer(World world, WorldSet worldSet);

	public void updateTick();

	public void addScheduledTask(Runnable runnable);
	public float getScreenWidth();

	public void setupStellarLoad(StellarManager manager);
	public void setupDimensionLoad(StellarScene dimManager);
}
