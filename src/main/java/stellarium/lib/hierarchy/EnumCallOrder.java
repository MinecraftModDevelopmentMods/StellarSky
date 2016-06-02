package stellarium.lib.hierarchy;

public enum EnumCallOrder {
	ParentFirst(true, true),
	ParentLast(true, false),
	Custom(false, true);
	
	public final boolean subCall;
	public final boolean isParentFirst;
	
	EnumCallOrder(boolean subCall, boolean isParentFirst) {
		this.subCall = subCall;
		this.isParentFirst = isParentFirst;
	}
}
