package stellarium.client.overlay.clientcfg;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import stellarapi.lib.gui.GuiElement;
import stellarapi.lib.gui.IRenderer;
import stellarium.client.lib.gui.IHierarchyElement;
import stellarium.client.lib.gui.IRollHelper;

public class ConfigElementSimple implements IHierarchyElement {
	private Configuration config;
	private ICfgHierarchyHandler handler;
	private ICfgTooltipHandler tooltip;

	public ConfigElementSimple(Configuration config, ICfgHierarchyHandler handler, ICfgTooltipHandler tooltip) {
		this.config = config;
		this.handler = handler;
		this.tooltip = tooltip;
	}

	@Override
	public List<IHierarchyElement> generateChildElements() {
		List<IHierarchyElement> childs = Lists.newArrayList();
				
		for(String name : config.getCategoryNames())
			if(handler.accept(null, config.getCategory(name))) {
				ConfigCategory category = config.getCategory(name);
				childs.add(handler.generate(category, this.tooltip));
			}
		
		return childs;
	}

	@Override
	public GuiElement generateGui(boolean isHorizontal, IRollHelper helper) {
		return null;
	}

	@Override
	public boolean hasRoll() {
		return false;
	}

	@Override
	public float getSize() {
		return CfgConstants.ELEMENT_SIZE;
	}

	@Override
	public int rollDuration() {
		return 0;
	}

	@Override
	public boolean needUpdate() {
		// TODO update handling
		return false;
	}
	
	@Override
	public boolean checkSettingsChanged() {
		return false;
	}

	@Override
	public String setupBackground(boolean isHorizontal, IRenderer renderer) {
		return handler.setupBackground(isHorizontal, renderer);
	}

	@Override
	public float rollSpacingSize() {
		return CfgConstants.SPACING;
	}

	@Override
	public boolean handleSpacingInElement() {
		return true;
	}

	@Override
	public boolean updateRollOnSpacing(boolean isRolled, boolean clicked, boolean hovering) {
		return isRolled;
	}
}
