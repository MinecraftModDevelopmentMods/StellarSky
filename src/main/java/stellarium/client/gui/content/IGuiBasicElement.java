package stellarium.client.gui.content;

import net.minecraft.client.Minecraft;
import stellarium.client.EnumKey;

public interface IGuiBasicElement<C extends IElementController> {

	public void initialize(C controller);

	public void updateElement();

	/**Return true to update settings*/
	public boolean mouseClicked(float mouseX, float mouseY, int eventButton);

	/**Return true to update settings*/
	public boolean mouseMovedOrUp(float mouseX, float mouseY, int eventButton);

	/**Return true to update settings*/
	public boolean keyTyped(EnumKey key, char eventChar);

	public void render(IRenderer renderer, float mouseX, float mouseY);

}
