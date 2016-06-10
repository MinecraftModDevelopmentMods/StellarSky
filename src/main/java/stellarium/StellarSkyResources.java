package stellarium;

import net.minecraft.util.ResourceLocation;
import stellarapi.api.perdimres.PerDimensionResource;
import stellarapi.feature.perdimres.PerDimensionResourceRegistry;

public class StellarSkyResources {
	
	public static final PerDimensionResource resourceEndSky =
			new PerDimensionResource("End_Sky", new ResourceLocation("textures/environment/end_sky.png"));
	
	
	public static final PerDimensionResource resourceMilkyway =
			new PerDimensionResource("Milkyway", new ResourceLocation(StellarSkyReferences.resourceId, "stellar/milkyway.png"));
	
	public static final PerDimensionResource resourceStar =
			new PerDimensionResource("Star", new ResourceLocation(StellarSkyReferences.resourceId, "stellar/star.png"));

	public static final PerDimensionResource resourceSunSurface =
			new PerDimensionResource("Sun_Surface", new ResourceLocation(StellarSkyReferences.resourceId, "stellar/sun.png"));

	public static final PerDimensionResource resourceSunHalo =
			new PerDimensionResource("Sun_Halo", new ResourceLocation(StellarSkyReferences.resourceId, "stellar/halo.png"));
	
	public static final PerDimensionResource resourceMoonSurface = 
			new PerDimensionResource("Moon_Surface", new ResourceLocation(StellarSkyReferences.resourceId, "stellar/lune.png"));
	
	public static final PerDimensionResource resourceMoonHalo =
			new PerDimensionResource("Moon_Halo", new ResourceLocation(StellarSkyReferences.resourceId, "stellar/haloLune.png"));
	
	public static final PerDimensionResource resourcePlanetSmall =
			new PerDimensionResource("Planet_Small", new ResourceLocation(StellarSkyReferences.resourceId, "stellar/star.png"));
	
	
	public static final PerDimensionResource resourceLandscape =
			new PerDimensionResource("Landscape", new ResourceLocation(StellarSkyReferences.resourceId, "textures/overlay/landscape.png"));

	public static void init() {
		PerDimensionResourceRegistry.getInstance().registerResourceId("End_Sky");
		PerDimensionResourceRegistry.getInstance().registerResourceId("Milkyway");
		PerDimensionResourceRegistry.getInstance().registerResourceId("Star");
		
		PerDimensionResourceRegistry.getInstance().registerResourceId("Sun_Surface");
		PerDimensionResourceRegistry.getInstance().registerResourceId("Sun_Halo");
		PerDimensionResourceRegistry.getInstance().registerResourceId("Moon_Surface");
		PerDimensionResourceRegistry.getInstance().registerResourceId("Moon_Halo");		
		PerDimensionResourceRegistry.getInstance().registerResourceId("Planet_Small");
		
		PerDimensionResourceRegistry.getInstance().registerResourceId("Landscape");
	}
	
	
	public static final ResourceLocation vertexAtmosphereSimple =
			new ResourceLocation(StellarSkyReferences.resourceId, "shaders/atmosphere_simple.vsh");
	
	public static final ResourceLocation fragmentAtmosphereSimple =
			new ResourceLocation(StellarSkyReferences.resourceId, "shaders/atmosphere_simple.psh");
	
	public static final ResourceLocation vertexAtmosphereSpCoord =
			new ResourceLocation(StellarSkyReferences.resourceId, "shaders/atmosphere_spcoord.vsh");
	
	public static final ResourceLocation fragmentAtmosphereSpCoord =
			new ResourceLocation(StellarSkyReferences.resourceId, "shaders/atmosphere_spcoord.psh");
	
	public static final ResourceLocation vertexScatterPointMapped =
			new ResourceLocation(StellarSkyReferences.resourceId, "shaders/scatter_point_mapped.vsh");
	
	public static final ResourceLocation fragmentScatterPointMapped =
			new ResourceLocation(StellarSkyReferences.resourceId, "shaders/scatter_point_mapped.psh");
	
	public static final ResourceLocation vertexScatterPointSkylight =
			new ResourceLocation(StellarSkyReferences.resourceId, "shaders/scatter_point_skylight.vsh");
	
	public static final ResourceLocation fragmentScatterPointSkylight =
			new ResourceLocation(StellarSkyReferences.resourceId, "shaders/scatter_point_skylight.psh");
	
	public static final ResourceLocation vertexScatterFuzzyMapped =
			new ResourceLocation(StellarSkyReferences.resourceId, "shaders/scatter_fuzzy_mapped.vsh");
	
	public static final ResourceLocation fragmentScatterFuzzyMapped =
			new ResourceLocation(StellarSkyReferences.resourceId, "shaders/scatter_fuzzy_mapped.psh");
	
	public static final ResourceLocation vertexScatterFuzzySkylight =
			new ResourceLocation(StellarSkyReferences.resourceId, "shaders/scatter_fuzzy_skylight.vsh");
	
	public static final ResourceLocation fragmentScatterFuzzySkylight =
			new ResourceLocation(StellarSkyReferences.resourceId, "shaders/scatter_fuzzy_skylight.psh");
	
	public static final ResourceLocation vertexTextureMapped =
			new ResourceLocation(StellarSkyReferences.resourceId, "shaders/src_texture_mapped.vsh");
	
	public static final ResourceLocation fragmentTextureMapped =
			new ResourceLocation(StellarSkyReferences.resourceId, "shaders/src_texture_mapped.psh");
	
	public static final ResourceLocation vertexTextureSkylight =
			new ResourceLocation(StellarSkyReferences.resourceId, "shaders/src_texture_skylight.vsh");
	
	public static final ResourceLocation fragmentTextureSkylight =
			new ResourceLocation(StellarSkyReferences.resourceId, "shaders/src_texture_skylight.psh");
	
	
	public static final ResourceLocation unroll =
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/unroll.png");

	public static final ResourceLocation clicked = 
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/clicked.png");
	
	public static final ResourceLocation fix = 
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/fix.png");
	
	public static final ResourceLocation hhmm = 
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/hhmm.png");
	
	public static final ResourceLocation tick = 
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/tick.png");

	
	public static final ResourceLocation round =
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/round.png");
	
	public static final ResourceLocation parallel =
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/parallel.png");
	
	public static final ResourceLocation scroll =
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/scroll.png");
	
	
	public static final ResourceLocation background =
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/background.png");
	
	public static final ResourceLocation rollround =
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/rollround.png");
	
	public static final ResourceLocation rollparallel =
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/rollparallel.png");
	
	public static final ResourceLocation rollscroll =
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/rollscroll.png");
	
	
	public static final ResourceLocation scrollbtn =
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/scrollbtn.png");
	
	public static final ResourceLocation scrollregion =
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/scrollregion.png");
	
	
	public static final ResourceLocation lock =
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/lock.png");
	
	public static final ResourceLocation unlock =
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/unlock.png");
	
	
	public static final ResourceLocation horizontal =
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/horizontal.png");
	
	public static final ResourceLocation equatorial =
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/equatorial.png");
	
	public static final ResourceLocation ecliptic =
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/ecliptic.png");
	
	public static final ResourceLocation display =
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/display.png");
	
	public static final ResourceLocation alpha =
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/alpha.png");
	
	public static final ResourceLocation fragment =
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/fragments.png");

	public static final ResourceLocation fillfragment =
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/fillfragments.png");
	
	
	public static final ResourceLocation horizon =
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/horizon.png");

	public static final ResourceLocation equator =
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/equator.png");
	
	public static final ResourceLocation eclipticline =
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/eclipticline.png");
	
	public static final ResourceLocation horizontalgrid =
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/horizontalgrid.png");

	public static final ResourceLocation equatorialgrid =
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/equatorialgrid.png");
	
	public static final ResourceLocation eclipticgrid =
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/eclipticgrid.png");


	public static final ResourceLocation landscape =
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/landscape.png");
	
	
	public static final ResourceLocation optics =
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/optics.png");
	
	public static final ResourceLocation turbulance =
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/turbulance.png");
	
	public static final ResourceLocation brcontrast =
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/brcontrast.png");
	
	
	public static final ResourceLocation solarsystem =
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/solarsystem.png");

	public static final ResourceLocation moonfrag =
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/moonfrag.png");
	
	
	public static final ResourceLocation milkywaycategory =
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/milkyway.png");
	
	public static final ResourceLocation milkywayfrag =
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/milkywayfrag.png");
	
	public static final ResourceLocation milkywaybrightness =
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/milkywaybrightness.png");

}
