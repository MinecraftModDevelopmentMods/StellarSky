package stellarium.client.gui.content.scrollbar;

import stellarium.client.EnumKey;
import stellarium.client.gui.content.IGuiBasicElement;
import stellarium.client.gui.content.IRenderer;

public class GuiScrollBarElement implements IGuiBasicElement<IScrollBarController> {

	private IScrollBarController controller;
	
	@Override
	public void initialize(IScrollBarController controller) {
		this.controller = controller;
	}

	@Override
	public void updateElement() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean mouseClicked(float mouseX, float mouseY, int eventButton) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMovedOrUp(float mouseX, float mouseY, int eventButton) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(EnumKey key, char eventChar) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void render(IRenderer renderer, float mouseX, float mouseY) {
		// TODO Auto-generated method stub
		
	}

}
