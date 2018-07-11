package stellarium.stellars.milkyway;

import net.minecraft.util.ResourceLocation;
import stellarapi.api.celestials.EnumObjectType;
import stellarium.StellarSkyReferences;
import stellarium.stellars.layer.StellarObject;

public class Milkyway extends StellarObject {
	public Milkyway() {
		super("milkyway",
				new ResourceLocation(StellarSkyReferences.MODID, "milkyway"),
				EnumObjectType.DeepSkyObject);
		this.setStandardMagnitude(4.5);
	}
}
