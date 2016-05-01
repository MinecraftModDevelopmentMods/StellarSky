package stellarium.client.gui.content.simple;

import stellarium.client.gui.content.IElementController;
import stellarium.client.gui.content.IRenderer;

public interface ISimpleController extends IElementController {

	/**
	 * Sets up and give model name.
	 * */
	public String setupRenderer(IRenderer renderer);

}
