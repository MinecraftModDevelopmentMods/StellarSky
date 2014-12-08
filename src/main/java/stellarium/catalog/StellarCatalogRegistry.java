package stellarium.catalog;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import stellarium.config.EnumCategoryType;
import stellarium.config.IConfigCategory;
import stellarium.config.IStellarConfig;
import stellarium.render.StellarRenderingRegistry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;

import cpw.mods.fml.relauncher.Side;

public class StellarCatalogRegistry {
	
	private static EnumMap<Side, StellarCatalogRegistry> instance = Maps.newEnumMap(Side.class);
	
	public static StellarCatalogRegistry instance(Side side)
	{
		if(instance.get(side) == null)
			instance.put(side, new StellarCatalogRegistry());
		return instance.get(side);
	}
	
	private List<IStellarCatalog> catalog = Lists.newArrayList();
	
	private List<IStellarCatalog> findlist = Lists.newArrayList();
	private PriorityQueue<IStellarCatalog> finds = new PriorityQueue(
			0, new Comparator<IStellarCatalog>() {

				@Override
				public int compare(IStellarCatalog arg0, IStellarCatalog arg1) {
					if(arg0.prioritySearch() > arg1.prioritySearch())
						return 1;
					else return -1;
				}
				
			});
	
	private List<IStellarCatalog> renderlist = Lists.newArrayList();
	private PriorityQueue<IStellarCatalog> renders = new PriorityQueue(
			0, new Comparator<IStellarCatalog>() {

				@Override
				public int compare(IStellarCatalog arg0, IStellarCatalog arg1) {
					if(arg0.priorityRender() > arg1.priorityRender())
						return 1;
					else return -1;
				}
				
			}); 
	
	private boolean ended;
	
	public void register(IStellarCatalog cat)
	{
		catalog.add(cat);
		finds.add(cat);
		renders.add(cat);
	}
	
	public void endRegistry()
	{
		if(ended)
			return;
		
		while(!finds.isEmpty())
			findlist.add(finds.poll());
		
		while(!renders.isEmpty())
			renderlist.add(renders.poll());
		
		ended = true;
	}
	
	public void formatConfig(IStellarConfig cfg)
	{
		cfg.setCategoryType(EnumCategoryType.ConfigList);
		
		for(IStellarCatalog cat : catalog)
		{
			IConfigCategory cfgcat = cfg.addCategory(cat.getCatalogName());
			cfg.setSubConfig(cfgcat);
			cat.formatConfig(cfg);
		}
	}
	
	public void loadCatalog(IStellarConfig cfg)
	{
		for(IStellarCatalog cat : catalog)
			cat.load(cfg.getSubConfig(cfg.getCategory(cat.getCatalogName())));
	}
	
	public void applyCatalogCfg(IStellarConfig cfg)
	{
		for(IStellarCatalog cat : catalog)
			cat.applyConfig(cfg.getSubConfig(cfg.getCategory(cat.getCatalogName())));
	}
	
	public void saveCatalog(IStellarConfig cfg)
	{
		for(IStellarCatalog cat : catalog)
			cat.saveConfig(cfg.getSubConfig(cfg.getCategory(cat.getCatalogName())));
	}
	
	public Iterator<IStellarCatalog> getItetoFind()
	{
		return findlist.iterator();
	}
	
	public Iterator<IStellarCatalog> getItetoRender()
	{
		return renderlist.iterator();
	}
	
}
