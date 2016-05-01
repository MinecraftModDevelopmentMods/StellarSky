package stellarium.client.gui.content.compound;

import net.minecraft.client.Minecraft;
import stellarium.client.EnumKey;

//Partial Ticks & Animation & Clipping
public interface IGuiElement {

	public void initElement(Minecraft mc);

	public float getWidth();
	public float getHeight();

	public void setClip(float posX1, float posY1, float posX2, float posY2);

	public void tickElement();

	/**Return true to update settings*/
	public boolean mouseClicked(float mouseX, float mouseY, int eventButton);

	/**Return true to update settings*/
	public boolean mouseMovedOrUp(float mouseX, float mouseY, int eventButton);

	/**Return true to update settings*/
	public boolean keyTyped(EnumKey key, char eventChar);

	public void render(float mouseX, float mouseY, float partialTicks);

}
