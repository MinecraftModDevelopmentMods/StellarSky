package stellarium;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import stellarapi.api.gui.overlay.OverlayRegistry;
import stellarapi.api.lib.config.ConfigManager;
import stellarapi.api.render.IAdaptiveRenderer;
import stellarapi.api.world.worldset.WorldSet;
import stellarium.api.StellarSkyAPI;
import stellarium.client.ClientSettings;
import stellarium.client.StellarClientFMLHook;
import stellarium.client.overlay.StellarSkyOverlays;
import stellarium.client.overlay.clientcfg.OverlayClientSettingsType;
import stellarium.client.overlay.clock.OverlayClockType;
import stellarium.display.DisplayRegistry;
import stellarium.render.GenericSkyRenderer;
import stellarium.render.SkyModel;
import stellarium.render.SkyRenderer;
import stellarium.render.stellars.QualitySettings;
import stellarium.render.stellars.atmosphere.AtmosphereSettings;
import stellarium.stellars.OpticsHelper;
import stellarium.stellars.StellarManager;
import stellarium.stellars.layer.CelestialManager;
import stellarium.stellars.layer.StellarLayerRegistry;
import stellarium.view.ViewerInfo;
import stellarium.world.StellarScene;
import stellarium.world.landscape.LandscapeClientSettings;

public class ClientProxy extends CommonProxy implements IProxy {
	
	public static final String clientConfigCategory = "clientconfig";
	private static final String clientConfigOpticsCategory = "clientconfig.optics";
	
	private Field fieldLightUpdateSet = ReflectionHelper.findField(RenderGlobal.class, "setLightUpdates", "field_184387_ae");
	
	private ClientSettings clientSettings = new ClientSettings();
	
	private ConfigManager guiConfig;
	private CelestialManager celestialManager = new CelestialManager(true);
	
	private Map<World, SkyModel> skyModels = new HashMap<>();
	
	public ClientSettings getClientSettings() {
		return this.clientSettings;
	}
	
	@Override
	public CelestialManager getClientCelestialManager() {
		return this.celestialManager;
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		this.guiConfig = new ConfigManager(
				StellarSkyReferences.getConfiguration(event.getModConfigurationDirectory(),
						StellarSkyReferences.GUI_SETTINGS));

		MinecraftForge.EVENT_BUS.register(new StellarClientFMLHook());

		OverlayRegistry.registerOverlaySet("stellarsky", new StellarSkyOverlays());
		OverlayRegistry.registerOverlay("clock", new OverlayClockType(), this.guiConfig);
		OverlayRegistry.registerOverlay("clientconfig", new OverlayClientSettingsType(), this.guiConfig);

		clientSettings.putSubConfig(QualitySettings.KEY, new QualitySettings());
		clientSettings.putSubConfig(AtmosphereSettings.KEY, new AtmosphereSettings());
		clientSettings.putSubConfig(LandscapeClientSettings.KEY, new LandscapeClientSettings());
		StellarLayerRegistry.getInstance().composeSettings(clientSettings);
		DisplayRegistry.getInstance().composeSettings(clientSettings);
	}

	@Override
	public void load(FMLInitializationEvent event) throws IOException {
		super.load(event);

		guiConfig.syncFromFile();
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
				
    	celestialManager.initializeClient(this.clientSettings);
	}

	@Override
	public void setupCelestialConfigManager(ConfigManager manager) {
		super.setupCelestialConfigManager(manager);
		manager.register(clientConfigCategory, this.clientSettings);
		manager.register(clientConfigOpticsCategory, OpticsHelper.instance);
	}
	
	@Override
	public World getDefWorld() {
		return Minecraft.getMinecraft().world;
	}
	
	public Entity getDefViewerEntity() {
		return Minecraft.getMinecraft().getRenderViewEntity();
	}
	
	@Override
	public void setupStellarLoad(StellarManager manager) {
		World world = manager.getWorld();
		SkyModel skyModel = new SkyModel(this.celestialManager, world);
		skyModel.stellarLoad(manager);
		skyModels.put(world, skyModel);
	}

	@Override
	public void setupDimensionLoad(StellarScene dimManager) {
		skyModels.get(dimManager.getWorld()).dimensionLoad(dimManager);
	}

	public void onSettingsChanged(ClientSettings settings) {
		for(SkyModel skyModel : skyModels.values())
			skyModel.updateSettings(settings);
		
		SkyRenderer.INSTANCE.initialize(settings);
	}

	@SuppressWarnings("deprecation")
	@Override
	public IAdaptiveRenderer setupSkyRenderer(World world, WorldSet worldSet, String option) {
		SkyModel skyModel = skyModels.get(world);
		skyModel.updateSettings(this.clientSettings);
		SkyRenderer.INSTANCE.initialize(this.clientSettings);
		IRenderHandler genericRenderer = new GenericSkyRenderer(skyModel);
		return StellarSkyAPI.getRendererFor(option, genericRenderer);
	}

	@Override
	public float getScreenWidth() {
		return Minecraft.getMinecraft().displayWidth;
	}
	
	@Override
	public void updateTick() {
		if(!skyModels.isEmpty()) {
			Minecraft mc = Minecraft.getMinecraft();
			Entity viewer = mc.getRenderViewEntity();
			
			if(clientSettings.checkDirty())
				this.onSettingsChanged(this.clientSettings);
	
			// Placeholder fix for vanilla lighting glitch.
			try {
				@SuppressWarnings("unchecked")
				Set<BlockPos> lightUpdates = (Set<BlockPos>) fieldLightUpdateSet.get(mc.renderGlobal);
				Iterator<BlockPos> ite = lightUpdates.iterator();
				while(ite.hasNext()) {
					BlockPos pos = ite.next();
					ite.remove();
					int x = pos.getX();
					int y = pos.getY();
					int z = pos.getZ();
					mc.renderGlobal.markBlockRangeForRenderUpdate(x-1, y-1, z-1, x+1, y+1, z+1);
				}
			} catch(IllegalAccessException exception) {
				throw new IllegalStateException("Illegal access to field " + fieldLightUpdateSet.getName() + ", Unexpected.");
			}
	
			if(viewer != null) {
				for (SkyModel skyModel : skyModels.values()) {
					skyModel.onTick(viewer);
				}
			}
		}
	}
	
	@Override
	public void addScheduledTask(Runnable runnable) {
		Minecraft.getMinecraft().addScheduledTask(runnable);
	}
	
	@Override
	public void removeSkyModel(World world) {
		if(skyModels.containsKey(world))
			skyModels.remove(world);
	}
}
