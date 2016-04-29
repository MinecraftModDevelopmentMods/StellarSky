package stellarium.client.gui.pos;

public enum EnumHorizontalPos {
	
	LEFT(0.0),
	CENTER(0.5),
	RIGHT(1.0);
	
	public static final String[] names = {LEFT.name(), CENTER.name(), RIGHT.name()};
	
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
	 * Offset for certain positioned element.
	 * */
	public int getOffset(int full, int control) {
		return (int)((full - control) * this.ratio);
	}
	
	/**
	 * Checks if in range.
	 * */
	public boolean inRange(int pos, int full, int control) {
		return pos >= this.getOffset(full, control) && pos <= this.getOffset(full, control) + control;
	}

	public static EnumHorizontalPos getNearest(int pos, int full, int control) {
		int current = Integer.MAX_VALUE;
		int currentDist;
		EnumHorizontalPos currentPos = null;
		for(EnumHorizontalPos value : values()) {
			currentDist = Math.abs(pos - control / 2 - value.getOffset(full, control));
			if(current > currentDist)
			{
				current = currentDist;
				currentPos = value;
			}
		}
		
		return currentPos;
	}

}
