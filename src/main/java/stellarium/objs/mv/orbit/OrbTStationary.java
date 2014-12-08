package stellarium.objs.mv.orbit;

import stellarium.config.IConfigCategory;
import stellarium.construct.CPropLangStrsCBody;
import stellarium.objs.mv.CMvEntry;

public class OrbTStationary implements IOrbitType {

	@Override
	public String getTypeName() {
		return CPropLangStrsCBody.storb;
	}

	@Override
	public void init() {
		
	}

	@Override
	public void formatConfig(IConfigCategory cat) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeConfig(IConfigCategory cat) {
		// TODO Auto-generated method stub

	}

	@Override
	public Orbit provideOrbit(CMvEntry e) {
		return new OrbitStationary(e);
	}

	@Override
	public void apply(Orbit orbit, IConfigCategory cfg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void save(Orbit orbit, IConfigCategory cfg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void formOrbit(Orbit orb) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setScaled(Orbit ref, Orbit target, double scale) {
		//Does not called
	}

	@Override
	public boolean hasParent() {
		return false;
	}

	@Override
	public void onRemove(Orbit orbit) {
		// TODO Auto-generated method stub

	}

	public class OrbitStationary extends Orbit
	{

		public OrbitStationary(CMvEntry e) {
			super(e);
		}

		@Override
		public double getAvgSize() {
			return 0;
		}

		@Override
		public IOrbitType getOrbitType() {
			return OrbTStationary.this;
		}
		
	}
}
