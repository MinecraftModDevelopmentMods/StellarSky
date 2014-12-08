package stellarium.objs.mv;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import stellarium.catalog.IStellarCatalog;
import stellarium.construct.CPropLangStrsCBody;
import stellarium.construct.CTypeRegistry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class StellarMvFormatter {

	private Map<String, MvInfo> infmap = Maps.newHashMap();
	
	public void formatMv(StellarMvLogical lmv, StellarMv nmv)
	{
		nmv.Au = lmv.Au;
		nmv.Msun = lmv.Msun;
		nmv.day = lmv.day;
		nmv.yr = lmv.yr;
		
		infmap.clear();
		
		setMassSum(lmv.root);
		
		for(CMvEntry ent : lmv)
		{			
			getInfo(ent).tsatq.clear();
			
			for(CMvEntry sat : ent.getSatelliteList())
			{
				if(0.01 * getInfo(ent).mass_sum < getInfo(sat).mass_sum)
				{
					getInfo(sat).twin = true;
					getInfo(ent).tsatq.offer(sat);
				}
			}
		}
		
		createMvs(lmv.root, null, nmv);
		
		copyScaled(lmv.root, nmv.root, 1.0);
		formatMvs(lmv.root, nmv.root, 1.0, nmv);
	}
	
	public void setMassSum(CMvEntry ent)
	{
		double msum = 0.0;
		
		for(CMvEntry sat : ent.getSatelliteList())
		{
			setMassSum(sat);
			msum += getInfo(sat).mass_sum;
		}
		
		getInfo(ent).mass_sum = msum + ent.getMass();
	}
	
	public void createMvs(CMvEntry lcur, CMvEntry curpar, StellarMv mv)
	{
		if(getInfo(lcur).tsatq.isEmpty())
		{
			CMvEntry pcur = mv.newEntry(curpar, lcur.getName());
			
			for(CMvEntry slc : lcur.getSatelliteList())
			{
				if(!getInfo(slc).twin)
					createMvs(lcur, pcur, mv);
			}
		}
		else {
			CMvEntry far = getInfo(lcur).tsatq.poll();
			curpar = mv.newEntry(curpar, lcur.getName() + "&" + far.getName());
			createMvs(far, curpar, mv);
			createMvs(lcur, curpar, mv);
			getInfo(lcur).tsatq.offer(far);
		}
	}
	
	public void formatMvs(CMvEntry lcur, CMvEntry pcur, double scale, StellarMv mv)
	{
		if(getInfo(lcur).tsatq.isEmpty())
		{
			for(CMvEntry slc : lcur.getSatelliteList())
			{
				if(!getInfo(slc).twin)
				{
					copyScaled(slc, pcur, 1.0);
					
					for(CMvEntry pslc : pcur.getSatelliteList())
					{
						if(pslc.getName().equals(slc.getName()))
							formatMvs(slc, pslc, 1.0, mv);
					}
				}
			}
		}
		else {
			MvInfo inf = getInfo(lcur);
			
			CMvEntry far = inf.tsatq.poll();
			
			double ratio = getInfo(far).mass_sum / inf.mass_sum;
			
			CMvEntry pnext = pcur.getSatelliteList().get(0);
			CMvEntry pfar = pcur.getSatelliteList().get(1);
			
			if(!pfar.getName().equals(far.getName()))
			{
				CMvEntry temp = pnext;
				pnext = pfar;
				pfar = temp;
			}
			
			copyScaled(far, pfar, scale * (1.0 - ratio));
			scale *= ratio;
			copyScaled(far, pnext, scale);
			
			getInfo(lcur).mass_sum -= getInfo(far).mass_sum;
			
			formatMvs(far, pfar, 1.0, mv);				
			formatMvs(lcur, pnext, scale, mv);			
		}
	}
	
	public void copyScaled(CMvEntry orig, CMvEntry tar, double scale)
	{
		if(orig.orbit() == null)
		{
			tar.setOrbit(CTypeRegistry.instance().getOrbType(CPropLangStrsCBody.storb).provideOrbit(tar));
		}
		else{
			tar.setOrbit(orig.orbit().getOrbitType().provideOrbit(tar));
			tar.orbit().getOrbitType().setScaled(orig.orbit(), tar.orbit(), scale);
		}

		if(getInfo(orig).tsatq.isEmpty())
		{
			tar.setCBody(orig.cbody().getCBodyType().provideCBody(tar));
			tar.cbody().getCBodyType().setCopy(orig.cbody(), tar.cbody());
		}
		else tar.setCBody(null);
	}
	
	public MvInfo getInfo(CMvEntry ent)
	{
		MvInfo inf = infmap.get(ent.getName());
		if(inf == null)
			infmap.put(ent.getName(), inf = new MvInfo());
		return inf;
	}
	
	public class MvInfo
	{
		public double mass_sum;
		public boolean twin = false;
		public PriorityQueue<CMvEntry> tsatq = new PriorityQueue(0, new Comparator<CMvEntry>() {

			@Override
			public int compare(CMvEntry arg0, CMvEntry arg1) {
				if(arg0.orbit().getAvgSize() > arg1.orbit().getAvgSize())
					return 1;
				else return -1;
			}
			
		});
	}
	
}
