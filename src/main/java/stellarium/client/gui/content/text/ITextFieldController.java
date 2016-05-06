package stellarium.client.gui.content.text;

import stellarium.client.gui.content.IElementController;
import stellarium.client.gui.content.simple.ISimpleRenderController;

public interface ITextFieldController extends IElementController {

	public ITextInternalController getTextController();
	public ISimpleRenderController getBackground();

	public float getSpacingX();
	public float getSpacingY();

}
