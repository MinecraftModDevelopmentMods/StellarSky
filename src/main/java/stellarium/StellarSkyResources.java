package stellarium;

import net.minecraft.util.ResourceLocation;
import stellarapi.api.perdimres.PerDimensionResource;
import stellarapi.feature.perdimres.PerDimensionResourceRegistry;

public class StellarSkyResources {

	public static final PerDimensionResource resourceEndSky =
			new PerDimensionResource("End_Sky", new ResourceLocation("textures/environment/end_sky.png"));


	public static final PerDimensionResource resourceMilkyway =
			new PerDimensionResource("Milkyway", new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "stellar/milkyway.png"));

	public static final PerDimensionResource resourceStar =
			new PerDimensionResource("Star", new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "stellar/star.png"));

	public static final PerDimensionResource resourceSunSurface =
			new PerDimensionResource("Sun_Surface", new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "stellar/sun.png"));

	public static final PerDimensionResource resourceSunHalo =
			new PerDimensionResource("Sun_Halo", new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "stellar/halo.png"));

	public static final PerDimensionResource resourceMoonSurface = 
			new PerDimensionResource("Moon_Surface", new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "stellar/lune.png"));

	public static final PerDimensionResource resourceMoonHalo =
			new PerDimensionResource("Moon_Halo", new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "stellar/haloLune.png"));

	public static final PerDimensionResource resourcePlanetSmall =
			new PerDimensionResource("Planet_Small", new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "stellar/star.png"));


	public static final PerDimensionResource resourceLandscape =
			new PerDimensionResource("Landscape", new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/overlay/landscape.png"));

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

	public static final ResourceLocation vertexScope =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "shaders/postprocess/scope.vsh");

	public static final ResourceLocation fragmentScope =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "shaders/postprocess/scope.psh");

	public static final ResourceLocation vertexSkyToQueried =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "shaders/postprocess/sky_to_queried.vsh");

	public static final ResourceLocation fragmentSkyToQueried =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "shaders/postprocess/sky_to_queried.psh");

	public static final ResourceLocation vertexHDRtoLDR =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "shaders/postprocess/hdr_to_ldr.vsh");

	public static final ResourceLocation fragmentHDRtoLDR =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "shaders/postprocess/hdr_to_ldr.psh");

	public static final ResourceLocation vertexLinearToSRGB =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "shaders/postprocess/linear_to_srgb.vsh");

	public static final ResourceLocation fragmentLinearToSRGB =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "shaders/postprocess/linear_to_srgb.psh");


	public static final ResourceLocation vertexAtmosphereSingle =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "shaders/atmosphere_single.vsh");

	public static final ResourceLocation fragmentAtmosphereSingle =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "shaders/atmosphere_single.psh");

	public static final ResourceLocation vertexAtmExtinction =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "shaders/atmosphere_extinction.vsh");

	public static final ResourceLocation fragmentAtmExtinction =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "shaders/atmosphere_extinction.psh");


	public static final ResourceLocation vertexPoint =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "shaders/point.vsh");

	public static final ResourceLocation fragmentPoint =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "shaders/point.psh");

	public static final ResourceLocation vertexTexured =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "shaders/textured.vsh");

	public static final ResourceLocation fragmentTextured =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "shaders/textured.psh");


	public static final ResourceLocation unroll =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/unroll.png");

	public static final ResourceLocation clicked = 
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/clicked.png");

	public static final ResourceLocation fix = 
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/fix.png");

	public static final ResourceLocation hhmm = 
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/hhmm.png");

	public static final ResourceLocation tick = 
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/tick.png");

	public static final ResourceLocation ampm = 
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/ampm.png");


	public static final ResourceLocation round =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/round.png");

	public static final ResourceLocation parallel =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/parallel.png");

	public static final ResourceLocation scroll =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/scroll.png");


	public static final ResourceLocation background =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/background.png");

	public static final ResourceLocation rollround =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/rollround.png");

	public static final ResourceLocation rollparallel =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/rollparallel.png");

	public static final ResourceLocation rollscroll =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/rollscroll.png");


	public static final ResourceLocation scrollbtn =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/scrollbtn.png");

	public static final ResourceLocation scrollregion =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/scrollregion.png");


	public static final ResourceLocation lock =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/lock.png");

	public static final ResourceLocation unlock =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/unlock.png");


	public static final ResourceLocation horizontal =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/horizontal.png");

	public static final ResourceLocation equatorial =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/equatorial.png");

	public static final ResourceLocation ecliptic =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/ecliptic.png");

	public static final ResourceLocation display =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/display.png");

	public static final ResourceLocation alpha =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/alpha.png");

	public static final ResourceLocation fragment =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/fragments.png");

	public static final ResourceLocation fillfragment =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/fillfragments.png");


	public static final ResourceLocation horizon =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/horizon.png");

	public static final ResourceLocation equator =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/equator.png");

	public static final ResourceLocation eclipticline =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/eclipticline.png");

	public static final ResourceLocation horizontalgrid =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/horizontalgrid.png");

	public static final ResourceLocation equatorialgrid =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/equatorialgrid.png");

	public static final ResourceLocation eclipticgrid =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/eclipticgrid.png");


	public static final ResourceLocation landscape =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/landscape.png");


	public static final ResourceLocation optics =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/optics.png");

	public static final ResourceLocation turbulance =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/turbulance.png");

	public static final ResourceLocation brcontrast =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/brcontrast.png");

	public static final ResourceLocation spritescale =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/spritescale.png");


	public static final ResourceLocation solarsystem =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/solarsystem.png");

	public static final ResourceLocation moonfrag =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/moonfrag.png");


	public static final ResourceLocation milkywaycategory =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/milkyway.png");

	public static final ResourceLocation milkywayfrag =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/milkywayfrag.png");

	public static final ResourceLocation milkywaybrightness =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/milkywaybrightness.png");


	public static final ResourceLocation atmosphereSettings =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/atmosphere.png");

	public static final ResourceLocation atmosphereCacheLevel =
			new ResourceLocation(StellarSkyReferences.RESOURCE_ID, "textures/gui/atmospherecachelevel.png");
}
