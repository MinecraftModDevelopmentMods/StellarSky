package stellarium.client.gui;

import java.util.Map;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import stellarium.client.EnumKey;
import stellarium.client.gui.pos.ElementPos;

public class GuiOverlayScreen extends GuiScreen {
	
	private GuiOverlayContainer container;
	private KeyBinding focusGuiKey;
	
	public GuiOverlayScreen(GuiOverlayContainer container, KeyBinding focusGuiKey) {
		this.container = container;
		this.focusGuiKey = focusGuiKey;
		
		container.switchMode(EnumGuiOverlayMode.FOCUS);
	}
	
	@Override
    public void onGuiClosed() {
    	container.switchMode(EnumGuiOverlayMode.OVERLAY);
    }
	
	@Override
    public void initGui() {
    	container.setSize(this.width, this.height);
    }
	
	
	@Override
	public void updateScreen() {
		container.updateOverlay();
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
        if (eventKey == 1 || eventKey == focusGuiKey.getKeyCode())
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
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    	container.render(mouseX, mouseY, partialTicks);
    }
	
	@Override
    public boolean doesGuiPauseGame() {
        return false;
    }

}
