package stellarium.construct;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Registry for converting between id(for physical) and lang(for logical) string.
 * the id string which is given for Configuration will be converted into lang form first.
 * */

public class CPropLangRegistry {

	private static CPropLangRegistry ins;
	
	private BiMap<String, String> lmap = HashBiMap.create();
	
	public static CPropLangRegistry instance()
	{
		if(ins == null)
			ins = new CPropLangRegistry();
		return ins;
	}
	
	public CPropLangRegistry()
	{
		CPropLangStrs.onRegister();
	}
	
	public void register(String id, String lang)
	{
		lmap.put(id, lang);
	}
	
	public String getLangfromID(String id)
	{
		return lmap.get(id);
	}
	
	public String getIDfromLang(String lang)
	{
		return lmap.inverse().get(lang);
	}
}
