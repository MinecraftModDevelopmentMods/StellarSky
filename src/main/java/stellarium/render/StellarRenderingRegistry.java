package stellarium.render;

import java.util.Map;

import com.google.common.collect.Maps;

public class StellarRenderingRegistry {
	
	private static StellarRenderingRegistry instance = new StellarRenderingRegistry();
	
	public static StellarRenderingRegistry instance()
	{
		return instance;
	}
	
    private Map<Integer, ISObjRenderer> sobjRenderers = Maps.newHashMap();
	private int nextId;
	
	public static void registerRenderer(int id, ISObjRenderer renderer){
		instance().sobjRenderers.put(id, renderer);
	}
	
	public static int nextRenderId()
	{
		return instance().nextId;
	}
	
	
	public static ISObjRenderer getRenderer(int id)
	{
		return instance().sobjRenderers.get(id);
	}

}
