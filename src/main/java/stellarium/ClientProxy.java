package stellarium;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import stellarium.api.StellarSkyAPI;
import stellarium.client.ClientSettings;
import stellarium.client.DefaultHourProvider;
import stellarium.client.StellarKeyHook;
import stellarium.client.StellarSkyClientHandler;
import stellarium.config.EnumViewMode;
import stellarium.config.IConfigHandler;
import stellarium.stellars.Optics;
import stellarium.stellars.StellarManager;
import stellarium.stellars.background.BrStar;

public class ClientProxy extends CommonProxy implements IProxy {
	
	private static final String clientConfigCategory = "clientconfig";
	private static final String clientConfigOpticsCategory = "clientconfig.optics";
	
	private ClientSettings clientSettings = new ClientSettings();
	
	public ClientSettings getClientSettings() {
		return this.clientSettings;
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {		
        this.setupConfigManager(event.getSuggestedConfigurationFile());
        
		MinecraftForge.EVENT_BUS.register(new StellarSkyClientHandler());
		FMLCommonHandler.instance().bus().register(new StellarKeyHook());
		
		StellarSkyAPI.registerHourProvider(new DefaultHourProvider(this.clientSettings));
	}

	@Override
	public void load(FMLInitializationEvent event) throws IOException {
		super.load(event);
		
		System.out.println("[Stellarium]: "+"Initializing Stars...");
    	BrStar.initializeAll();
    	System.out.println("[Stellarium]: "+"Stars Initialized!");
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}
	
	@Override
	public void setupConfigManager(File file) {
		super.setupConfigManager(file);
		cfgManager.register(clientConfigCategory, this.clientSettings);
		cfgManager.register(clientConfigOpticsCategory, Optics.instance);
	}
	
	@Override
	public World getDefWorld() {
		return Minecraft.getMinecraft().theWorld;
	}
}
