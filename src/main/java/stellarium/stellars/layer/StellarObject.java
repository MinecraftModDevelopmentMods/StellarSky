package stellarium.stellars.layer;

import net.minecraft.util.ResourceLocation;
import stellarapi.api.celestials.CelestialObject;
import stellarapi.api.celestials.EnumObjectType;

public abstract class StellarObject extends CelestialObject {
	public StellarObject(ResourceLocation nameIn, EnumObjectType typeIn) {
		super(nameIn, typeIn);
	}

	public int hashCode() {
		return this.getName().hashCode();
	}

	public boolean equals(Object obj) {
		if(obj instanceof StellarObject)
			return ((StellarObject) obj).getName().equals(this.getName());
		return false;
	}
}
