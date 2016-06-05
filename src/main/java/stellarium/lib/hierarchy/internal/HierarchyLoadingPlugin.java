package stellarium.lib.hierarchy.internal;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.Name("HierarchyLoader")
@IFMLLoadingPlugin.TransformerExclusions("stellarium.lib.hierarchy")
public class HierarchyLoadingPlugin implements IFMLLoadingPlugin {
	@Override
	public String[] getASMTransformerClass() {
		return new String[] {"stellarium.lib.hierarchy.internal.HierarchyClassTransformer"};
	}

	@Override
	public String getModContainerClass() {
		return "stellarium.lib.hierarchy.internal.HierarchyModContainer";
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) { }

	@Override
	public String getAccessTransformerClass() {
		return null;
	}
}
