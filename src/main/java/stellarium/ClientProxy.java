package stellarium;

import java.io.IOException;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import stellarapi.api.ICelestialCoordinate;
import stellarapi.api.ISkyEffect;
import stellarapi.api.StellarAPIReference;
import stellarapi.api.gui.overlay.OverlayRegistry;
import stellarapi.api.lib.config.ConfigManager;
import stellarapi.api.optics.IOpticalFilter;
import stellarapi.api.optics.IViewScope;
import stellarium.client.ClientSettings;
import stellarium.client.StellarClientFMLHook;
import stellarium.client.overlay.StellarSkyOverlays;
import stellarium.client.overlay.clientcfg.OverlayClientSettingsType;
import stellarium.client.overlay.clock.OverlayClockType;
import stellarium.lib.hierarchy.HierarchyDistributor;
import stellarium.lib.render.RendererRegistry;
import stellarium.render.sky.EnumSkyRenderState;
import stellarium.render.sky.NewSkyRenderer;
import stellarium.render.sky.SkyModel;
import stellarium.stellars.OpticsHelper;
import stellarium.stellars.StellarManager;
import stellarium.stellars.layer.CelestialManager;
import stellarium.view.ViewerInfo;
import stellarium.world.StellarDimensionManager;

public class ClientProxy extends CommonProxy implements IProxy {
	
	public static final String clientConfigCategory = "clientconfig";
	private static final String clientConfigOpticsCategory = "clientconfig.optics";
	
	private ClientSettings clientSettings = new ClientSettings();
	
	private ConfigManager guiConfig;
	private CelestialManager celestialManager = new CelestialManager(true);
	
	private SkyModel skyModel = new SkyModel(this.celestialManager);
	
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
						StellarSkyReferences.guiSettings));
		
		FMLCommonHandler.instance().bus().register(new StellarClientFMLHook());
		
		OverlayRegistry.registerOverlaySet("stellarsky", new StellarSkyOverlays());
		OverlayRegistry.registerOverlay("clock", new OverlayClockType(), this.guiConfig);
		OverlayRegistry.registerOverlay("clientconfig", new OverlayClientSettingsType(), this.guiConfig);
		
		skyModel.initializeSettings(this.clientSettings);
		
		EnumSkyRenderState.constructRender();
	}

	@Override
	public void load(FMLInitializationEvent event) throws IOException {
		super.load(event);

	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
		
		guiConfig.syncFromFile();		
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
		return Minecraft.getMinecraft().theWorld;
	}
	
	public Entity getDefViewerEntity() {
		return Minecraft.getMinecraft().renderViewEntity;
	}
	
	@Override
	public int getRenderDistanceSettings() {
		return Minecraft.getMinecraft().gameSettings.renderDistanceChunks;
	}
	
	public void setupStellarLoad(StellarManager manager) {
		skyModel.stellarLoad(manager);
	}

	public void setupDimensionLoad(StellarDimensionManager dimManager) {
		skyModel.dimensionLoad(dimManager);
	}
	
	public void onSettingsChanged(ClientSettings settings) {
		skyModel.updateSettings(this.clientSettings);
		RendererRegistry.INSTANCE.evaluateRenderer(SkyModel.class).initialize(settings);
	}
	
	@Override
	public void setupSkyRenderer(WorldProvider provider, String skyType) {
		RendererRegistry.INSTANCE.evaluateRenderer(SkyModel.class).initialize(this.clientSettings);

		//IRenderHandler renderer = StellarSkyAPI.getRendererFor(skyType,
		//		new SkyCelestialRenderer(this.clientSettings, celManager, this.displayManager, this.landscapeSettings, cache));
		provider.setSkyRenderer(new NewSkyRenderer(this.skyModel));
	}
	
	@Override
	public float getScreenWidth() {
		return Minecraft.getMinecraft().displayWidth;
	}
	
	@Override
	public void updateTick() {
		if(clientSettings.checkDirty())
			this.onSettingsChanged(this.clientSettings);

		World world = Minecraft.getMinecraft().theWorld;
		Entity viewer = Minecraft.getMinecraft().renderViewEntity;
		
		ICelestialCoordinate coordinate = StellarAPIReference.getCoordinate(world);
		ISkyEffect sky = StellarAPIReference.getSkyEffect(world);
		IViewScope scope = StellarAPIReference.getScope(viewer);
		IOpticalFilter filter = StellarAPIReference.getFilter(viewer);

		skyModel.onTick(this.getDefWorld(), new ViewerInfo(coordinate, sky, scope, filter, viewer));
	}
}
