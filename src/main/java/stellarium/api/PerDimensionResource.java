package stellarium.api;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class PerDimensionResource {
	private String textureId;
	private ResourceLocation defaultLocation;
	
	public PerDimensionResource(String textureId, ResourceLocation defaultLocation) {
		this.textureId = textureId;
		this.defaultLocation = defaultLocation;
	}
	
	public String getTextureId() {
		return this.textureId;
	}
	
	public ResourceLocation getLocationFor(World world) {
		ResourceLocation location = null;
		if(StellarSkyAPI.hasSkyProvider(world))
			location = StellarSkyAPI.getSkyProvider(world).getPerDimensionResourceLocation(this.textureId);
		
		return location != null? location : this.defaultLocation;
	}
}
