package stellarium.client;

public class PressedKey {
	public EnumKey identifier;
	public int pressedKey;
	public char pressedChar;
	
	public PressedKey(EnumKey key, int eventKey, char eventChar) {
		this.identifier = key;
		this.pressedKey = eventKey;
		this.pressedChar = eventChar;
	}
}
