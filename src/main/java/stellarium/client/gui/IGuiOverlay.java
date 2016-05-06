package stellarium.client.gui;

import net.minecraft.client.Minecraft;
import stellarium.client.PressedKey;

public interface IGuiOverlay<Settings extends PerOverlaySettings> {
	
	public void initialize(Minecraft mc, Settings settings);
	
	public int getWidth();
	public int getHeight();
	
	public float animationOffsetX(float partialTicks);
	public float animationOffsetY(float partialTicks);
	
	public void switchMode(EnumOverlayMode mode);
	
	public void updateOverlay();
	
	/**Return true to update settings*/
	public boolean mouseClicked(int mouseX, int mouseY, int eventButton);
	
	/**Return true to update settings*/
	public boolean mouseMovedOrUp(int mouseX, int mouseY, int eventButton);
	
	/**Return true to update settings*/
	public boolean keyTyped(PressedKey key);
	
	public void render(int mouseX, int mouseY, float partialTicks);
	
}
