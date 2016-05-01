package stellarium.client.gui.content;

import java.util.List;

import com.google.common.collect.Lists;

import stellarium.client.EnumKey;

public class GuiContent {
	
	private List<IGuiBasicElement> elements = Lists.newArrayList();
	private IRenderer renderer;
	
	public GuiContent(IRenderer renderer) {
		this.renderer = renderer;
	}
	
	public void updateTick() {
		for(IGuiBasicElement element : this.elements)
			element.updateElement();
	}
	
	/**Return true to update settings*/
	public boolean mouseClicked(float mouseX, float mouseY, int eventButton) {
		for(IGuiBasicElement element : this.elements)
			if(element.mouseClicked(mouseX, mouseY, eventButton))
				return true;
		
		return false;
	}

	/**Return true to update settings*/
	public boolean mouseMovedOrUp(float mouseX, float mouseY, int eventButton) {
		for(IGuiBasicElement element : this.elements)
			if(element.mouseMovedOrUp(mouseX, mouseY, eventButton))
				return true;
		
		return false;
		
	}

	/**Return true to update settings*/
	public boolean keyTyped(EnumKey key, char eventChar) {
		boolean check = false;
		for(IGuiBasicElement element : this.elements)
			check = element.keyTyped(key, eventChar) || check;
		return check;
	}

	public void render(float mouseX, float mouseY, float partialTicks) {
		for(IGuiBasicElement element : this.elements)
			element.render(this.renderer, mouseX, mouseY);
	}

}
