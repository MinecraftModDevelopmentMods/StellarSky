package stellarium.client.gui.pos;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import stellarium.client.EnumKey;
import stellarium.client.gui.EnumOverlayMode;
import stellarium.client.gui.OverlayContainer;
import stellarium.client.gui.PerOverlaySettings;
import stellarium.client.gui.IGuiOverlay;

public class OverlayPosCfg implements IGuiOverlay<PerOverlaySettings> {

	private static final int WIDTH = 60;
	private static final int HEIGHT = 20;
	private static final int ANIMATION_DURATION = 20;
	
	private Minecraft mc;
	private EnumOverlayMode currentMode = EnumOverlayMode.OVERLAY;
	private OverlayContainer container;
	
	private GuiButton button;
	private int animationTick = 0;
	
	public OverlayPosCfg(OverlayContainer container) {
		this.container = container;
	}
	
	@Override
	public void initialize(Minecraft mc, PerOverlaySettings settings) {
		this.mc = mc;
		
		this.button = new GuiButton(0, 0, 0, WIDTH, HEIGHT,
				currentMode == EnumOverlayMode.CUSTOMIZE? "Stop Customizing" : "Customize");
	}

	@Override
	public int getWidth() {
		return WIDTH;
	}

	@Override
	public int getHeight() {
		return HEIGHT;
	}

	@Override
	public float animationOffsetX(float partialTicks) {
		return 0.0f;
	}

	@Override
	public float animationOffsetY(float partialTicks) {
		return 0.0f;
	}

	@Override
	public void switchMode(EnumOverlayMode mode) {
		if(currentMode.displayed() != mode.displayed()) {
			if(mode.displayed())
				animationTick = 0;
			else animationTick = ANIMATION_DURATION;
		}
		this.currentMode = mode;
		
		button.displayString = (currentMode == EnumOverlayMode.CUSTOMIZE)? "Stop Customizing" : "Customize";
	}

	@Override
	public void updateOverlay() {
		if(this.animationTick > 0 && currentMode.displayed())
			this.animationTick--;
		else if(this.animationTick < ANIMATION_DURATION && !currentMode.displayed())
			this.animationTick++;
	}

	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int eventButton) {
		if(currentMode.displayed() && button.mousePressed(this.mc, mouseX, mouseY))
		{
			if(this.currentMode == EnumOverlayMode.FOCUS)
				container.switchMode(EnumOverlayMode.CUSTOMIZE);
			else if(this.currentMode == EnumOverlayMode.CUSTOMIZE)
				container.switchMode(EnumOverlayMode.FOCUS);
		}
		
		return false;
	}

	@Override
	public boolean mouseMovedOrUp(int mouseX, int mouseY, int eventButton) {
		if(currentMode.displayed())
			button.mouseReleased(mouseX, mouseY);
		return false;
	}

	@Override
	public boolean keyTyped(EnumKey key, char eventChar) {
		return false;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		partialTicks = currentMode.displayed()? partialTicks : -partialTicks;
		GL11.glColor4f(1.0f, 1.0f, 1.0f, (this.animationTick + partialTicks) / ANIMATION_DURATION);
		button.drawButton(this.mc, mouseX, mouseY);
	}

}
