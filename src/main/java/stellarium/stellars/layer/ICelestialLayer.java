package stellarium.stellars.layer;

import java.io.IOException;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import stellarium.config.IConfigHandler;

public interface ICelestialLayer<T extends IConfigHandler> {
	
	public void initialize(boolean isRemote, T config) throws IOException;
	
	public void updateLayer(double year);
	
	public List<? extends CelestialObject> getObjectList();
	
	public int getLayerRendererIndex();
	
	@SideOnly(Side.CLIENT)
	public void registerRenderers();
}

/**
 * Data:
 *  - Saved in world
 *  - Loaded from configuration
 *  - Used for cache (Render/Effect)
 *  
 *  Client/Common
 *  Configuration/Decided
 *  Saved&Synced/Not Saved&Synced
 *  Consistent/Time.Dep/VP.Dep
 *  
 *  Render: ClientSettings, LayerClientSettings, RenderCache
 *  
 *  (Will discuss later)
 */