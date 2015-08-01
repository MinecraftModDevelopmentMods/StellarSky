package stellarium.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EVector;
import sciapi.api.value.euclidian.EVectorSet;
import sciapi.api.value.util.COp;
import stellarium.StellarSky;
import stellarium.stellars.ExtinctionRefraction;
import stellarium.stellars.StellarManager;
import stellarium.util.math.Spmath;
import stellarium.util.math.VecMath;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.common.DimensionManager;

public class StellarWorldProvider extends WorldProvider {
	
	public static WorldProvider[] preProviders = new WorldProvider[2];
	private WorldProvider parProvider;
	
	@Override
    public float calculateCelestialAngle(long par1, float par3)
    {
    	if(StellarSky.getManager().Earth.EcRPos == null)
    		StellarSky.getManager().Update(par1+par3, isSurfaceWorld());
    	
    	IValRef<EVector> sun = EVectorSet.ins(3).getSTemp();
    	
    	sun.set(StellarSky.getManager().Sun.GetPosition());
    	sun.set(ExtinctionRefraction.Refraction(sun, true));
    	sun.set(VecMath.normalize(sun));
    	
    	double h=Math.asin(VecMath.getZ(sun));
    	
    	if(VecMath.getCoord(sun, 0).asDouble()<0) h=Math.PI-h;
    	if(VecMath.getCoord(sun, 0).asDouble()>0 && h<0) h=h+2*Math.PI;
    	
    	sun.onUsed();
    	
    	return (float)(Spmath.fmod((h/2/Math.PI)+0.75,2*Math.PI));
    }

	@Override
    public int getMoonPhase(long par1)
    {
    	if(StellarSky.getManager().Earth.EcRPos==null)
    		StellarSky.getManager().Update(par1, isSurfaceWorld());
    	return (int)(StellarSky.getManager().Moon.Phase_Time()*8);
    }
	
	
    /**
     * creates a new world chunk manager for WorldProvider
     */
	@Override
    protected void registerWorldChunkManager()
    {
		this.parProvider = worldObj.isRemote? preProviders[0] : preProviders[1];
		
		parProvider.setDimension(this.dimensionId);
		
        parProvider.registerWorld(this.worldObj);
        this.worldChunkMgr = parProvider.worldChunkMgr;
    }

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
        return parProvider.calcSunriseSunsetColors(p_76560_1_, p_76560_2_);
    }

    /**
     * Return Vec3D with biome specific fog color
     */
    @SideOnly(Side.CLIENT)
    @Override
    public Vec3 getFogColor(float p_76562_1_, float p_76562_2_)
    {
        return parProvider.getFogColor(p_76562_1_, p_76562_2_);
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
        return parProvider.getSkyColor(cameraEntity, partialTicks);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Vec3 drawClouds(float partialTicks)
    {
        return parProvider.drawClouds(partialTicks);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getStarBrightness(float par1)
    {
        return parProvider.getStarBrightness(par1);
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
