package stellarium.client.gui;

import net.minecraft.client.Minecraft;
import stellarium.client.PressedKey;

public interface IRawHandler<Element extends IGuiOverlay> {
	
	public void initialize(Minecraft mc, OverlayContainer container, Element element);
	
	/**
	 * Return true to update settings.
	 * Checked after element.
	 * */
	public boolean mouseClicked(int mouseX, int mouseY, int eventButton);
	
	/**
	 * Return true to update settings.
	 * Checked after element.
	 * */
	public boolean mouseMovedOrUp(int mouseX, int mouseY, int eventButton);
	
	/**
	 * Return true to update settings.
	 * Checked after element.
	 * */
	public boolean keyTyped(PressedKey key);

	public void render(int mouseX, int mouseY, float partialTicks);

}
