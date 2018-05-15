package stellarium;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Iterator;
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
import stellarapi.api.ICelestialCoordinates;
import stellarapi.api.ICelestialWorld;
import stellarapi.api.ISkyEffect;
import stellarapi.api.SAPICapabilities;
import stellarapi.api.SAPIReferences;
import stellarapi.api.gui.overlay.OverlayRegistry;
import stellarapi.api.lib.config.ConfigManager;
import stellarapi.api.optics.IOpticalFilter;
import stellarapi.api.optics.IViewScope;
import stellarapi.api.render.IAdaptiveRenderer;
import stellarapi.api.world.worldset.WorldSet;
import stellarium.api.StellarSkyAPI;
import stellarium.client.ClientSettings;
import stellarium.client.StellarClientFMLHook;
import stellarium.client.overlay.StellarSkyOverlays;
import stellarium.client.overlay.clientcfg.OverlayClientSettingsType;
import stellarium.client.overlay.clock.OverlayClockType;
import stellarium.render.GenericSkyRenderer;
import stellarium.render.SkyModel;
import stellarium.render.SkyRenderer;
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
		skyModel.updateSettings(settings);
		SkyRenderer.INSTANCE.initialize(settings);
	}

	@Override
	public IAdaptiveRenderer setupSkyRenderer(World world, WorldSet worldSet, String option) {
		skyModel.updateSettings(this.clientSettings);
		SkyRenderer.INSTANCE.initialize(this.clientSettings);
		IRenderHandler genericRenderer = new GenericSkyRenderer(this.skyModel);
		return StellarSkyAPI.getRendererFor(option, genericRenderer);
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
