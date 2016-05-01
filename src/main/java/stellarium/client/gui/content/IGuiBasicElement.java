package stellarium.client.gui.content;

import net.minecraft.client.Minecraft;
import stellarium.client.EnumKey;

public interface IGuiBasicElement<C extends IElementController> {

	public void initialize(C controller);

	public void updateElement();

	public void mouseClicked(float mouseX, float mouseY, int eventButton);
	public void mouseMovedOrUp(float mouseX, float mouseY, int eventButton);

	public void keyTyped(EnumKey key, char eventChar);
	
	/**
	 * Called before rendering passes to check mouse position.
	 * Bounds can be differ here.
	 * */
	public void checkMousePosition(float mouseX, float mouseY);
	
	public void render(IRenderer renderer);

}
