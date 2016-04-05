package stellarium.client;

public enum EnumLockBtnPosition {
	UPRIGHT("upright", 5, 12),
	DOWNLEFT("downleft", -155, 48-6);
	
	private final String name;
	private final int offsetX, offsetY;
	
	public static String[] names = {"upright", "downleft"};
	
	EnumLockBtnPosition(String name, int offsetX, int offsetY) {
		this.name = name;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}

	public String getName() {
		return this.name;
	}
	
	public int getPosX(int width) {
		return width / 2 + this.offsetX;
	}
	
	public int getPosY(int height) {
		return height / 6 + this.offsetY;
	}

	public static EnumLockBtnPosition getModeForName(String name) {
		for(EnumLockBtnPosition mode : EnumLockBtnPosition.values())
			if(mode.name.equals(name))
				return mode;
		return null;
	}

}
