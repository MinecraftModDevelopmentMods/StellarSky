package stellarium.objs.mv;

import net.minecraft.client.resources.I18n;
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

public class CMvCfgLogical extends CMvCfgBase implements ICfgArrMListener {

	private StellarMvLogical ins;
	
	public CMvCfgLogical(StellarMvLogical pins)
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
		cat.getConfig().addLoadFailMessage(CPropLangStrs.cbmissing, CPropLangStrs.getExpl(CPropLangStrs.cbmissing) + cat.getDisplayName());
		return true;
	}

	@Override
	public void postLoad(IStellarConfig subConfig) { }
	
}
