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

	public static final PerDimensionResource resourceSunHalo =
			new PerDimensionResource("Sun_Halo", new ResourceLocation(StellarSkyReferences.resourceId, "stellar/halo.png"));
	
	public static final PerDimensionResource resourceMoonSurface = 
			new PerDimensionResource("Moon_Surface", new ResourceLocation(StellarSkyReferences.resourceId, "stellar/lune.png"));
	
	public static final PerDimensionResource resourceMoonHalo =
			new PerDimensionResource("Moon_Halo", new ResourceLocation(StellarSkyReferences.resourceId, "stellar/haloLune.png"));
	
	public static final PerDimensionResource resourcePlanetSmall =
			new PerDimensionResource("Planet_Small", new ResourceLocation(StellarSkyReferences.resourceId, "stellar/star.png"));
	
	public static void init() {
		PerDimensionResourceRegistry.getInstance().registerResourceId("End_Sky");
		PerDimensionResourceRegistry.getInstance().registerResourceId("Milkyway");
		PerDimensionResourceRegistry.getInstance().registerResourceId("Star");
		PerDimensionResourceRegistry.getInstance().registerResourceId("Sun_Halo");
		PerDimensionResourceRegistry.getInstance().registerResourceId("Moon_Surface");
		PerDimensionResourceRegistry.getInstance().registerResourceId("Moon_Halo");
		PerDimensionResourceRegistry.getInstance().registerResourceId("Planet_Small");
	}
	
	
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
	
	public static final ResourceLocation scrollbtn =
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/scrollbtn.png");
	
	public static final ResourceLocation scrollregion =
			new ResourceLocation(StellarSkyReferences.resourceId, "textures/gui/scrollregion.png");
}
