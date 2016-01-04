package stellarium.config;

public enum EnumViewMode {
	
	HHMM("hhmm", true, false),
	EMPTY("empty", false, false),
	TICK("tick", true, true);
	
	private String name;
	private boolean showOnHUD;
	private boolean showTick;
	
	public static String[] names = {"hhmm", "empty", "tick"};
	
	EnumViewMode(String name, boolean showOnHUD, boolean showTick) {
		this.name = name;
		this.showOnHUD = showOnHUD;
		this.showTick = showTick;
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
