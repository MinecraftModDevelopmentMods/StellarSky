package stellarium.client.overlay.clientcfg.property;

import net.minecraftforge.common.config.Property;
import stellarium.client.lib.gui.IHierarchyElement;
import stellarium.client.overlay.clientcfg.ICfgHierarchyHandler;
import stellarium.client.overlay.clientcfg.ICfgTooltipHandler;

public interface IPropertyElementType {

	/**
	 * Whether this type accepts certain property.
	 * Type and list will already be checked, so no check needed for that part.
	 * */
	public boolean accept(Property property);
	
	/**Use rollable element*/
	public boolean useRollable();
	public IHierarchyElement generate(Property property, ICfgHierarchyHandler handler, ICfgTooltipHandler tooltip);

}
