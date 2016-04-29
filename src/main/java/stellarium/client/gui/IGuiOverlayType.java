package stellarium.client.gui;

import stellarium.client.gui.pos.EnumHorizontalPos;
import stellarium.client.gui.pos.EnumVerticalPos;

public interface IGuiOverlayType<Element extends IGuiOverlayElement<Settings>, Settings extends GuiOverlayElementSettings> {
	
	public Element generateElement();
	public Settings generateSettings();
	public String getName();
	
	public EnumHorizontalPos defaultHorizontalPos();
	public EnumVerticalPos defaultVerticalPos();

	public boolean accepts(EnumHorizontalPos pos);
	public boolean accepts(EnumVerticalPos pos);

}
