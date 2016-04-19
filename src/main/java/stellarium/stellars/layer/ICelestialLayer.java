package stellarium.stellars.layer;

import java.io.IOException;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarapi.api.celestials.ICelestialCollection;
import stellarapi.api.lib.config.IConfigHandler;
import stellarapi.api.lib.config.INBTConfig;

public interface ICelestialLayer<T extends INBTConfig, S extends IConfigHandler> 
		extends ICelestialCollection {
	public void initializeClient(boolean isRemote, S config) throws IOException;
	public void initializeCommon(boolean isRemote, T config) throws IOException;

	public void updateLayer(double year);
	
	public List<? extends CelestialObject> getObjectList();
	
	public int getLayerRendererIndex();
	
	@SideOnly(Side.CLIENT)
	public void registerRenderers();
	
}
