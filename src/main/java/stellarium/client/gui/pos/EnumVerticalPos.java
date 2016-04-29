package stellarium.client.gui.pos;

public enum EnumVerticalPos {
	
	UP(0.0),
	CENTER(0.5),
	DOWN(1.0);
	
	public static final String[] names = {UP.name(), CENTER.name(), DOWN.name()};
	
	private double ratio;
		
	EnumVerticalPos(double ratio) {
		this.ratio = ratio;
	}
	
	/**
	 * Translates certain actual position into vertically controlled position.
	 * */
	public int translateInto(int pos, int full, int control) {
		return pos - (int)((full - control) * this.ratio);
	}
	
	/**
	 * Offset for certain positioned element
	 * */
	public int getOffset(int full, int control) {
		return (int)((full - control) * this.ratio);
	}
}
