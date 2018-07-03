package stellarium.render.stellars.layer;

import java.util.Map;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.Maps;

import stellarapi.api.lib.config.IConfigHandler;
import stellarium.client.ClientSettings;
import stellarium.render.stellars.access.IStellarChecker;
import stellarium.stellars.layer.IStellarLayerType;
import stellarium.stellars.layer.StellarCollection;
import stellarium.stellars.layer.StellarObject;
import stellarium.stellars.layer.StellarObjectContainer;
import stellarium.view.ViewerInfo;

public class StellarLayerModel<Obj extends StellarObject> {
	private StellarObjectContainer container;
	private StellarCollection<Obj> collection;

	private Map<Obj, IObjRenderCache> cacheMap = Maps.newHashMap();

	public StellarLayerModel(StellarObjectContainer container) {
		this.container = container;
		container.bindRenderModel(this);
	}

	public IStellarLayerType getLayerType() {
		return container.getType();
	}

	public void addRenderCache(Obj object, IObjRenderCache<Obj, ?, ?> renderCache) {
		Validate.notNull(object);
		Validate.notNull(renderCache);
		cacheMap.put(object, renderCache);
	}


	public void onLoadCollection(StellarCollection<Obj> collection) {
		this.collection = collection;
	}
	
	public void updateSettings(ClientSettings settings) {
		for(Map.Entry<Obj, IObjRenderCache> entry : cacheMap.entrySet())
			entry.getValue().updateSettings(settings, this.getSubSettings(settings), entry.getKey());
	}

	public void onStellarTick(ViewerInfo update, IStellarChecker checker) {
		Validate.notNull(this.collection);

		for(Map.Entry<Obj, IObjRenderCache> entry : cacheMap.entrySet())
			entry.getValue().updateCache(entry.getKey(),
					collection.loadImageFor(entry.getKey()), update, checker);
	}

	public Iterable<IObjRenderCache> getRenderCaches() {
		return cacheMap.values();
	}
	
	private IConfigHandler getSubSettings(ClientSettings settings) {
		return settings.getSubConfig(container.getConfigName());
	}

	public StellarLayerModel copy(StellarObjectContainer copied) {
		StellarLayerModel model = new StellarLayerModel(copied);
		model.cacheMap = Maps.newHashMap(this.cacheMap);
		return model;
	}
}
