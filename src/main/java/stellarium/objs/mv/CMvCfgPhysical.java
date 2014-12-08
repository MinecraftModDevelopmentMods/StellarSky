package stellarium.objs.mv;

import stellarium.config.ICfgArrMListener;
import stellarium.config.IConfigCategory;
import stellarium.config.IConfigProperty;
import stellarium.config.IMConfigProperty;
import stellarium.config.IPropertyRelation;
import stellarium.config.IStellarConfig;
import stellarium.construct.CPropLangRegistry;
import stellarium.construct.CPropLangStrs;
import stellarium.objs.mv.cbody.ICBodyType;
import stellarium.objs.mv.cbody.TypeCBodyRelation;
import stellarium.objs.mv.orbit.IOrbitType;
import stellarium.objs.mv.orbit.TypeOrbitRelation;

public class CMvCfgPhysical extends CMvCfgBase implements ICfgArrMListener {

	private StellarMv ins;
	
	public CMvCfgPhysical(StellarMv pins)
	{
		super(pins);
	}

	@Override
	public boolean handleOrbitMissing(CMvEntry ent, IConfigCategory cat) {
		cat.getConfig().addLoadFailMessage(CPropLangStrs.orbmissing, CPropLangStrs.getExpl(CPropLangStrs.orbmissing) + cat.getDisplayName());
		return true;
	}

	@Override
	public boolean handleCBodyMissing(CMvEntry ent, IConfigCategory cat) {
		ent.setCBody(null);
		return false;
	}

	@Override
	public void postLoad(IStellarConfig subConfig) {
		for(IConfigCategory cat : getCfgIteWrapper(subConfig))
		{
			if(subConfig.isImmutable(cat))
				continue;
			
			CMvEntry ent = findEntry(cat);

			ent.orbit().getOrbitType().formOrbit(ent.orbit());
			if(!ent.isVirtual())
				ent.cbody().getCBodyType().formCBody(ent.cbody());
		}
	}
	
}
