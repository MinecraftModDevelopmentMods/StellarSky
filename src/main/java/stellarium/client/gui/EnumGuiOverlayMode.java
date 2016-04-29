package stellarium.client.gui;

public enum EnumGuiOverlayMode {
	FOCUS(true),
	//Default
	OVERLAY(false);
	
	private final boolean focused;
	
	EnumGuiOverlayMode(boolean focused) {
		this.focused = focused;
	}
		
	public boolean focused() {
		return this.focused;
	}
}
