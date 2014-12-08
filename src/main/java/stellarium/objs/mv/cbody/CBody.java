package stellarium.objs.mv.cbody;

import sciapi.api.value.euclidian.ECoord;
import sciapi.api.value.util.BOp;
import sciapi.api.value.util.VOp;
import stellarium.catalog.EnumCatalogType;
import stellarium.mech.Wavelength;
import stellarium.objs.EnumSObjType;
import stellarium.objs.IStellarObj;
import stellarium.objs.mv.CMvEntry;
import stellarium.util.math.SpCoord;
import stellarium.util.math.SpCoordf;
import stellarium.view.ViewPoint;

public abstract class CBody implements IStellarObj{

	protected CMvEntry entry;
	
	public CBody(CMvEntry e)
	{
		entry = e;
	}
	
	public CMvEntry getEntry()
	{
		return entry;
	}
	
	public void update(double year)
	{
		
	}
	
	abstract public double getRadius();
	
	@Override
	public String getName() {
		return entry.getName();
	}

	@Override
	public SpCoord getPos(ViewPoint vp, double partime) {
		SpCoord ret = new SpCoord();
		ret.setWithVec(VOp.normalize((BOp.sub(entry.orbit().getPosition(partime), vp.EcRPos))));
		return ret;
	}
	
	public ECoord getCoord(double partime)
	{
		//TODO Total Stub;
		return null;
	}

	@Override
	public double getRadius(Wavelength wl) {
		return getRadius();
	}

	@Override
	public double getMag(Wavelength wl) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getRenderId() {
		return entry.getMain().renderId;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	abstract public EnumSObjType getType();
	
	abstract public ICBodyType getCBodyType();

}
