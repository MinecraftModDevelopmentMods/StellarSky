package stellarium.client.overlay.clientcfg.category;

import net.minecraftforge.common.config.Property;
import stellarapi.lib.gui.GuiElement;
import stellarium.client.overlay.clientcfg.DefCfgHierarchyHandler;

public interface ICategoryController {

	public String mainPropertyName();

	public GuiElement generateSpecificElement(Property property, DefCfgHierarchyHandler helper);
	
	public boolean hasRoll();

}
