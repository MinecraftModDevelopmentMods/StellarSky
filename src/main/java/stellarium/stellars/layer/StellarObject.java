package stellarium.stellars.layer;

import stellarapi.api.celestials.ICelestialObject;

public abstract class StellarObject {
	public abstract String getID();
	
	public int hashCode() {
		return getID().hashCode();
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof StellarObject)
			return ((StellarObject) obj).getID().equals(this.getID());
		return false;
	}
}
