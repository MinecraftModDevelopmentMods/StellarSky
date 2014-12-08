package stellarium.objs.mv.cbody;

import stellarium.config.IConfigCategory;
import stellarium.config.IStellarConfig;
import stellarium.objs.mv.CMvEntry;
import stellarium.objs.mv.orbit.Orbit;
import stellarium.render.ISObjRenderer;
import stellarium.world.CWorldProvider;

public interface ICBodyType {
	
	/**gives name of this type*/
	public String getTypeName();
	
	/**initiation for this type*/
	public void init();
	
	
	/**forms configuration for this type*/
	public void formatConfig(IConfigCategory cfg);
	
	/**remove properties from this type*/
	public void removeConfig(IConfigCategory cat);
	
	/**provides CBody from the entry*/
	public CBody provideCBody(CMvEntry e);
	
	/**applies the configuration to the body*/
	public void apply(CBody body, IConfigCategory cfg);
	
	/**saves the body as configuration*/
	public void save(CBody body, IConfigCategory cfg);
	
	/**checks the current settings and forms the celestial body*/
	public void formCBody(CBody body);
	
	
	/**Copies the CBody.*/
	public void setCopy(CBody ref, CBody target);
	
	/**do tasks needed for remove*/
	public void onRemove(CBody body);
	
	/**gives CBody Renderer for this type*/
	public ICBodyRenderer getCBodyRenderer();
	
	/**gives WorldProvider for this type of CBody*/
	public CWorldProvider getCWorldProvider();
	
}
