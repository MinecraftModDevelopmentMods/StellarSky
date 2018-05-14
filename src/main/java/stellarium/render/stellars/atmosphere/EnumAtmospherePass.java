package stellarium.render.stellars.atmosphere;

public enum EnumAtmospherePass {
	/*
	 * Dominate Scattering Rendering (Atmosphere rendering)
	 * */
	PrepareDominateScatter,
	FinalizeDominateScatter,

	/*
	 * Setup Typical Rendering
	 * */
	SetupSurfaceScatter,
	SetupPointScatter,
	SetupOpaque,
	SetupOpaqueScatter;
}