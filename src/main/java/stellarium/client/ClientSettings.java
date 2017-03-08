package stellarium.client;

import net.minecraftforge.common.config.Configuration;
import stellarapi.api.lib.config.SimpleHierarchicalConfig;
import stellarapi.api.lib.config.property.ConfigPropertyDouble;

public class ClientSettings extends SimpleHierarchicalConfig {

	public float mag_Limit;

	private ConfigPropertyDouble propMagLimit;

	private boolean isDirty = false;

	public ClientSettings() {
		this.propMagLimit = new ConfigPropertyDouble("Mag_Limit", "", 4.5);

		this.addConfigProperty(this.propMagLimit);
	}

	@Override
	public void setupConfig(Configuration config, String category) {
		config.setCategoryComment(category, "Configurations for client modifications.\n"
				+ "Most of them are for rendering/view.");
		config.setCategoryLanguageKey(category, "config.category.client");
		config.setCategoryRequiresMcRestart(category, false);

		super.setupConfig(config, category);

		propMagLimit.setComment("Limit of magnitude can be seen on naked eye.\n" +
				"If you want to increase FPS, lower the Mag_Limit.\n" +
				"(Realistic = 6.5, Default = 4.5)\n" +
				"The lower you set it, the fewer stars you will see\n" +
				"but the better FPS you will get");
		propMagLimit.setRequiresMcRestart(true);
		propMagLimit.setLanguageKey("config.property.client.maglimit");
		propMagLimit.setMinValue(2.0);
		propMagLimit.setMaxValue(7.0);
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		super.loadFromConfig(config, category);

		this.mag_Limit=(float)propMagLimit.getDouble();
		this.isDirty = true;
	}

	@Override
	public void saveToConfig(Configuration config, String category) {
		super.saveToConfig(config, category);
	}

	public boolean checkDirty() {
		boolean flag = this.isDirty;
		this.isDirty = false;
		return flag;
	}
}
