package stellarium.client.gui.content.button;

import stellarium.client.gui.content.IElementController;
import stellarium.client.gui.content.IRenderer;

public interface IButtonController extends IElementController {

	public boolean canClick(int eventButton);
	
	/** Return true to change settings */
	public boolean onClicked(int eventButton);
	
	/** Return true to change settings */
	public boolean onClickEnded(int eventButton);
	
	public void setupRenderer(boolean mouseOver, IRenderer renderer);
	public String setupOverlay(boolean mouseOver, IRenderer renderer);
	public String setupMain(boolean mouseOver, IRenderer renderer);

}
