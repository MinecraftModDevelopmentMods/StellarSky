package stellarium.stellars.layer;

import net.minecraft.util.ResourceLocation;
import stellarapi.api.CelestialPeriod;
import stellarapi.api.celestials.CelestialObject;
import stellarapi.api.celestials.EnumObjectType;
import stellarapi.api.view.ICCoordinates;

public abstract class StellarObject extends CelestialObject {
	private final String identifier;

	public StellarObject(String id, ResourceLocation nameIn, EnumObjectType typeIn) {
		super(nameIn, typeIn);
		this.identifier = id;
	}

	public void setupCoord(ICCoordinates coords, CelestialPeriod yearPeriod) {
		this.setHoritontalPeriod(coords.getPeriod());
	}

	@Override
	public int hashCode() {
		return this.identifier.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof StellarObject)
			return ((StellarObject) obj).identifier.equals(this.identifier);
		return false;
	}
}
