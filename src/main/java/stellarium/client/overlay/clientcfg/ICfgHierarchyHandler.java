package stellarium.client.overlay.clientcfg;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Property;
import stellarapi.lib.gui.IRenderer;
import stellarium.client.lib.gui.IHierarchyElement;

public interface ICfgHierarchyHandler {

	/**
	 * Parent can be null when the 'parent' should be configuration itself.
	 * Though the category can be child of another category on the case, too.
	 * */
	public boolean accept(ConfigCategory parent, ConfigCategory category);
	public IHierarchyElement generate(ConfigCategory category, ICfgTooltipHandler tooltip);
	
	public boolean accept(Property property);
	public IHierarchyElement generate(Property property, ICfgTooltipHandler tooltip);

	public IHierarchyElement spacing(float size);

	
	public String setupBackground(boolean isHorizontal, IRenderer renderer);
	public void setupMainRenderer(boolean isHorizontal, IRenderer renderer);

}
