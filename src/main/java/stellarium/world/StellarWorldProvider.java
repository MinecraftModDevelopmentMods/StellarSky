package stellarium.world;

import org.lwjgl.util.vector.Vector3f;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.ForgeHooksClient;
import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EVector;
import sciapi.api.value.euclidian.EVectorSet;
import stellarium.api.ISkyProvider;
import stellarium.stellars.StellarManager;
import stellarium.stellars.util.ExtinctionRefraction;
import stellarium.stellars.view.StellarDimensionManager;
import stellarium.util.math.SpCoord;
import stellarium.util.math.Spmath;
import stellarium.util.math.VecMath;

public class StellarWorldProvider extends WorldProvider implements ISkyProvider {
	
	private static final int DEFAULT_OFFSET = 1000;
	
	private WorldProvider parProvider;
	private StellarManager manager;
	private StellarDimensionManager dimManager;
	
    /** Array for sunrise/sunset colors (RGBA) */
    private float[] colorsSunriseSunset = new float[4];
    
    private long cloudColour = 16777215L;
	
	public StellarWorldProvider(WorldProvider provider, StellarManager manager, StellarDimensionManager dimManager) {
		this.parProvider = provider;
		this.worldObj = provider.worldObj;
		this.field_82913_c = provider.field_82913_c;
		this.terrainType = provider.terrainType;
		this.worldChunkMgr = provider.worldChunkMgr;
		this.isHellWorld = provider.isHellWorld;
		this.hasNoSky = provider.hasNoSky;
		this.lightBrightnessTable = provider.lightBrightnessTable;
		this.dimensionId = provider.dimensionId;
		this.manager = manager;
		this.dimManager = dimManager;
	}
	
	@Override
    public float calculateCelestialAngle(long par1, float par3) {
		double dayLength = manager.getSettings().day;
		double longitude = dimManager.getSettings().longitude / 360.0;
		double skyTime = manager.getSkyTime(par1 + par3);
		double angle = skyTime / dayLength + longitude + 0.5;
		return (float) (angle - Math.floor(angle));
    }
	
	public float calculateSunHeight(long par1, float par3) {
    	if(!manager.updated())
    	{
    		manager.update(par1+par3);
    		dimManager.update(this.worldObj, par1+par3);
    	}
   	
    	return (float) Spmath.sind(dimManager.sunAppPos.y);
	}
	
    /*public float calculateRelativeHeightAngle(long par1, float par3) {
    	if(!manager.updated())
    		manager.update(par1+par3);
    	
    	IValRef<EVector> sun = EVectorSet.ins(3).getNew();
    	
    	sun.set(manager.Sun.getPosition());
    	sun.set(ExtinctionRefraction.refraction(sun, true));
    	sun.set(VecMath.normalize(sun));
    	
    	double h = Math.asin(VecMath.getZ(sun));
    	
    	if(VecMath.getCoord(sun, 0).asDouble()<0) h=Math.PI-h;
    	if(VecMath.getCoord(sun, 0).asDouble()>0 && h<0) h=h+2*Math.PI;
    	
    	return (float)(Spmath.fmod((h/2/Math.PI)+0.75,1.0));
    }*/
    
    @Override
    @SideOnly(Side.CLIENT)
    public float getSunBrightness(float par1)
    {
        float f2 = 1.0F - (this.calculateSunHeight(worldObj.getWorldTime(), par1) * 2.0F + 0.2F);

        if (f2 < 0.0F)
        {
            f2 = 0.0F;
        }

        if (f2 > 1.0F)
        {
            f2 = 1.0F;
        }

        f2 = 1.0F - f2;
        f2 = (float)((double)f2 * (1.0D - (double)(worldObj.getRainStrength(par1) * 5.0F) / 16.0D));
        f2 = (float)((double)f2 * (1.0D - (double)(worldObj.getWeightedThunderStrength(par1) * 5.0F) / 16.0D));
        return f2 * 0.8F + 0.2F;
    }
	
	@Override
	public float getSunBrightnessFactor(float par1) {
        float f1 = 1.0F - (this.calculateSunHeight(worldObj.getWorldTime(), par1) * 2.0F + 0.5F);
        f1 = MathHelper.clamp_float(f1, 0.0F, 1.0F);
        f1 = 1.0F - f1;
        f1 = (float)((double)f1 * (1.0D - (double)(worldObj.getRainStrength(par1) * 5.0F) / 16.0D));
        f1 = (float)((double)f1 * (1.0D - (double)(worldObj.getWeightedThunderStrength(par1) * 5.0F) / 16.0D));
        return f1;
	}

	@Override
    public int getMoonPhase(long par1) {
    	if(!manager.updated())
    		return parProvider.getMoonPhase(par1);
    	
    	return (int)(dimManager.moonFactors[2]*8);
    }
	
	@Override
	public float getCurrentMoonPhaseFactor() {
    	if(!manager.updated())
    		return parProvider.getCurrentMoonPhaseFactor();
		return (float) dimManager.moonFactors[1];
	}
	
	
	/* ---------------------------------------
	 * Start of Stellar Sky API Implementation
	 * --------------------------------------- */
	@Override
	public double getDayLength() {
		return manager.getSettings().day;
	}

	@Override
	public double getLunarMonthLength() {
		double period = dimManager.moonFactors[0];
		return period / (1.0 - period) * manager.getSettings().year;
	}

	@Override
	public double getYearLength() {
		return manager.getSettings().year;
	}

	@Override
	public double getDaytimeOffset() {
		return this.getDaytimeOffset(worldObj.getWorldTime());
	}
	
	@Override
	public double getDaytimeOffset(long tick) {
		return Spmath.fmod((tick + manager.getSettings().tickOffset + DEFAULT_OFFSET) / manager.getSettings().day, 1.0) + dimManager.getSettings().longitude / 360.0 + 0.5;
	}

	@Override
	public double getYearlyOffset() {
		return this.getYearlyOffset(worldObj.getWorldTime());
	}
	
	@Override
	public double getYearlyOffset(long tick) {
		return Spmath.fmod(((tick + manager.getSettings().tickOffset + DEFAULT_OFFSET) / manager.getSettings().day + manager.getSettings().dayOffset) / manager.getSettings().year, 1.0);
	}
	
	@Override
	public double dayOffsetUntilSunReach(double heightAngle) {
		double radLatitude = Spmath.Radians(dimManager.getSettings().latitude);

		SpCoord coord = new SpCoord();
		coord.setWithVec(dimManager.sunEquatorPos);
		
		return this.hourAngleForHeight(heightAngle, Spmath.Radians(coord.y), radLatitude) / (2 * Math.PI);
	}
	
	@Override
	public double dayOffsetUntilMoonReach(double heightAngle) {
		double radLatitude = Spmath.Radians(dimManager.getSettings().latitude);

		SpCoord coord = new SpCoord();
		coord.setWithVec(dimManager.moonEquatorPos);
		
		return this.hourAngleForHeight(heightAngle, Spmath.Radians(coord.y), radLatitude) / (2 * Math.PI);
	}
	
	private double hourAngleForHeight(double heightAngle, double dec, double lat) {
		return Math.acos((Spmath.sind(heightAngle) - Math.sin(dec) * Math.sin(lat)) / (Math.cos(dec) * Math.cos(lat)));
	}

	@Override
	public Vector3f getCurrentSunPosition() {
		EVector sun = dimManager.sunAppPos.getVec();
    	
    	return new Vector3f(sun.getCoord(0).asFloat(),
    			sun.getCoord(1).asFloat(),
    			sun.getCoord(2).asFloat());
	}

	@Override
	public Vector3f getCurrentMoonPosition() {
    	EVector moon = dimManager.moonAppPos.getVec();
    	
    	return new Vector3f(moon.getCoord(0).asFloat(),
    			moon.getCoord(1).asFloat(),
    			moon.getCoord(2).asFloat());
	}

	@Override
	public double getHighestSunHeightAngle() {
		SpCoord crd = new SpCoord();
		crd.setWithVec(dimManager.sunEquatorPos);
		
		return 90.0 - Math.abs(dimManager.getSettings().latitude - crd.y);
	}

	@Override
	public double getHighestMoonHeightAngle() {
		SpCoord crd = new SpCoord();
		crd.setWithVec(dimManager.moonEquatorPos);
		
		return 90.0 - Math.abs(dimManager.getSettings().latitude - crd.y);
	}
	
	/* ---------------------------------------
	 * End of Stellar Sky API Implementation
	 * --------------------------------------- */

    /**
     * Returns a new chunk provider which generates chunks for this world
     */
	@Override
    public IChunkProvider createChunkGenerator()
    {
        return parProvider.createChunkGenerator();
    }

    /**
     * Will check if the x, z position specified is alright to be set as the map spawn point
     */
	@Override
    public boolean canCoordinateBeSpawn(int p_76566_1_, int p_76566_2_)
    {
        return parProvider.canCoordinateBeSpawn(p_76566_1_, p_76566_2_);
    }

    /**
     * Returns 'true' if in the "main surface world", but 'false' if in the Nether or End dimensions.
     */
	@Override
    public boolean isSurfaceWorld()
    {
        return parProvider.isSurfaceWorld();
    }

    /**
     * Returns array with sunrise/sunset colors
     */
    @SideOnly(Side.CLIENT)
    @Override
    public float[] calcSunriseSunsetColors(float p_76560_1_, float p_76560_2_)
    {
        float f2 = 0.4F;
        float f3 = this.calculateSunHeight(worldObj.getWorldTime(), p_76560_2_) - 0.0F;
        float f4 = -0.0F;

        if (f3 >= f4 - f2 && f3 <= f4 + f2)
        {
            float f5 = (f3 - f4) / f2 * 0.5F + 0.5F;
            float f6 = 1.0F - (1.0F - MathHelper.sin(f5 * (float)Math.PI)) * 0.99F;
            f6 *= f6;
            this.colorsSunriseSunset[0] = f5 * 0.3F + 0.7F;
            this.colorsSunriseSunset[1] = f5 * f5 * 0.7F + 0.2F;
            this.colorsSunriseSunset[2] = f5 * f5 * 0.0F + 0.2F;
            this.colorsSunriseSunset[3] = f6;
            return this.colorsSunriseSunset;
        }
        else
        {
            return null;
        }
    }

    /**
     * Return Vec3D with biome specific fog color
     */
    @SideOnly(Side.CLIENT)
    @Override
    public Vec3 getFogColor(float p_76562_1_, float p_76562_2_)
    {
        float f2 = this.calculateSunHeight(worldObj.getWorldTime(), p_76562_2_) * 2.0F + 0.5F;

        if (f2 < 0.0F)
        {
            f2 = 0.0F;
        }

        if (f2 > 1.0F)
        {
            f2 = 1.0F;
        }

        float f3 = 0.7529412F;
        float f4 = 0.84705883F;
        float f5 = 1.0F;
        f3 *= f2 * 0.94F + 0.06F;
        f4 *= f2 * 0.94F + 0.06F;
        f5 *= f2 * 0.91F + 0.09F;
        return Vec3.createVectorHelper((double)f3, (double)f4, (double)f5);
    }

    /**
     * True if the player can respawn in this dimension (true = overworld, false = nether).
     */
    @Override
    public boolean canRespawnHere()
    {
        return parProvider.canRespawnHere();
    }

    /**
     * the y level at which clouds are rendered.
     */
    @SideOnly(Side.CLIENT)
    @Override
    public float getCloudHeight()
    {
        return parProvider.getCloudHeight();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean isSkyColored()
    {
        return parProvider.isSkyColored();
    }

    /**
     * Gets the hard-coded portal location to use when entering this dimension.
     */
    @Override
    public ChunkCoordinates getEntrancePortalLocation()
    {
        return parProvider.getEntrancePortalLocation();
    }

    @Override
    public int getAverageGroundLevel()
    {
        return parProvider.getAverageGroundLevel();
    }

    /**
     * returns true if this dimension is supposed to display void particles and pull in the far plane based on the
     * user's Y offset.
     */
    @SideOnly(Side.CLIENT)
    @Override
    public boolean getWorldHasVoidParticles()
    {
        return parProvider.getWorldHasVoidParticles();
    }

    /**
     * Returns a double value representing the Y value relative to the top of the map at which void fog is at its
     * maximum. The default factor of 0.03125 relative to 256, for example, means the void fog will be at its maximum at
     * (256*0.03125), or 8.
     */
    @SideOnly(Side.CLIENT)
    @Override
    public double getVoidFogYFactor()
    {
        return parProvider.getVoidFogYFactor();
    }

    /**
     * Returns true if the given X,Z coordinate should show environmental fog.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public boolean doesXZShowFog(int p_76568_1_, int p_76568_2_)
    {
        return parProvider.doesXZShowFog(p_76568_1_, p_76568_2_);
    }

    /**
     * Returns the dimension's name, e.g. "The End", "Nether", or "Overworld".
     */
    @Override
    public String getDimensionName() {
    	return parProvider.getDimensionName();
    }
    
    /**
     * Sets the providers current dimension ID, used in default getSaveFolder()
     * Added to allow default providers to be registered for multiple dimensions.
     * 
     * @param dim Dimension ID
     */
    @Override
    public void setDimension(int dim)
    {
        this.dimensionId = dim;
    }

    /**
     * Returns the sub-folder of the world folder that this WorldProvider saves to.
     * EXA: DIM1, DIM-1
     * @return The sub-folder name to save this world's chunks to.
     */
    @Override
    public String getSaveFolder()
    {
        return parProvider.getSaveFolder();
    }

    /**
     * A message to display to the user when they transfer to this dimension.
     *
     * @return The message to be displayed
     */
    @Override
    public String getWelcomeMessage()
    {
        return parProvider.getWelcomeMessage();
    }

    /**
     * A Message to display to the user when they transfer out of this dismension.
     *
     * @return The message to be displayed
     */
    @Override
    public String getDepartMessage()
    {
        return parProvider.getDepartMessage();
    }

    /**
     * The dimensions movement factor. Relative to normal overworld.
     * It is applied to the players position when they transfer dimensions.
     * Exa: Nether movement is 8.0
     * @return The movement factor
     */
    @Override
    public double getMovementFactor()
    {
        return parProvider.getMovementFactor();
    }

    @Override
    public ChunkCoordinates getRandomizedSpawnPoint()
    {
        return parProvider.getRandomizedSpawnPoint();
    }
    
    /**
     * Determine if the cusor on the map should 'spin' when rendered, like it does for the player in the nether.
     * 
     * @param entity The entity holding the map, playername, or frame-ENTITYID
     * @param x X Position
     * @param y Y Position
     * @param z Z Postion
     * @return True to 'spin' the cursor
     */
    @Override
    public boolean shouldMapSpin(String entity, double x, double y, double z)
    {
        return parProvider.shouldMapSpin(entity, x, y, z);
    }

    /**
     * Determines the dimension the player will be respawned in, typically this brings them back to the overworld.
     * 
     * @param player The player that is respawning
     * @return The dimension to respawn the player in
     */
    @Override
    public int getRespawnDimension(EntityPlayerMP player)
    {
        return parProvider.getRespawnDimension(player);
    }

    /*======================================= Start Moved From World =========================================*/
    
    @Override
    public BiomeGenBase getBiomeGenForCoords(int x, int z)
    {
        return parProvider.getBiomeGenForCoords(x, z);
    }

    @Override
    public boolean isDaytime()
    {
        return parProvider.isDaytime();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Vec3 getSkyColor(Entity cameraEntity, float partialTicks)
    {
        float f2 = this.calculateSunHeight(worldObj.getWorldTime(), partialTicks) * 2.0F + 0.5F;

        if (f2 < 0.0F)
        {
            f2 = 0.0F;
        }

        if (f2 > 1.0F)
        {
            f2 = 1.0F;
        }

        int i = MathHelper.floor_double(cameraEntity.posX);
        int j = MathHelper.floor_double(cameraEntity.posY);
        int k = MathHelper.floor_double(cameraEntity.posZ);
        int l = ForgeHooksClient.getSkyBlendColour(this.worldObj, i, j, k);
        float f4 = (float)(l >> 16 & 255) / 255.0F;
        float f5 = (float)(l >> 8 & 255) / 255.0F;
        float f6 = (float)(l & 255) / 255.0F;
        f4 *= f2;
        f5 *= f2;
        f6 *= f2;
        float f7 = worldObj.getRainStrength(partialTicks);
        float f8;
        float f9;

        if (f7 > 0.0F)
        {
            f8 = (f4 * 0.3F + f5 * 0.59F + f6 * 0.11F) * 0.6F;
            f9 = 1.0F - f7 * 0.75F;
            f4 = f4 * f9 + f8 * (1.0F - f9);
            f5 = f5 * f9 + f8 * (1.0F - f9);
            f6 = f6 * f9 + f8 * (1.0F - f9);
        }

        f8 = worldObj.getWeightedThunderStrength(partialTicks);

        if (f8 > 0.0F)
        {
            f9 = (f4 * 0.3F + f5 * 0.59F + f6 * 0.11F) * 0.2F;
            float f10 = 1.0F - f8 * 0.75F;
            f4 = f4 * f10 + f9 * (1.0F - f10);
            f5 = f5 * f10 + f9 * (1.0F - f10);
            f6 = f6 * f10 + f9 * (1.0F - f10);
        }

        if (worldObj.lastLightningBolt > 0)
        {
            f9 = (float)worldObj.lastLightningBolt - partialTicks;

            if (f9 > 1.0F)
            {
                f9 = 1.0F;
            }

            f9 *= 0.45F;
            f4 = f4 * (1.0F - f9) + 0.8F * f9;
            f5 = f5 * (1.0F - f9) + 0.8F * f9;
            f6 = f6 * (1.0F - f9) + 1.0F * f9;
        }

        return Vec3.createVectorHelper((double)f4, (double)f5, (double)f6);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Vec3 drawClouds(float partialTicks)
    {
        float f2 = this.calculateSunHeight(worldObj.getWorldTime(), partialTicks) * 2.0F + 0.5F;

        if (f2 < 0.0F)
        {
            f2 = 0.0F;
        }

        if (f2 > 1.0F)
        {
            f2 = 1.0F;
        }

        float f3 = (float)(this.cloudColour >> 16 & 255L) / 255.0F;
        float f4 = (float)(this.cloudColour >> 8 & 255L) / 255.0F;
        float f5 = (float)(this.cloudColour & 255L) / 255.0F;
        float f6 = worldObj.getRainStrength(partialTicks);
        float f7;
        float f8;

        if (f6 > 0.0F)
        {
            f7 = (f3 * 0.3F + f4 * 0.59F + f5 * 0.11F) * 0.6F;
            f8 = 1.0F - f6 * 0.95F;
            f3 = f3 * f8 + f7 * (1.0F - f8);
            f4 = f4 * f8 + f7 * (1.0F - f8);
            f5 = f5 * f8 + f7 * (1.0F - f8);
        }

        f3 *= f2 * 0.9F + 0.1F;
        f4 *= f2 * 0.9F + 0.1F;
        f5 *= f2 * 0.85F + 0.15F;
        f7 = worldObj.getWeightedThunderStrength(partialTicks);

        if (f7 > 0.0F)
        {
            f8 = (f3 * 0.3F + f4 * 0.59F + f5 * 0.11F) * 0.2F;
            float f9 = 1.0F - f7 * 0.95F;
            f3 = f3 * f9 + f8 * (1.0F - f9);
            f4 = f4 * f9 + f8 * (1.0F - f9);
            f5 = f5 * f9 + f8 * (1.0F - f9);
        }

        return Vec3.createVectorHelper((double)f3, (double)f4, (double)f5);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getStarBrightness(float par1)
    {
        float f2 = 1.0F - (this.calculateSunHeight(worldObj.getWorldTime(), par1) * 2.0F + 0.25F);

        if (f2 < 0.0F)
        {
            f2 = 0.0F;
        }

        if (f2 > 1.0F)
        {
            f2 = 1.0F;
        }

        return f2 * f2 * 0.5F;
    }

    @Override
    public void setAllowedSpawnTypes(boolean allowHostile, boolean allowPeaceful)
    {
    	parProvider.setAllowedSpawnTypes(allowHostile, allowPeaceful);
    }

    @Override
    public void calculateInitialWeather()
    {
    	parProvider.calculateInitialWeather();
    }

    @Override
    public void updateWeather()
    {
    	parProvider.updateWeather();
    }

    @Override
    public boolean canBlockFreeze(int x, int y, int z, boolean byWater)
    {
        return parProvider.canBlockFreeze(x, y, z, byWater);
    }

    @Override
    public boolean canSnowAt(int x, int y, int z, boolean checkLight)
    {
        return parProvider.canSnowAt(x, y, z, checkLight);
    }

    @Override
    public void setWorldTime(long time)
    {
    	parProvider.setWorldTime(time);
    }

    @Override
    public long getSeed()
    {
    	return parProvider.getSeed();
    }

    @Override
    public long getWorldTime()
    {
        return parProvider.getWorldTime();
    }

    @Override
    public ChunkCoordinates getSpawnPoint()
    {
        return parProvider.getSpawnPoint();
    }

    @Override
    public void setSpawnPoint(int x, int y, int z)
    {
    	parProvider.setSpawnPoint(x, y, z);
    }

    @Override
    public boolean canMineBlock(EntityPlayer player, int x, int y, int z)
    {
    	return parProvider.canMineBlock(player, x, y, z);
    }

    @Override
    public boolean isBlockHighHumidity(int x, int y, int z)
    {
        return parProvider.isBlockHighHumidity(x, y, z);
    }

    @Override
    public int getHeight()
    {
        return parProvider.getHeight();
    }

    @Override
    public int getActualHeight()
    {
        return parProvider.getActualHeight();
    }

    @Override
    public double getHorizon()
    {
        return parProvider.getHorizon();
    }

    @Override
    public void resetRainAndThunder()
    {
    	parProvider.resetRainAndThunder();
    }

    @Override
    public boolean canDoLightning(Chunk chunk)
    {
    	return parProvider.canDoLightning(chunk);
    }

    @Override
    public boolean canDoRainSnowIce(Chunk chunk)
    {
    	return parProvider.canDoRainSnowIce(chunk);
    }

}
