package stellarium.render.stellars;

import org.lwjgl.opengl.GL11;

import net.minecraftforge.common.config.Configuration;
import stellarapi.api.lib.config.SimpleConfigHandler;
import stellarapi.api.lib.config.property.ConfigPropertyBoolean;

public class QualitySettings extends SimpleConfigHandler {
	public static final String KEY = "quality";

	public QualitySettings() { }

	@Override
	public void setupConfig(Configuration config, String category) {
		config.setCategoryComment(category, "Configurations for quality vs performance.");
		config.setCategoryLanguageKey(category, "config.category.quality");
		config.setCategoryRequiresMcRestart(category, false);

		super.setupConfig(config, category);
	}

	@Override
	public void loadFromConfig(Configuration config, String category) {
		super.loadFromConfig(config, category);
	}

	@Override
	public void saveToConfig(Configuration config, String category) {
		super.saveToConfig(config, category);
	}
}
