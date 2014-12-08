package stellarium.objs.mv;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

import com.google.common.collect.Lists;

import stellarium.config.IStellarConfig;
import stellarium.objs.mv.cbody.CBody;
import stellarium.util.math.SpCoord;
import stellarium.view.ViewPoint;

/**Logical StellarMv for Configuration*/
public class StellarMvLogical implements Iterable<CMvEntry> {
	
	protected String id;
	public int renderId;
	protected CMvEntry root;
	protected CMvCfgBase cfg;
	
	public double Msun, yr, day, Au;
	
	public StellarMvLogical(String pid)
	{
		id = pid;
		cfg = new CMvCfgLogical(this);
	}
	
	public String getID()
	{
		return id;
	}
	
	public void setID(String pid)
	{
		id = pid;
	}
	
	protected CMvEntry newEntry(CMvEntry par, String name) {
		CMvEntry ne = new CMvEntry(this, par, name);
		if(par != null)
			par.addSatellite(ne);
		else root = ne;
		return ne;
	}
	
	protected void removeEntry(CMvEntry mv)
	{
		if(mv.hasAdditive())
			mv.additive().getAdditiveType().onRemove(mv.additive());
		if(!mv.isVirtual())
			mv.cbody().getCBodyType().onRemove(mv.cbody());
		mv.orbit().getOrbitType().onRemove(mv.orbit());
		
		mv.getParent().removeSatellite(mv);
	}

	
	public class MvIterator implements Iterator<CMvEntry> {

		CMvEntry now = null;
		
		Stack<ListIterator> ites = new Stack();
		
		public boolean hasNextRec()
		{
			if(ites.isEmpty())
				return false;
						
			ListIterator ite = ites.pop();
			boolean hn = ite.hasNext() || hasNextRec();
			ites.push(ite);
			
			return hn;
		}
		
		@Override
		public boolean hasNext() {
			
			if(ites.isEmpty())
				return true;

			if(now != null && now.hasSatellites())
				return true;
			
			return hasNextRec();
			
		}

		@Override
		public CMvEntry next() {
			
			if(ites.isEmpty())
			{
				ListIterator<CMvEntry> rt = root.getSatelliteList().listIterator();
				ites.push(rt);
				return now = rt.next();
			}
			
			if(now.hasSatellites())
			{
				ListIterator<CMvEntry> ite = now.getSatelliteList().listIterator();
				ites.push(ite);
				return now = ite.next();
			}
			
			while(!ites.isEmpty())
			{
				ListIterator<CMvEntry> ite = ites.pop();
				if(ite.hasNext())
				{
					ites.push(ite);
					return now = ite.next();
				}
			}
			
			return null;
		}

		@Override
		public void remove() {
			//Not Removable via this Iterator.
		}
		
	}

	
	public Iterator<CMvEntry> iterator()
	{
		return new MvIterator();
	}

	
	public CMvEntry findEntry(String name)
	{
		for(CMvEntry e : this)
		{
			if(e.getName().equals(name))
				return e;
		}
		
		return null;
	}
	
	public void formatConfig(IStellarConfig subConfig) {
		cfg.formatConfig(subConfig);
	}

	public void loadFromConfig(IStellarConfig subConfig) {
		cfg.loadConfig(subConfig);
	}

	public void saveAsConfig(IStellarConfig subConfig) {
		cfg.saveConfig(subConfig);
	}
	
}
