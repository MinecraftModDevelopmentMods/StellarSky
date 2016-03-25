package stellarium;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import stellarium.api.StellarSkyAPI;
import stellarium.client.ClientSettings;
import stellarium.client.DefaultHourProvider;
import stellarium.client.StellarKeyHook;
import stellarium.config.EnumViewMode;
import stellarium.config.IConfigHandler;
import stellarium.render.StellarSkyClientRender;
import stellarium.stellars.Optics;
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
        
		MinecraftForge.EVENT_BUS.register(new StellarSkyClientRender());
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
	
	@Override
    public World getDefWorld(boolean isRemote) {
    	return isRemote? this.getDefWorld() : super.getDefWorld();
    }
	
	@Override
	public void addScheduledTask(Runnable runnable) {
		Minecraft.getMinecraft().addScheduledTask(runnable);
	}
}
