package stellarium.client.gui.pos;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import stellarium.client.PressedKey;
import stellarium.client.gui.EnumOverlayMode;
import stellarium.client.gui.IRawHandler;
import stellarium.client.gui.OverlayContainer;

public class OverlayPosHandler implements IRawHandler<OverlayPosCfg> {
	
	private Minecraft mc;
	private OverlayContainer container;
	private OverlayPosCfg element;
	private OverlayContainer.Delegate currentSelected = null;
	private EnumHorizontalPos horizontal;
	private EnumVerticalPos vertical;

	@Override
	public void initialize(Minecraft mc, OverlayContainer container, OverlayPosCfg element) {
		this.mc = mc;
		this.container = container;
		this.element = element;
	}

	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int eventButton) {
		if(element.markForUpdate)
		{
			container.switchMode(element.currentMode.focused()?
					EnumOverlayMode.CUSTOMIZE : EnumOverlayMode.FOCUS);
			element.markForUpdate = false;
		}
		
		if(element.currentMode != EnumOverlayMode.CUSTOMIZE)
			return false;
		
		this.currentSelected = container.getElement(mouseX, mouseY);
		if(this.currentSelected != null)
		{
			this.horizontal = currentSelected.getCurrentPos().getHorizontalPos();
			this.vertical = currentSelected.getCurrentPos().getVerticalPos();
		}
		
		return false;
	}

	@Override
	public boolean mouseMovedOrUp(int mouseX, int mouseY, int eventButton) {
		if(element.currentMode != EnumOverlayMode.CUSTOMIZE)
			return false;

		if(eventButton != -1 && this.currentSelected != null) {
			boolean possible = currentSelected.trySetPos(this.horizontal, this.vertical);
			this.currentSelected = null;
			return possible;
		}
		
		return false;
	}

	@Override
	public boolean keyTyped(PressedKey key) {
		return false;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		if(element.currentMode != EnumOverlayMode.CUSTOMIZE)
			return;
		
		if(this.currentSelected != null) {
			if(!horizontal.inRange(mouseX, container.getWidth(), currentSelected.getWidth())
					|| !vertical.inRange(mouseY, container.getHeight(), currentSelected.getHeight())) {
				this.horizontal = EnumHorizontalPos.getNearest(mouseX, container.getWidth(), currentSelected.getWidth());
				this.vertical = EnumVerticalPos.getNearest(mouseY, container.getHeight(), currentSelected.getHeight());
			}
			
			boolean possible = currentSelected.canSetPos(this.horizontal, this.vertical);
			
			int offsetX = horizontal.getOffset(container.getWidth(), currentSelected.getWidth());
			int offsetY = vertical.getOffset(container.getHeight(), currentSelected.getHeight());
			
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
			Gui.drawRect(offsetX+1, offsetY+1,
					offsetX + currentSelected.getWidth(), offsetY + currentSelected.getHeight(),
					possible? 0xff00ff00 : 0xffff0000);
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
			
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			Gui.drawRect(offsetX+1, offsetY+1,
					offsetX + currentSelected.getWidth(), offsetY + currentSelected.getHeight(),
					possible? 0x0700ff00 : 0x07ff0000);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
		}
	}

}
