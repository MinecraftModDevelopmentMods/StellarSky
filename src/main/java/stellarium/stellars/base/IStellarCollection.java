package stellarium.stellars.base;

import java.io.IOException;
import java.util.List;

import stellarium.stellars.view.IStellarViewpoint;

public interface IStellarCollection {
	
	public void initialize() throws IOException;
	public List<? extends StellarObject> getObjects();

}
