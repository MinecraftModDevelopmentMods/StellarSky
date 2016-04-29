package stellarium.client.gui;

import java.util.Map;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;
import stellarium.client.EnumKey;
import stellarium.client.gui.pos.ElementPos;

public class GuiScreenOverlay extends GuiScreen {
	
	private OverlayContainer container;
	private KeyBinding focusGuiKey;
	
	public GuiScreenOverlay(OverlayContainer container, KeyBinding focusGuiKey) {
		this.container = container;
		this.focusGuiKey = focusGuiKey;
		
		container.switchMode(EnumOverlayMode.FOCUS);
	}
	
	@Override
    public void onGuiClosed() {
    	container.switchMode(EnumOverlayMode.OVERLAY);
    }
	
	@Override
    public void initGui() {
		ScaledResolution resolution = new ScaledResolution(
				this.mc, mc.displayWidth, mc.displayHeight);
    	container.setResolution(resolution);
    }
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int eventButton) {
		container.mouseClicked(mouseX, mouseY, eventButton);
	}
	
	@Override
	public void mouseMovedOrUp(int mouseX, int mouseY, int eventButton) {
		container.mouseMovedOrUp(mouseX, mouseY, eventButton);
	}
	
	@Override
	public void keyTyped(char eventChar, int eventKey) {
        if (eventKey == Keyboard.KEY_ESCAPE || eventKey == focusGuiKey.getKeyCode())
        {
            this.mc.displayGuiScreen((GuiScreen)null);
            this.mc.setIngameFocus();
        }
		
		for(EnumKey key : EnumKey.values())
			if(key.isThisKey(eventKey))
			{
				container.keyTyped(key, eventChar);
				break;
			}
	}
	
	@Override
    public boolean doesGuiPauseGame() {
        return false;
    }

}
