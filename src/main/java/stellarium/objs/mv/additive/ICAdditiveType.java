package stellarium.objs.mv.additive;

import stellarium.config.IConfigCategory;
import stellarium.config.IStellarConfig;
import stellarium.objs.mv.CMvEntry;

public interface ICAdditiveType {
	
	/**gives name of this type*/
	public String getTypeName();
	
	/**forms configuration for this type*/
	public void formatConfig(IStellarConfig cfg);
	
	/**remove properties from this type*/
	public void removeConfig(IConfigCategory cat);
	
	/**provides CAdditive from the entry*/
	public CAdditive provideCAdditive(CMvEntry e);
	
	/**populates the additive with configuration*/
	public void populate(CAdditive add, IStellarConfig cfg);
	
	/**do tasks needed for remove*/
	public void onRemove(CAdditive add);
	
	/**gives CAdditive Renderer for this type*/
	public ICAdditiveRenderer getCAdditiveRenderer();
	
}
