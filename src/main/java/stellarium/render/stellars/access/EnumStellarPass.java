package stellarium.render.stellars.access;

public enum EnumStellarPass {
	/** Light source which contributes to the total intensity */
	Source(false),
	/** Opaque object */
	Opaque(true),
	/** Dominating scatter for brightest objects */
	DominateScatter(true, false);

	public final boolean isDominate, isOpaque;

	EnumStellarPass(boolean isOpaque) {
		this.isDominate = false;
		this.isOpaque = isOpaque;
	}

	EnumStellarPass(boolean isDominate, boolean isOpaque) {
		this.isDominate = isDominate;
		this.isOpaque = isOpaque;
	}
}
