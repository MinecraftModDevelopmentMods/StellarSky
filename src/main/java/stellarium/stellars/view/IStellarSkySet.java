package stellarium.stellars.view;

import net.minecraft.world.World;
import stellarapi.api.ISkyEffect;
import stellarapi.api.lib.math.SpCoord;

public interface IStellarSkySet extends ISkyEffect {
	public boolean hideObjectsUnderHorizon();
}
