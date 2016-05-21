package stellarium.client.lib.gui;

import java.util.List;

import stellarapi.lib.gui.GuiElement;
import stellarapi.lib.gui.IRenderer;

public interface IHierarchyElement {

	public List<IHierarchyElement> generateChildElements();

	public GuiElement generateGui(boolean isHorizontal, IRollHelper helper);

	public boolean hasRoll();

	/**
	 * Size of <b>this</b> element, specifically the main region where generated gui will be on.
	 * Should be constant.
	 * */
	public float getSize();
	
	/**
	 * Size of spacing on roll, not the element itself.
	 * */
	public float rollSpacingSize();
	
	/** Duration of rolling */
	public int rollDuration();
	public boolean checkSettingsChanged();
	public boolean needUpdate();

	/**
	 * Setup background of the container of sub-elements.
	 * This is only for elements with sub-elements.
	 * */
	public String setupBackground(boolean isHorizontal, IRenderer renderer);

	/**
	 * Whether spacing should handle click in the element or not.
	 * */
	@Deprecated
	public boolean handleSpacingInElement();

	public boolean updateRollOnSpacing(boolean isRolled, boolean clicked, boolean hovering);

}
