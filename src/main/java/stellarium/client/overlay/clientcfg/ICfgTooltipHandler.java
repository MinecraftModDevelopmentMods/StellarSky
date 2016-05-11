package stellarium.client.overlay.clientcfg;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Property;
import stellarapi.lib.gui.GuiElement;
import stellarapi.lib.gui.dynamic.tooltip.ITooltipElementController;

public interface ICfgTooltipHandler {
	
	public ITooltipElementController defaultTooltip(ConfigCategory category);
	public ITooltipElementController defaultTooltip(Property property);
	
	public GuiElement wrapElement(GuiElement element, String tooltip);
	public GuiElement wrapElement(GuiElement element, ITooltipElementController controller);
	
	public void setWrappedGui(GuiElement spaced);
	public GuiElement generateGui();

}
