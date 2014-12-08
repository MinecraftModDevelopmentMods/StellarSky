package stellarium.config;

import java.util.Map;

import stellarium.config.proptype.DoublePropHandler;
import stellarium.config.proptype.IntegerPropHandler;
import stellarium.config.proptype.StringPropHandler;
import stellarium.config.proptype.UDoublePropHandler;

import com.google.common.collect.Maps;

public class ConfigPropTypeRegistry {

	private static ConfigPropTypeRegistry ins;
	
	public static ConfigPropTypeRegistry instance()
	{
		if(ins == null)
			ins = new ConfigPropTypeRegistry();
		return ins;
	}
	
	private Map<String, IConfigPropHandler> hmap = Maps.newHashMap();
	
	static{	
		register("double", new DoublePropHandler());
		register("udouble", new UDoublePropHandler());
		register("integer", new IntegerPropHandler());
		register("string", new StringPropHandler());
	}
	
	public static <T> void register(String proptype, IConfigPropHandler<T> handler)
	{
		instance().hmap.put(proptype, handler);
	}
	
	public static <T> IConfigPropHandler<T> getHandler(String proptype)
	{
		return instance().hmap.get(proptype);
	}
	
}
