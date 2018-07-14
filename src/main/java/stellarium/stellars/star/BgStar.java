package stellarium.stellars.star;

import net.minecraft.util.ResourceLocation;
import stellarapi.api.CelestialPeriod;
import stellarapi.api.celestials.EnumObjectType;
import stellarapi.api.lib.math.Vector3;
import stellarapi.api.view.ICCoordinates;
import stellarium.StellarSkyReferences;
import stellarium.stellars.layer.StellarObject;

public class BgStar extends StellarObject {
	public final boolean hasName;
	protected String name;
	protected int number;
	protected double mag;
	protected double B_V;
	protected Vector3 pos;
	
	public BgStar(String name, int number, double mag, double B_V, Vector3 pos) {
		super(String.valueOf(number),
				new ResourceLocation(StellarSkyReferences.MODID, celestialName(name)),
				EnumObjectType.Star);
		this.setPos(pos);
		this.setStandardMagnitude(mag);

		this.hasName = name.trim().isEmpty();
		this.name = name;
		this.number = number;
		this.mag = mag;
		this.B_V = B_V;
		this.pos = pos;
	}

	private static String celestialName(String name) {
		String constellation = name.substring(7);
		short flamsteedId = 0;
		//byte bayerSubId = 0;

		if(!name.substring(0,3).trim().isEmpty())
			flamsteedId = Short.valueOf(name.substring(0, 3).trim());
		String bayerId = name.substring(3, 6);
		/*if(!name.substring(6,7).trim().isEmpty())
			bayerSubId = Byte.valueOf(name.substring(6,7));*/

		//this.binary = object.name.substring(49, 51);
		//  50- 51  A2     ---     ADScomp  ADS number components
		//  This covers every binaries, (Alp1/2 Cen: A/B)

		return (constellation + flamsteedId + bayerId).trim();
	}


	@Override
	public CelestialPeriod getHorizontalPeriod(ICCoordinates coords) {
		return new CelestialPeriod(String.format("Day; Star %s", this.name),
				coords.getPeriod().getPeriodLength(),
				coords.calculateInitialOffset(this.pos, coords.getPeriod().getPeriodLength()));
	}
}
