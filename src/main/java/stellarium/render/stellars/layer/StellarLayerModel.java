package stellarium.render.stellars.layer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import stellarapi.api.lib.config.IConfigHandler;
import stellarium.client.ClientSettings;
import stellarium.stellars.layer.StellarCollection;
import stellarium.stellars.layer.StellarLayer;
import stellarium.stellars.layer.StellarObject;
import stellarium.view.ViewerInfo;

public class StellarLayerModel<Obj extends StellarObject> {
	private StellarCollection<Obj> container;

	private List<Pair<Obj, IObjRenderCache>> caches = new ArrayList<>();

	public StellarLayerModel(StellarCollection<Obj> container) {
		this.container = container;
		container.bindRenderModel(this);
	}

	public StellarLayer getLayerType() {
		return container.getType();
	}

	public void addRenderCache(Obj object, IObjRenderCache<? extends Obj, ?> renderCache) {
		Validate.notNull(object);
		Validate.notNull(renderCache);
		caches.add(Pair.of(object, renderCache));
	}
	
	public void updateSettings(ClientSettings settings) {
		for(Map.Entry<Obj, IObjRenderCache> entry : this.caches)
			entry.getValue().updateSettings(settings, this.getSubSettings(settings), entry.getKey());
	}

	public void onStellarTick(ViewerInfo update) {
		for(Map.Entry<Obj, IObjRenderCache> entry : this.caches)
			entry.getValue().updateCache(entry.getKey(), update);
	}

	public Iterable<Pair<Obj, IObjRenderCache>> getRenderCaches() {
		return this.caches;
	}
	
	private IConfigHandler getSubSettings(ClientSettings settings) {
		return settings.getSubConfig(container.getConfigName());
	}

	public StellarLayerModel<Obj> copy(StellarCollection<Obj> copied) {
		StellarLayerModel<Obj> model = new StellarLayerModel<Obj>(copied);
		model.caches = Lists.newArrayList(this.caches);
		return model;
	}
}
