package stellarium;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import stellarapi.api.ICelestialCoordinates;
import stellarapi.api.ICelestialWorld;
import stellarapi.api.ISkyEffect;
import stellarapi.api.SAPICapabilities;
import stellarapi.api.SAPIReferences;
import stellarapi.api.gui.overlay.OverlayRegistry;
import stellarapi.api.lib.config.ConfigManager;
import stellarapi.api.optics.IOpticalFilter;
import stellarapi.api.optics.IViewScope;
import stellarium.api.IAdaptiveRenderer;
import stellarium.api.StellarSkyAPI;
import stellarium.client.ClientSettings;
import stellarium.client.StellarClientFMLHook;
import stellarium.client.overlay.StellarSkyOverlays;
import stellarium.client.overlay.clientcfg.OverlayClientSettingsType;
import stellarium.client.overlay.clock.OverlayClockType;
import stellarium.lib.render.RendererRegistry;
import stellarium.render.NewSkyRenderer;
import stellarium.render.sky.EnumSkyRenderState;
import stellarium.render.sky.SkyModel;
import stellarium.stellars.OpticsHelper;
import stellarium.stellars.StellarManager;
import stellarium.stellars.layer.CelestialManager;
import stellarium.view.ViewerInfo;
import stellarium.world.StellarScene;

public class ClientProxy extends CommonProxy implements IProxy {
	
	public static final String clientConfigCategory = "clientconfig";
	private static final String clientConfigOpticsCategory = "clientconfig.optics";
	
	private Field fieldLightUpdateSet = ReflectionHelper.findField(RenderGlobal.class, "setLightUpdates", "field_184387_ae");
	
	private ClientSettings clientSettings = new ClientSettings();
	
	private ConfigManager guiConfig;
	private CelestialManager celestialManager = new CelestialManager(true);
	
	private SkyModel skyModel;
	
	
	
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
		
		this.skyModel = new SkyModel(this.celestialManager);
		skyModel.initializeSettings(this.clientSettings);
		
		EnumSkyRenderState.constructRender();
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
		skyModel.stellarLoad(manager);
	}

	@Override
	public void setupDimensionLoad(StellarScene dimManager) {
		skyModel.dimensionLoad(dimManager);
	}
	
	public void onSettingsChanged(ClientSettings settings) {
		skyModel.updateSettings(this.clientSettings);
		RendererRegistry.INSTANCE.evaluateRenderer(SkyModel.class).initialize(settings);
	}
	
	@Override
	public void setupSkyRenderer(World world, String skyRenderType) {
		skyModel.updateSettings(this.clientSettings);
		RendererRegistry.INSTANCE.evaluateRenderer(SkyModel.class).initialize(this.clientSettings);

		IAdaptiveRenderer renderer = StellarSkyAPI.getRendererFor(skyRenderType, new NewSkyRenderer(this.skyModel));
		world.getCapability(StellarSkyAPI.SKY_RENDER_HOLDER, null).setRenderer(renderer);
	}

	@Override
	public float getScreenWidth() {
		return Minecraft.getMinecraft().displayWidth;
	}
	
	private int counter = 0;
	
	@Override
	public void updateTick() {
		if(clientSettings.checkDirty())
			this.onSettingsChanged(this.clientSettings);

		Minecraft mc = Minecraft.getMinecraft();
		World world = mc.world;
		Entity viewer = mc.getRenderViewEntity();
		
		try {
			if(this.counter > 5) {
				this.counter = 0;
				@SuppressWarnings("unchecked")
				Set<BlockPos> lightUpdates = (Set<BlockPos>) fieldLightUpdateSet.get(mc.renderGlobal);
				for(BlockPos pos : lightUpdates) {
					if(pos.distanceSq(viewer.posX, viewer.posY, viewer.posZ) < 24.0) {
						int x = pos.getX();
						int y = pos.getY();
						int z = pos.getZ();
						mc.renderGlobal.markBlockRangeForRenderUpdate(x-1, y-1, z-1, x+1, y+1, z+1);
					}
				}
			} else this.counter++;
		} catch(IllegalAccessException exception) {
			throw new IllegalStateException("Illegal access to field " + fieldLightUpdateSet.getName() + ", Unexpected.");
		}

		ICelestialWorld cWorld = world.getCapability(SAPICapabilities.CELESTIAL_CAPABILITY, null);
		ICelestialCoordinates coordinate = cWorld.getCoordinate();
		ISkyEffect sky = cWorld.getSkyEffect();
		IViewScope scope = SAPIReferences.getScope(viewer);
		IOpticalFilter filter = SAPIReferences.getFilter(viewer);

		skyModel.onTick(this.getDefWorld(), new ViewerInfo(coordinate, sky, scope, filter, viewer));
	}
	
	@Override
	public void addScheduledTask(Runnable runnable) {
		Minecraft.getMinecraft().addScheduledTask(runnable);
	}
}
