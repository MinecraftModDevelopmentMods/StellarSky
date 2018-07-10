package stellarium.stellars.milkyway;

import net.minecraft.util.ResourceLocation;
import stellarapi.api.celestials.EnumObjectType;
import stellarium.StellarSkyReferences;
import stellarium.stellars.layer.StellarObject;

public class Milkyway extends StellarObject {
	public Milkyway() {
		super(new ResourceLocation(StellarSkyReferences.MODID, "milkyway"),
				EnumObjectType.DeepSkyObject);
		// TODO All Auto-generated constructor stub
		// TODO All find period
	}
}
