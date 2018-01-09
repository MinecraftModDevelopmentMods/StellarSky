package stellarium.client.overlay;

import stellarapi.api.gui.overlay.IOverlaySetType;
import stellarapi.api.gui.overlay.IRawOverlayElement;
import stellarium.StellarSkyReferences;

public class StellarSkyOverlays implements IOverlaySetType {

	@Override
	public String getLanguageKey() {
		return "hud.overlay.stellarsky";
	}

	@Override
	public boolean acceptOverlayByDefault(IRawOverlayElement overlay) {
		return overlay.getModId().equals(StellarSkyReferences.MODID);
	}

	@Override
	public boolean isMain() {
		return false;
	}

}
