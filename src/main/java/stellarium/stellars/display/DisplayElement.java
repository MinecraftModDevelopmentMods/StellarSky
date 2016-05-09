package stellarium.stellars.display;

import stellarium.stellars.layer.StellarObject;

public class DisplayElement extends StellarObject {

	private final IDisplayElementType type;
	
	public DisplayElement(DisplayRegistry.Delegate delegate) {
		this.type = delegate.getType();
	}
	
	public IDisplayElementType getType() {
		return this.type;
	}

	@Override
	public String getID() {
		return type.getName();
	}
	
}
