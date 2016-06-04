package stellarium.render.stellars.atmosphere;

public enum EnumAtmospherePass {
	PrepareDominateScatter,
	FinalizeDominateScatter,

	BindDomination,
	UnbindDomination,

	SetupSurfaceScatter,
	SetupPointScatter,
	SetupOpaque,
	SetupOpaqueScatter,
	
	RenderCachedDominate;
}