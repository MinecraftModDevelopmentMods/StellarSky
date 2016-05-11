package stellarium.client.lib.gui;

import stellarapi.lib.gui.IRenderer;

public interface IRollHelper {

	public boolean isDirectionInverted(boolean isHorizontal);

	public boolean rollToRight();
	public boolean rollToDown();

	public boolean hasRollButton();
	public float rollBtnSize();

	public void setupRollBtnRenderer(boolean isHorizontal, boolean mouseOver, IRenderer renderer);
	public String setupRollBtnOverlay(boolean isHorizontal, boolean mouseOver, IRenderer renderer);
	public String setupRollBtnMain(boolean isHorizontal, boolean mouseOver, IRenderer renderer);

	public void setupSpacingBtnRenderer(boolean isHorizontal, boolean mouseOver, IRenderer renderer);
	public String setupSpacingBtnOverlay(boolean isHorizontal, boolean mouseOver, IRenderer renderer);
	public String setupSpacingBtnMain(boolean isHorizontal, boolean mouseOver, IRenderer renderer);

	public long hoverUpdateDelay();
	
}
