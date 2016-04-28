package stellarium.stellars.display;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarapi.api.lib.config.IConfigHandler;
import stellarium.render.ICelestialObjectRenderer;
import stellarium.stellars.layer.IRenderCache;
import stellarium.stellars.layer.StellarObject;

public class DisplayElement extends StellarObject {

	private final IDisplayElementType type;
	
	public DisplayElement(LayerDisplay.DisplayDelegate delegate) {
		this.type = delegate.type;
	}
	
	public IDisplayElementType getType() {
		return this.type;
	}

	@Override
	public String getID() {
		return type.getName();
	}
	
}
