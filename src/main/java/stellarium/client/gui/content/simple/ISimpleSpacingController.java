package stellarium.client.gui.content.simple;

import stellarium.client.gui.content.IElementController;
import stellarium.client.gui.content.IRenderer;

public interface ISimpleSpacingController extends ISimpleController {

	/**
	 * Sets up and give model name.
	 * */
	public String setupSpacingRenderer(IRenderer renderer);

	public float getSpacingX();
	public float getSpacingY();

}
