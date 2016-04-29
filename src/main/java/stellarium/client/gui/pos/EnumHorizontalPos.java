package stellarium.client.gui.pos;

public enum EnumHorizontalPos {
	
	LEFT(0.0),
	CENTER(0.5),
	RIGHT(1.0);
	
	private double ratio;
	
	EnumHorizontalPos(double ratio) {
		this.ratio = ratio;
	}
	
	/**
	 * Translates certain actual position into horizontally controlled position.
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
