package stellarium.api;

import net.minecraftforge.client.IRenderHandler;

public abstract class IAdaptiveRenderer extends IRenderHandler {

	public abstract IAdaptiveRenderer setReplacedRenderer(IRenderHandler handler);

}
