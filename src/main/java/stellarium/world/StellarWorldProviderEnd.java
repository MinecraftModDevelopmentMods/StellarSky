package stellarium.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.end.DragonFightManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarium.api.ISkyProvider;
import stellarium.api.IStellarWorldProvider;
import stellarium.render.SkyRenderer;

public class StellarWorldProviderEnd extends WorldProviderEnd implements IStellarWorldProvider {

	private WorldProviderEnd parProvider;
	private ISkyProvider skyProvider;
	
	private final float[] colorsSunriseSunset = new float[4];
		
	private long cloudColour = 16777215L;
	
	public StellarWorldProviderEnd(World world, WorldProviderEnd provider) {
		this.parProvider = provider;
		this.worldObj = world;
	}
	
	@Override
	public void setSkyProvider(ISkyProvider skyProvider) {
		this.skyProvider = skyProvider;
	}
	
	@Override
	public ISkyProvider getSkyProvider() {
		return this.skyProvider;
	}

	@Override
    public float calculateCelestialAngle(long worldTime, float partialTicks) {
		return skyProvider.calculateCelestialAngle(worldTime, partialTicks);
    }
	
	public float calculateSunHeight(float partialTicks) {
    	return skyProvider.calculateSunHeight(partialTicks);
	}
	
	public float calculateSunlightFactor(float partialTicks) {
		return skyProvider.calculateSunlightFactor(partialTicks);
	}
    
    @Override
    @SideOnly(Side.CLIENT)
    public float getSunBrightness(float par1)
    {
        float f1 = 1.0F - (this.calculateSunlightFactor(par1) - 0.3F);
        f1 = MathHelper.clamp_float(f1, 0.0F, 1.0F);
        f1 = 1.0F - f1;
        f1 = (float)((double)f1 * (1.0D - (double)(worldObj.getRainStrength(par1) * 5.0F) / 16.0D));
        f1 = (float)((double)f1 * (1.0D - (double)(worldObj.getThunderStrength(par1) * 5.0F) / 16.0D));
        return f1 * 0.8F + 0.2F;
    }
	
	@Override
	public float getSunBrightnessFactor(float par1) {
        float f1 = 1.0F - (this.calculateSunlightFactor(par1));
        f1 = MathHelper.clamp_float(f1, 0.0F, 1.0F);
        f1 = 1.0F - f1;
        f1 = (float)((double)f1 * (1.0D - (double)(worldObj.getRainStrength(par1) * 5.0F) / 16.0D));
        f1 = (float)((double)f1 * (1.0D - (double)(worldObj.getThunderStrength(par1) * 5.0F) / 16.0D));
        return f1;
	}

	@Override
    public int getMoonPhase(long worldTime) {
    	return skyProvider.getCurrentMoonPhase(worldTime);
    }
	
	@Override
	public float getCurrentMoonPhaseFactor() {
    	return skyProvider.getCurrentMoonPhaseFactor();
	}

    /**
     * Returns a new chunk provider which generates chunks for this world
     */
	@Override
    public IChunkGenerator createChunkGenerator()
    {
        return parProvider.createChunkGenerator();
    }

    /**
     * Will check if the x, z position specified is alright to be set as the map spawn point
     */
	@Override
    public boolean canCoordinateBeSpawn(int x, int z)
    {
        return parProvider.canCoordinateBeSpawn(x, z);
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
        return null;
    }

    /**
     * Return Vec3dD with biome specific fog color
     */
    @SideOnly(Side.CLIENT)
    @Override
    public Vec3d getFogColor(float p_76562_1_, float p_76562_2_)
    {
    	int i = 10518688;
        float f = this.calculateSunlightFactor(p_76562_2_);
        f = MathHelper.clamp_float(f, 0.0F, 1.0F);
        float f1 = (float)(i >> 16 & 255) / 255.0F;
        float f2 = (float)(i >> 8 & 255) / 255.0F;
        float f3 = (float)(i & 255) / 255.0F;
        f1 = f1 * (f * 0.0F + 0.15F);
        f2 = f2 * (f * 0.0F + 0.15F);
        f3 = f3 * (f * 0.0F + 0.15F);
        return new Vec3d((double)f1, (double)f2, (double)f3);
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
    public BlockPos getSpawnCoordinate()
    {
        return parProvider.getSpawnCoordinate();
    }

    @Override
    public int getAverageGroundLevel()
    {
        return parProvider.getAverageGroundLevel();
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
    
    public BiomeProvider getBiomeProvider()
    {
        return parProvider.getBiomeProvider();
    }
    
    @Override
    public boolean doesWaterVaporize()
    {
        return parProvider.doesWaterVaporize();
    }
    
    @Override
    public boolean getHasNoSky()
    {
        return parProvider.getHasNoSky();
    }
    
    @Override
    public float[] getLightBrightnessTable()
    {
        return parProvider.getLightBrightnessTable();
    }
    
    @Override
    public WorldBorder getWorldBorder()
    {
        return parProvider.getWorldBorder();
    }
    
    /*======================================= Forge Start =========================================*/
    
    /**
     * Sets the providers current dimension ID, used in default getSaveFolder()
     * Added to allow default providers to be registered for multiple dimensions.
     * 
     * @param dim Dimension ID
     */
    @Override
    public void setDimension(int dim)
    {
       parProvider.setDimension(dim);
    }
    public int getDimension()
    {
        return parProvider.getDimension();
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
    @SideOnly(Side.CLIENT)
    public void setSkyRenderer(net.minecraftforge.client.IRenderHandler skyRenderer)
    {
        if(skyRenderer instanceof SkyRenderer)
        	super.setSkyRenderer(skyRenderer);
    }

    @Override
    public BlockPos getRandomizedSpawnPoint()
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
    public BiomeGenBase getBiomeGenForCoords(BlockPos pos)
    {
        return parProvider.getBiomeGenForCoords(pos);
    }

    @Override
    public boolean isDaytime()
    {
        return parProvider.isDaytime();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Vec3d getSkyColor(Entity cameraEntity, float partialTicks)
    {
        float f1 = this.calculateSunlightFactor(partialTicks);
        f1 = MathHelper.clamp_float(f1, 0.0F, 1.0F);
        int i = MathHelper.floor_double(cameraEntity.posX);
        int j = MathHelper.floor_double(cameraEntity.posY);
        int k = MathHelper.floor_double(cameraEntity.posZ);
        BlockPos blockpos = new BlockPos(i, j, k);
        int l = net.minecraftforge.client.ForgeHooksClient.getSkyBlendColour(this.worldObj, blockpos);
        float f3 = (float)(l >> 16 & 255) / 255.0F;
        float f4 = (float)(l >> 8 & 255) / 255.0F;
        float f5 = (float)(l & 255) / 255.0F;
        f3 = f3 * f1;
        f4 = f4 * f1;
        f5 = f5 * f1;
        float f6 = worldObj.getRainStrength(partialTicks);

        if (f6 > 0.0F)
        {
            float f7 = (f3 * 0.3F + f4 * 0.59F + f5 * 0.11F) * 0.6F;
            float f8 = 1.0F - f6 * 0.75F;
            f3 = f3 * f8 + f7 * (1.0F - f8);
            f4 = f4 * f8 + f7 * (1.0F - f8);
            f5 = f5 * f8 + f7 * (1.0F - f8);
        }

        float f10 = worldObj.getThunderStrength(partialTicks);

        if (f10 > 0.0F)
        {
            float f11 = (f3 * 0.3F + f4 * 0.59F + f5 * 0.11F) * 0.2F;
            float f9 = 1.0F - f10 * 0.75F;
            f3 = f3 * f9 + f11 * (1.0F - f9);
            f4 = f4 * f9 + f11 * (1.0F - f9);
            f5 = f5 * f9 + f11 * (1.0F - f9);
        }

        if (worldObj.getLastLightningBolt() > 0)
        {
            float f12 = (float)worldObj.getLastLightningBolt() - partialTicks;

            if (f12 > 1.0F)
            {
                f12 = 1.0F;
            }

            f12 = f12 * 0.45F;
            f3 = f3 * (1.0F - f12) + 0.8F * f12;
            f4 = f4 * (1.0F - f12) + 0.8F * f12;
            f5 = f5 * (1.0F - f12) + 1.0F * f12;
        }

        return new Vec3d((double)f3, (double)f4, (double)f5);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Vec3d getCloudColor(float partialTicks)
    {
        float f1 = this.calculateSunlightFactor(partialTicks);
        f1 = MathHelper.clamp_float(f1, 0.0F, 1.0F);
        float f2 = (float)(this.cloudColour >> 16 & 255L) / 255.0F;
        float f3 = (float)(this.cloudColour >> 8 & 255L) / 255.0F;
        float f4 = (float)(this.cloudColour & 255L) / 255.0F;
        float f5 = worldObj.getRainStrength(partialTicks);

        if (f5 > 0.0F)
        {
            float f6 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.6F;
            float f7 = 1.0F - f5 * 0.95F;
            f2 = f2 * f7 + f6 * (1.0F - f7);
            f3 = f3 * f7 + f6 * (1.0F - f7);
            f4 = f4 * f7 + f6 * (1.0F - f7);
        }

        f2 = f2 * (f1 * 0.9F + 0.1F);
        f3 = f3 * (f1 * 0.9F + 0.1F);
        f4 = f4 * (f1 * 0.85F + 0.15F);
        float f9 = worldObj.getThunderStrength(partialTicks);

        if (f9 > 0.0F)
        {
            float f10 = (f2 * 0.3F + f3 * 0.59F + f4 * 0.11F) * 0.2F;
            float f8 = 1.0F - f9 * 0.95F;
            f2 = f2 * f8 + f10 * (1.0F - f8);
            f3 = f3 * f8 + f10 * (1.0F - f8);
            f4 = f4 * f8 + f10 * (1.0F - f8);
        }
        
        return new Vec3d((double)f2, (double)f3, (double)f4);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getStarBrightness(float par1)
    {
        float f1 = 1.0F - (this.calculateSunlightFactor(par1) - 0.25F);
        f1 = MathHelper.clamp_float(f1, 0.0F, 1.0F);
        return f1 * f1 * 0.5F;
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
    public boolean canBlockFreeze(BlockPos pos, boolean byWater)
    {
        return parProvider.canBlockFreeze(pos, byWater);
    }

    @Override
    public boolean canSnowAt(BlockPos pos, boolean checkLight)
    {
        return parProvider.canSnowAt(pos, checkLight);
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
    public BlockPos getSpawnPoint()
    {
        return parProvider.getSpawnPoint();
    }

    @Override
    public void setSpawnPoint(BlockPos pos)
    {
    	parProvider.setSpawnPoint(pos);
    }

    @Override
    public boolean canMineBlock(EntityPlayer player, BlockPos pos)
    {
    	return parProvider.canMineBlock(player, pos);
    }

    @Override
    public boolean isBlockHighHumidity(BlockPos pos)
    {
        return parProvider.isBlockHighHumidity(pos);
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
    
    /**
     * Called when a Player is added to the provider's world.
     */
    @Override
    public void onPlayerAdded(EntityPlayerMP p_186061_1_)
    {
    	parProvider.onPlayerAdded(p_186061_1_);
    }

    /**
     * Called when a Player is removed from the provider's world.
     */
    @Override
    public void onPlayerRemoved(EntityPlayerMP p_186062_1_)
    {
    	parProvider.onPlayerRemoved(p_186062_1_);
    }

    @Override
    public DimensionType getDimensionType() {
    	return parProvider.getDimensionType();
    }

    /**
     * Called when the world is performing a save. Only used to save the state of the Dragon Boss fight in
     * WorldProviderEnd in Vanilla.
     */
    @Override
    public void onWorldSave()
    {
    	parProvider.onWorldSave();
    }

    /**
     * Called when the world is updating entities. Only used in WorldProviderEnd to update the DragonFightManager in
     * Vanilla.
     */
    @Override
    public void onWorldUpdateEntities()
    {
    	parProvider.onWorldUpdateEntities();
    }

    /**
     * Called to determine if the chunk at the given chunk coordinates within the provider's world can be dropped. Used
     * in WorldProviderSurface to prevent spawn chunks from being unloaded.
     */
    @Override
    public boolean canDropChunk(int x, int z)
    {
        return parProvider.canDropChunk(x, z);
    }
    
    
    /**
     * Used for fixing dragon.
     * */
    @Override
    public DragonFightManager getDragonFightManager()
    {
        return parProvider.getDragonFightManager();
    }
	
}
