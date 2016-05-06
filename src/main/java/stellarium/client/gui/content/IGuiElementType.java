package stellarium.client.gui.content;

import stellarium.client.PressedKey;

public interface IGuiElementType<C extends IElementController> {

	/**
	 * Initialize with certain position and the controller for the element.
	 * */
	public void initialize(GuiPositionHierarchy positions, C controller);

	public void updateElement();

	public void mouseClicked(float mouseX, float mouseY, int eventButton);
	public void mouseMovedOrUp(float mouseX, float mouseY, int eventButton);

	public void keyTyped(PressedKey key);
	
	/**
	 * Called before rendering passes to check mouse position.
	 * Bounds can be differ here.
	 * */
	public void checkMousePosition(float mouseX, float mouseY);
	
	public void render(IRenderer renderer);

}
