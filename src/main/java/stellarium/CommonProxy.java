package stellarium;

import java.io.File;
import java.io.IOException;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import stellarapi.api.lib.config.ConfigManager;
import stellarium.client.ClientSettings;
import stellarium.common.CommonSettings;
import stellarium.common.DimensionSettings;
import stellarium.stellars.layer.CelestialManager;

public class CommonProxy implements IProxy {

	protected Configuration config;
	protected ConfigManager cfgManager;
	public CommonSettings commonSettings = new CommonSettings();
	public DimensionSettings dimensionSettings = new DimensionSettings();
	
	private static final String serverConfigCategory = "serverconfig";
	private static final String serverConfigDimensionCategory = "serverconfig.dimension";
	private static final String serverConfigWakeCategory = "serverconfig.wake";
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {		
		this.setupConfigManager(event.getSuggestedConfigurationFile());
	}

	@Override
	public void load(FMLInitializationEvent event) throws IOException {
        cfgManager.syncFromFile();
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		
	}
	
	public void setupConfigManager(File file) {
		config = new Configuration(file);
        cfgManager = new ConfigManager(config);
        
        cfgManager.register(serverConfigCategory, this.commonSettings);
        cfgManager.register(serverConfigDimensionCategory, this.dimensionSettings);
	}
	
	public ConfigManager getCfgManager() {
		return this.cfgManager;
	}
	
	@Override
	public World getDefWorld() {
		return MinecraftServer.getServer().getEntityWorld();
	}
	
	@Override
	public World getDefWorld(boolean isRemote) {
		return MinecraftServer.getServer().getEntityWorld();
	}
	
	@Override
	public ClientSettings getClientSettings() {
		return null;
	}

	public Configuration getConfig() {
		return this.config;
	}

	@Override
	public CelestialManager getClientCelestialManager() {
		return null;
	}

	public Entity getDefViewerEntity() {
		return null;
	}
}
