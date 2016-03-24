package stellarium.sleepwake;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import stellarium.config.IConfigHandler;
import stellarium.stellars.StellarManager;

public class SleepWakeManager implements IConfigHandler {
	
	private boolean enabled;
	//true for first, flase for last
	private boolean mode;
	private List<WakeHandler> wakeHandlers = Lists.newArrayList();
	
	public void register(String name, IWakeHandler handler, boolean defaultEnabled) {
		wakeHandlers.add(new WakeHandler(name, handler, defaultEnabled));
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}
	
	@Override
	public void setupConfig(Configuration config, String category) {
		Property allEnabled = config.get(category, "Custom_Wake_Enabled", true);
		allEnabled.comment = "Enable/Disable wake system provided by Stellar Sky";
		allEnabled.setRequiresMcRestart(true);
		allEnabled.setLanguageKey("config.property.server.wakeenable");
		
		Property mode = config.get(category, "Wake_Mode", "latest")
				.setValidValues(new String[]{"earliest", "latest"});
		mode.comment = "You can choose earliest or latest available wake time"
				+ "among these wake properties";
		mode.setRequiresMcRestart(true);
		mode.setLanguageKey("config.property.server.wakemode");
		
		for(WakeHandler entry : this.wakeHandlers) {
			String cat2 = category + Configuration.CATEGORY_SPLITTER + entry.name.toLowerCase();
			Property enabled = config.get(cat2, "Enabled", entry.enabled);
			enabled.comment = "Enable this wake property.";
			enabled.setRequiresMcRestart(true);
			enabled.setLanguageKey("config.property.server.enablewake");
			entry.handler.setupConfig(config, cat2);
		}
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		this.enabled = config.getCategory(category).get("Custom_Wake_Enabled").getBoolean();
		this.mode = config.getCategory(category).get("Wake_Mode").getString().equals("first");
		for(WakeHandler entry : this.wakeHandlers) {
			String cat2 = category + Configuration.CATEGORY_SPLITTER + entry.name.toLowerCase();
			entry.enabled = config.getCategory(cat2).get("Enabled").getBoolean();
			entry.handler.loadFromConfig(config, cat2);
		}
	}
	
	public long getWakeTime(World world, StellarManager manager, long sleepTime) {
		long wakeTime;
		if(this.mode)
		{
			wakeTime=Integer.MAX_VALUE;
			for(WakeHandler handler : wakeHandlers) {
				if(handler.enabled)
					wakeTime = Math.min(wakeTime, handler.handler.getWakeTime(world, manager, sleepTime));
			}
		} else {
			wakeTime=Integer.MIN_VALUE;
			for(WakeHandler handler : wakeHandlers) {
				if(handler.enabled)
					wakeTime = Math.max(wakeTime, handler.handler.getWakeTime(world, manager, sleepTime));
			}
		}
		return wakeTime;
	}
	
	public boolean canSkipTime(World world, StellarManager manager, long sleepTime) {
		boolean canSkip = true;
		boolean wakeHandlerExist = false;
		for(WakeHandler handler : wakeHandlers) {
			if(handler.enabled) {
				wakeHandlerExist = true;
				if(!handler.handler.canSleep(world, manager, sleepTime))
					canSkip = false;
			}
		}
		return wakeHandlerExist && canSkip;
	}
	
	private class WakeHandler {
		public WakeHandler(String name2, IWakeHandler handler2, boolean defaultEnabled) {
			this.name = name2;
			this.handler = handler2;
			this.enabled = defaultEnabled;
		}
		protected String name;
		protected IWakeHandler handler;
		protected boolean enabled;
	}
}
