package stellarium.client.gui.content.textfield;

import stellarium.client.EnumKey;
import stellarium.client.gui.content.IGuiBasicElement;
import stellarium.client.gui.content.IRenderer;

public class GuiTextFieldElement implements IGuiBasicElement<ITextFieldController> {

	private ITextFieldController controller;
	private String currentString;
	private int cursor, selectedEnd;
	private int cursorCounter;
	private boolean focused;
	
	@Override
	public void initialize(ITextFieldController controller) {
		this.controller = controller;
	}

	@Override
	public void updateElement() {
		this.cursorCounter++;
	}

	@Override
	public void mouseClicked(float mouseX, float mouseY, int eventButton) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMovedOrUp(float mouseX, float mouseY, int eventButton) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(EnumKey key, char eventChar) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void checkMousePosition(float mouseX, float mouseY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(IRenderer renderer) {
		// TODO Auto-generated method stub
		
	}
	
}
