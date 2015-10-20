package stellarium.sleepwake;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import stellarium.config.IConfigHandler;

public class SleepWakeManager implements IConfigHandler {
	
	private List<WakeHandler> wakeHandlers = Lists.newArrayList();
	
	public void register(String name, IWakeHandler handler, boolean defaultEnabled) {
		wakeHandlers.add(new WakeHandler(name, handler, defaultEnabled));
	}
	
	@Override
	public void setupConfig(Configuration config, String category) {
		Property mode = config.get(category, "Wake Mode", "last")
				.setValidValues(new String[]{"first", "last"});
		mode.comment = "You can choose first or last available wake time among wake properties";
		mode.setRequiresMcRestart(true);
		mode.setLanguageKey("config.property.server.wakemode");
		
		for(WakeHandler entry : this.wakeHandlers) {
			String cat2 = category + Configuration.CATEGORY_SPLITTER + entry.name;
			Property enabled = config.get(category, "Enabled", entry.enabled);
			enabled.comment = "Enable this wake property.";
			enabled.setRequiresMcRestart(true);
			enabled.setLanguageKey("config.property.server.enablewake");
			entry.handler.setupConfig(config, cat2);
		}
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		for(WakeHandler entry : this.wakeHandlers) {
			String cat2 = category + Configuration.CATEGORY_SPLITTER + entry.name;
			entry.enabled = config.getCategory(cat2).get("Enabled").getBoolean();
			entry.handler.loadFromConfig(config, cat2);
		}
	}
	
	public int getWakeTime(int sleepTime) {
		return sleepTime;
	}
	
	public boolean canSkipTime(int sleepTime) {
		return false;
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
