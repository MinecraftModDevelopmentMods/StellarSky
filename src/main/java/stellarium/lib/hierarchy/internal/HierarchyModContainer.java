package stellarium.lib.hierarchy.internal;

import java.util.Arrays;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModMetadata;

public class HierarchyModContainer extends DummyModContainer {
	
	public HierarchyModContainer() {
		super(new ModMetadata());
        ModMetadata meta = getMetadata();
        meta.modId="HierarchyLoader";
        meta.name="Hierarchy Loader";
        meta.version="1.0";
        meta.description="Simple coremod for loading hierarchy. "
        		+ "This mod DOES NOT affect compatibility in any level, "
        		+ "So does not report it in any reason.";
	}

}
