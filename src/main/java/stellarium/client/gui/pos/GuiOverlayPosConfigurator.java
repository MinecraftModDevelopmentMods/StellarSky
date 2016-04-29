package stellarium.client.gui.pos;

import net.minecraft.client.Minecraft;
import stellarium.client.EnumKey;
import stellarium.client.gui.EnumGuiOverlayMode;
import stellarium.client.gui.GuiOverlayContainer;
import stellarium.client.gui.GuiOverlayElementSettings;
import stellarium.client.gui.IGuiOverlayElement;

public class GuiOverlayPosConfigurator implements IGuiOverlayElement<GuiOverlayElementSettings> {

	private GuiOverlayContainer container;
	private Minecraft mc;
	
	public GuiOverlayPosConfigurator(GuiOverlayContainer container) {
		this.container = container;
	}
	
	@Override
	public void initialize(Minecraft mc, GuiOverlayElementSettings settings) {
		this.mc = mc;
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float animationOffsetX(float partialTicks) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float animationOffsetY(float partialTicks) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void switchMode(EnumGuiOverlayMode mode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateOverlay() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int eventButton) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMovedOrUp(int mouseX, int mouseY, int eventButton) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(EnumKey key, char eventChar) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		// TODO Auto-generated method stub
		
	}

}
