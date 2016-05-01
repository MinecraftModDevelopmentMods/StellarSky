package stellarium.client.gui.content;

import java.util.List;

import com.google.common.collect.Lists;

import stellarium.client.EnumKey;

public class GuiContent {
	
	/**
	 * Basic GUI elements collected by depth-first traversal.
	 * */
	private List<IGuiBasicElement> elements = Lists.newArrayList();
	private IRenderer renderer;
	
	public GuiContent(IRenderer renderer) {
		this.renderer = renderer;
	}
	
	public void updateTick() {
		for(IGuiBasicElement element : this.elements)
			element.updateElement();
	}
	
	public void mouseClicked(float mouseX, float mouseY, int eventButton) {
		for(IGuiBasicElement element : this.elements)
			element.mouseClicked(mouseX, mouseY, eventButton);
	}

	public void mouseMovedOrUp(float mouseX, float mouseY, int eventButton) {
		for(IGuiBasicElement element : this.elements)
			element.mouseMovedOrUp(mouseX, mouseY, eventButton);
	}

	public void keyTyped(EnumKey key, char eventChar) {
		for(IGuiBasicElement element : this.elements)
			element.keyTyped(key, eventChar);
	}

	public void render(float mouseX, float mouseY, float partialTicks) {
		for(IGuiBasicElement element : this.elements)
			element.checkMousePosition(mouseX, mouseY);
		
		for(IGuiBasicElement element : this.elements)
			element.render(this.renderer);
	}

}
