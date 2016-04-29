package stellarium.client.gui;

import net.minecraftforge.common.config.Configuration;
import stellarapi.api.lib.config.SimpleConfigHandler;
import stellarapi.api.lib.config.property.ConfigPropertyString;
import stellarium.client.gui.clock.EnumViewMode;
import stellarium.client.gui.pos.EnumHorizontalPos;
import stellarium.client.gui.pos.EnumVerticalPos;

public class GuiOverlayElementSettings extends SimpleConfigHandler {
	
	EnumHorizontalPos horizontal;
	EnumVerticalPos vertical;
	
	private ConfigPropertyString propHorizontal;
	private ConfigPropertyString propVertical;
	
	void initializeSetttings(EnumHorizontalPos horizontal, EnumVerticalPos vertical) {
		this.horizontal = horizontal;
		this.vertical = vertical;
		
		this.propHorizontal = new ConfigPropertyString("Horizontal_Position", "", horizontal.name());
		this.propVertical = new ConfigPropertyString("Vertical_Position", "", vertical.name());
	}
	
	@Override
	public void setupConfig(Configuration config, String category) {
		super.setupConfig(config, category);
		
		propHorizontal.setValidValues(EnumHorizontalPos.names);
		propHorizontal.setComment("Horizontal Position on the Overlay.");
		propHorizontal.setRequiresMcRestart(false);
		propHorizontal.setLanguageKey("config.property.gui.pos.horizontal");
		
		propVertical.setValidValues(EnumVerticalPos.names);
		propVertical.setComment("Vertical Position on the Overlay.");
		propVertical.setRequiresMcRestart(false);
		propVertical.setLanguageKey("config.property.gui.pos.vertical");
	}
	
	@Override
	public void loadFromConfig(Configuration config, String category) {
		super.loadFromConfig(config, category);
		this.horizontal = EnumHorizontalPos.valueOf(propHorizontal.getString());
		this.vertical = EnumVerticalPos.valueOf(propVertical.getString());
	}

	@Override
	public void saveToConfig(Configuration config, String category) {
		propHorizontal.setString(horizontal.name());
		propVertical.setString(vertical.name());
		super.saveToConfig(config, category);
	}

}
