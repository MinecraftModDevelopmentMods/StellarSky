package stellarium.render.stellars.access;

public enum EnumStellarPass {
	DominateScatter,
	SurfaceScatter(false, true),
	PointScatter(false, false),
	Opaque(true, true),
	OpaqueScatter(true, false);
	
	public final boolean isDominate, isOpaque, hasTexture;
	
	EnumStellarPass() {
		this.isDominate = true;
		this.isOpaque = false;
		this.hasTexture = false;
	}
	
	EnumStellarPass(boolean isOpaque, boolean hasTexture) {
		this.isDominate = false;
		this.isOpaque = isOpaque;
		this.hasTexture = hasTexture;
	}
}
