package stellarium.render.stellars.phased;

public enum EnumStellarPass {
	DominateScatter,
	SurfaceScatter(false, true),
	PointScatter(false, false),
	Opaque(true, true),
	OpaqueScatter(true, false);
	
	public final boolean isOpaque, hasTexture;
	
	EnumStellarPass() {
		
	}
	
	EnumStellarPass(boolean isOpaque, boolean hasTexture) {
		this.isOpaque = isOpaque;
		this.hasTexture = hasTexture;
	}
}
