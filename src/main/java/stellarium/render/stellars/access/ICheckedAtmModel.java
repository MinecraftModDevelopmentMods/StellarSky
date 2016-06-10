package stellarium.render.stellars.access;

import stellarapi.api.lib.math.SpCoord;

public interface ICheckedAtmModel {
	public boolean isRendered(SpCoord pos);
}
