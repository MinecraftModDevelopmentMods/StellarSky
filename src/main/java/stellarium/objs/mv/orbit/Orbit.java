package stellarium.objs.mv.orbit;

import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.ECoord;
import sciapi.api.value.euclidian.EVector;
import stellarium.objs.mv.CMvEntry;
import stellarium.objs.mv.cbody.ICBodyType;

public abstract class Orbit {

	protected CMvEntry entry;
	
	public Orbit(CMvEntry e)
	{
		entry = e;
	}
	
	public CMvEntry getEntry()
	{
		return entry;
	}
	
	public void update()
	{
		
	}
	
	public void update(double year)
	{
		
	}
	
	public IValRef<EVector> getPosition(double partime)
	{
		//TODO Total Stub;
		return null;
	}
	
	public IValRef<EVector> getVelocity(double partime)
	{
		//TODO Total Stub;
		return null;
	}
	
	public ECoord getOrbCoord(double partime)
	{
		//TODO Total Stub;
		return null;
	}
	
	abstract public double getAvgSize();
	
	abstract public IOrbitType getOrbitType();
}
