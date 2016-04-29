package stellarium.stellars.display;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import stellarium.stellars.display.eccoord.EcGridType;
import stellarium.stellars.display.eqcoord.EqGridType;
import stellarium.stellars.display.horcoord.HorGridType;
import stellarium.stellars.layer.IRenderCache;

public class DisplayRegistry {
	
	private static final DisplayRegistry instance = new DisplayRegistry();
	
	public static DisplayRegistry getInstance() {
		return instance;
	}
	
	static {
		instance.register(new HorGridType());
		instance.register(new EqGridType());
		instance.register(new EcGridType());
	}
	
	private List<IDisplayElementType> list = Lists.newArrayList();
	private ImmutableList.Builder<Delegate> builder = ImmutableList.builder();
	
	public <Cfg extends PerDisplaySettings, Cache extends IDisplayRenderCache<Cfg>>
	void register(IDisplayElementType<Cfg, Cache> type) {
		list.add(type);
		builder.add(new Delegate<Cfg, Cache>(type));
	}
	
	public Ordering<IDisplayElementType> generateOrdering() {
		return Ordering.explicit(this.list).reverse();
	}
	
	public ImmutableList<Delegate> generateList() {
		return builder.build();
	}
	
	static class Delegate<Cfg extends PerDisplaySettings, Cache extends IDisplayRenderCache<Cfg>> {
		private Delegate(IDisplayElementType<Cfg, Cache> input) {
			this.type = input;
		}

		private int renderId = -1;
		private IDisplayElementType<Cfg, Cache> type;
		
		public IRenderCache getWrappedCache() {
			return new WrappedDisplayRenderCache(this);
		}

		public IDisplayElementType<Cfg, Cache> getType() {
			return type;
		}

		public int getRenderId() {
			return this.renderId;
		}

		public void setRenderId(int renderId) {
			this.renderId = renderId;
		}
	}
}
