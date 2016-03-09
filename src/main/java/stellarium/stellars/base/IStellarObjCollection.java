package stellarium.stellars.base;

import net.minecraft.world.World;

/**
 * Stellar object collection which constructs a layer.
 * */
public interface IStellarObjCollection {
	
	public void update(World world);
	
	public IStellarObj[] getObject();
	
	public IStellarObjRenderer getRendererFor(IStellarObj object);

}
