package stellarium.client.gui;

import net.minecraft.client.Minecraft;
import stellarium.client.EnumKey;
import stellarium.client.gui.pos.EnumHorizontalPos;
import stellarium.client.gui.pos.EnumVerticalPos;

public interface IGuiOverlayElement<Settings extends GuiOverlayElementSettings> {
	
	public void initialize(Minecraft mc, Settings settings);
	
	public int getWidth();
	public int getHeight();
	
	public float animationOffsetX(float partialTicks);
	public float animationOffsetY(float partialTicks);
	
	public void switchMode(EnumGuiOverlayMode mode);
	
	public void updateOverlay();
	
	/**Return true to update settings*/
	public boolean mouseClicked(int mouseX, int mouseY, int eventButton);
	
	/**Return true to update settings*/
	public boolean mouseMovedOrUp(int mouseX, int mouseY, int eventButton);
	
	/**Return true to update settings*/
	public boolean keyTyped(EnumKey key, char eventChar);
	
	public void render(int mouseX, int mouseY, float partialTicks);
	
}
