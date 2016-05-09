package stellarium;

import java.io.IOException;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import stellarapi.api.lib.config.ConfigManager;
import stellarium.client.ClientSettings;
import stellarium.common.CommonSettings;
import stellarium.common.DimensionSettings;
import stellarium.stellars.layer.CelestialManager;

public class CommonProxy implements IProxy {

	public CommonSettings commonSettings = new CommonSettings();
	public DimensionSettings dimensionSettings = new DimensionSettings();
	
	private static final String serverConfigCategory = "serverconfig";
	private static final String serverConfigDimensionCategory = "serverconfig.dimension";
	
	@Override
	public void preInit(FMLPreInitializationEvent event) { }

	@Override
	public void load(FMLInitializationEvent event) throws IOException { }

	@Override
	public void postInit(FMLPostInitializationEvent event) { }
	
	@Override
	public void setupCelestialConfigManager(ConfigManager manager) {
		manager.register(serverConfigCategory, this.commonSettings);
		manager.register(serverConfigDimensionCategory, this.dimensionSettings);
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

	@Override
	public CelestialManager getClientCelestialManager() {
		return null;
	}

	public Entity getDefViewerEntity() {
		return null;
	}
}
