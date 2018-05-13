package stellarium.render.stellars.atmosphere;

public enum EnumAtmospherePass {
	/*
	 * Prepare/Finalize All Rendering
	 * */
	PrepareRender,
	FinalizeRender,

	/*
	 * Dominate Scattering Rendering (Atmosphere rendering)
	 * */
	PrepareDominateScatter,
	FinalizeDominateScatter,

	/*
	 * Bind Dominate Scattering for info
	 * */
	BindDomination,
	UnbindDomination,

	/*
	 * Setup Typical Rendering
	 * */
	SetupSurfaceScatter,
	SetupPointScatter,
	SetupOpaque,
	SetupOpaqueScatter,

	/*
	 * Dominate Scattering Rendering to the Screen
	 * */
	RenderCachedDominate,

	TestAtmCache;
}