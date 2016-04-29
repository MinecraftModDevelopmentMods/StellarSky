package stellarium.client.gui.pos;

public class ElementPos {

	private EnumHorizontalPos horizontalPos;
	private EnumVerticalPos verticalPos;
	
	public ElementPos(EnumHorizontalPos hor, EnumVerticalPos ver) {
		this.horizontalPos = hor;
		this.verticalPos = ver;
	}
	
	public EnumHorizontalPos getHorizontalPos() {
		return this.horizontalPos;
	}
	
	public EnumVerticalPos getVerticalPos() {
		return this.verticalPos;
	}
	
	@Override
	public int hashCode() {
		return (horizontalPos.ordinal() << (Integer.SIZE >> 1)) + verticalPos.ordinal();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ElementPos) {
			return horizontalPos == ((ElementPos)obj).horizontalPos
					&& verticalPos == ((ElementPos)obj).verticalPos;
		} else return false;
	}
}
