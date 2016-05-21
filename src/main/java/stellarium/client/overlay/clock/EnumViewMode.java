package stellarium.client.overlay.clock;

public enum EnumViewMode {
	
	HHMM("hhmm", true, false, 135),
	TICK("tick", true, true, 150);
	
	private String name;
	private boolean showOnHUD;
	private boolean showTick;
	private int guiWidth;
	
	public static String[] names = {"hhmm", "tick"};
	
	EnumViewMode(String name, boolean showOnHUD, boolean showTick, int guiWidth) {
		this.name = name;
		this.showOnHUD = showOnHUD;
		this.showTick = showTick;
		this.guiWidth = guiWidth;
	}
	
	public String getName() {
		return this.name;
	}
	
	public boolean showOnHUD() {
		return this.showOnHUD;
	}
	
	public boolean showTick() {
		return this.showTick;
	}
	
	public int getGuiWidth() {
		return this.guiWidth;
	}
	
	public EnumViewMode nextMode() {
		int ordinal = this.ordinal() + 1;
		EnumViewMode[] values = EnumViewMode.values();
		ordinal %= values.length;
		return values[ordinal];
	}

	public static EnumViewMode getModeForName(String name) {
		for(EnumViewMode mode : EnumViewMode.values())
			if(mode.name.equals(name))
				return mode;
		return null;
	}
}
