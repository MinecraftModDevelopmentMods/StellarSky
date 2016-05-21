package stellarium;

import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import stellarapi.api.lib.config.ConfigManager;
import stellarapi.api.lib.config.HierarchicalConfig;
import stellarium.client.ClientSettings;
import stellarium.common.DimensionSettings;
import stellarium.common.ServerSettings;
import stellarium.stellars.layer.CelestialManager;
import stellarium.world.landscape.LandscapeCache;

public class CommonProxy implements IProxy {

	private ServerSettings serverSettings = new ServerSettings();
	private DimensionSettings dimensionSettings = new DimensionSettings();
	
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
		manager.register(serverConfigCategory, this.serverSettings);
		manager.register(serverConfigDimensionCategory, this.dimensionSettings);
	}
	
	@Override
	public World getDefWorld() {
		return null;
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

	@Override
	public int getRenderDistanceSettings() {
		return 0;
	}

	@Override
	public void setupSkyRenderer(WorldProvider provider, CelestialManager celManager, String skyType, LandscapeCache cache) { }

	@Override
	public HierarchicalConfig getDimensionSettings() {
		return this.dimensionSettings;
	}

	@Override
	public ServerSettings getServerSettings() {
		return this.serverSettings;
	}

	@Override
	public void addScheduledTask(Runnable runnable) {
		FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(runnable);
	}

	@Override
	public void updateTick() { }
}
