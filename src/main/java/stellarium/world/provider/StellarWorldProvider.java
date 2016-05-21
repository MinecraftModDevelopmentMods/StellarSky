package stellarium.world.provider;

import java.lang.reflect.Field;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stellarapi.api.optics.EnumRGBA;
import stellarium.api.ICelestialHelper;
import stellarium.api.ICelestialRenderer;

public class StellarWorldProvider extends WorldProvider {
	
	private WorldProvider parProvider;
	private ICelestialHelper celestialHelper;
	
	private final float[] colorsSunriseSunset = new float[4];
		
	private long cloudColour = 16777215L;
	
	public StellarWorldProvider(World world, WorldProvider provider, ICelestialHelper celestialHelper) {
		this.parProvider = provider;
		this.worldObj = world;
		this.celestialHelper = celestialHelper;
	}
	
	@Override
    public float calculateCelestialAngle(long worldTime, float partialTicks) {
		return celestialHelper.calculateCelestialAngle(worldTime, partialTicks);
    }
	
	public float getSunHeight(float partialTicks) {
    	return celestialHelper.getSunHeightFactor(partialTicks);
	}
    
    @Override
    @SideOnly(Side.CLIENT)
    public float getSunBrightness(float par1) {
        float f2 = (celestialHelper.getSunlightRenderBrightnessFactor(par1));
        f2 *= celestialHelper.getSkyTransmissionFactor(par1);
        f2 = (float)((double)f2 * (1.0D - (double)(worldObj.getRainStrength(par1) * 5.0F) / 16.0D));
        f2 = (float)((double)f2 * (1.0D - (double)(worldObj.getThunderStrength(par1) * 5.0F) / 16.0D));
        return f2 + celestialHelper.minimumSkyRenderBrightness() * (1.0f - f2);
    }
	
	@Override
	public float getSunBrightnessFactor(float par1) {
        float f1 = celestialHelper.getSunlightFactor(EnumRGBA.Alpha, par1) * celestialHelper.getSkyTransmissionFactor(par1);
        f1 = (float)((double)f1 * (1.0D - (double)(worldObj.getRainStrength(par1) * 5.0F) / 16.0D));
        f1 = (float)((double)f1 * (1.0D - (double)(worldObj.getThunderStrength(par1) * 5.0F) / 16.0D));
        return f1;
	}
	
	/**
     * Returns array with sunrise/sunset colors
     */
    @SideOnly(Side.CLIENT)
    @Override
    public float[] calcSunriseSunsetColors(float p_76560_1_, float p_76560_2_)
    {
		float f2 = 0.4F;
		float f3 = this.getSunHeight(p_76560_2_);
		float f4 = -0.0F;

		if (f3 >= f4 - f2 && f3 <= f4 + f2) {
			float f5 = (f3 - f4) / f2 * 0.5F + 0.5F;
			float f6 = 1.0f - 3.0f * Math.abs(f3);
			f6 *= f6;
			this.colorsSunriseSunset[0] = f5 * 0.3F + 0.7F;
			this.colorsSunriseSunset[1] = f5 * f5 * 0.7F + 0.2F;
			this.colorsSunriseSunset[2] = f5 * f5 * 0.0F + 0.2F;
			this.colorsSunriseSunset[3] = f6;

			for (EnumRGBA color : EnumRGBA.RGB)
				this.colorsSunriseSunset[color.ordinal()] = this.colorsSunriseSunset[color.ordinal()]
						* celestialHelper.calculateSunriseSunsetFactor(color, p_76560_2_)
						* celestialHelper.getDispersionFactor(color, p_76560_2_);

			return this.colorsSunriseSunset;
		} else {
			return null;
		}
    }

    /**
     * Return Vec3D with biome specific fog color
     */
    @SideOnly(Side.CLIENT)
    @Override
    public Vec3d getFogColor(float p_76562_1_, float p_76562_2_)
    {
    	float f3 = 0.7529412F;
		float f4 = 0.84705883F;
		float f5 = 1.0F;
		f3 *= celestialHelper.getSunlightFactor(EnumRGBA.Red, p_76562_2_)
				* celestialHelper.getDispersionFactor(EnumRGBA.Red, p_76562_2_) * 0.94F + 0.06F;
		f4 *= celestialHelper.getSunlightFactor(EnumRGBA.Green, p_76562_2_)
				* celestialHelper.getDispersionFactor(EnumRGBA.Green, p_76562_2_) * 0.94F + 0.06F;
		f5 *= celestialHelper.getSunlightFactor(EnumRGBA.Blue, p_76562_2_)
				* celestialHelper.getDispersionFactor(EnumRGBA.Blue, p_76562_2_) * 0.91F + 0.09F;

		return new Vec3d((double) f3, (double) f4, (double) f5);
    }

	@Override
    public int getMoonPhase(long par1) {
    	return celestialHelper.getCurrentMoonPhase(par1);
    }
	
	@Override
	public float getCurrentMoonPhaseFactor() {
    	return celestialHelper.getCurrentMoonPhaseFactor();
	}

    @Override
    @SideOnly(Side.CLIENT)
    public Vec3d getSkyColor(Entity cameraEntity, float partialTicks)
    {
		int i = MathHelper.floor_double(cameraEntity.posX);
		int j = MathHelper.floor_double(cameraEntity.posY);
		int k = MathHelper.floor_double(cameraEntity.posZ);
		BlockPos blockpos = new BlockPos(i, j, k);

		float mixedBrightness = this.getMixedBrightnessOn(cameraEntity.posX, cameraEntity.posY, cameraEntity.posZ,
				i, j, k);

		int l = ForgeHooksClient.getSkyBlendColour(this.worldObj, blockpos);
		float f4 = (float) (l >> 16 & 255) / 255.0F * celestialHelper.getDispersionFactor(EnumRGBA.Red, partialTicks);
		float f5 = (float) (l >> 8 & 255) / 255.0F * celestialHelper.getDispersionFactor(EnumRGBA.Green, partialTicks);
		float f6 = (float) (l & 255) / 255.0F * celestialHelper.getDispersionFactor(EnumRGBA.Blue, partialTicks);

		f4 *= (celestialHelper.getSunlightFactor(EnumRGBA.Red, partialTicks)
				+ mixedBrightness * celestialHelper.getLightPollutionFactor(EnumRGBA.Red, partialTicks));
		f5 *= (celestialHelper.getSunlightFactor(EnumRGBA.Green, partialTicks)
				+ mixedBrightness * celestialHelper.getLightPollutionFactor(EnumRGBA.Green, partialTicks));
		f6 *= (celestialHelper.getSunlightFactor(EnumRGBA.Blue, partialTicks)
				+ mixedBrightness * celestialHelper.getLightPollutionFactor(EnumRGBA.Blue, partialTicks));

		float f7 = worldObj.getRainStrength(partialTicks);
		float f8;
		float f9;

		if (f7 > 0.0F) {
			f8 = (f4 * 0.3F + f5 * 0.59F + f6 * 0.11F) * 0.6F;
			f9 = 1.0F - f7 * 0.75F;
			f4 = f4 * f9 + f8 * (1.0F - f9);
			f5 = f5 * f9 + f8 * (1.0F - f9);
			f6 = f6 * f9 + f8 * (1.0F - f9);
		}

		f8 = worldObj.getThunderStrength(partialTicks);

		if (f8 > 0.0F) {
			f9 = (f4 * 0.3F + f5 * 0.59F + f6 * 0.11F) * 0.2F;
			float f10 = 1.0F - f8 * 0.75F;
			f4 = f4 * f10 + f9 * (1.0F - f10);
			f5 = f5 * f10 + f9 * (1.0F - f10);
			f6 = f6 * f10 + f9 * (1.0F - f10);
		}

		if (worldObj.getLastLightningBolt() > 0) {
			f9 = (float) worldObj.getLastLightningBolt() - partialTicks;

			if (f9 > 1.0F) {
				f9 = 1.0F;
			}

			f9 *= 0.45F;
			f4 = f4 * (1.0F - f9) + 0.8F * f9;
			f5 = f5 * (1.0F - f9) + 0.8F * f9;
			f6 = f6 * (1.0F - f9) + 1.0F * f9;
		}

		return new Vec3d((double) f4, (double) f5, (double) f6);
    }
    
    public float getMixedBrightnessOn(double posX, double posY, double posZ, int i, int j, int k) {
    	double f2 = 0;
        f2 += (posX-i)* this.getMixedBrightnessOnBlock(i+1, j, k);
        f2 += (i+1-posX)* this.getMixedBrightnessOnBlock(i, j, k);
        f2 += (posY-j)* this.getMixedBrightnessOnBlock(i, j+1, k);
        f2 += (j+1-posY)* this.getMixedBrightnessOnBlock(i, j, k);
        f2 += (posZ-k)* this.getMixedBrightnessOnBlock(i, j, k+1);
        f2 += (k+1-posZ)* this.getMixedBrightnessOnBlock(i, j, k);
        return (float)f2 * 0.33f;
    }
    
    public float getMixedBrightnessOnBlock(int i, int j, int k) {
    	BlockPos pos = new BlockPos(i, j, k);
    	IBlockState state = worldObj.getBlockState(pos);
    	return ((state.getBlock().getPackedLightmapCoords(state, this.worldObj, pos) & 0xff)>>4) * 0.005f;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Vec3d getCloudColor(float partialTicks)
    {
        float f3 = (float)(this.cloudColour >> 16 & 255L) / 255.0F;
        float f4 = (float)(this.cloudColour >> 8 & 255L) / 255.0F;
        float f5 = (float)(this.cloudColour & 255L) / 255.0F;
        float f6 = worldObj.getRainStrength(partialTicks);
        float f7;
        float f8;

        if (f6 > 0.0F)
        {
            f7 = (f3 * 0.3F + f4 * 0.59F + f5 * 0.11F) * 0.6F;
            f8 = 1.0F - f5 * 0.95F;
            f3 = f3 * f8 + f7 * (1.0F - f8);
            f4 = f4 * f8 + f7 * (1.0F - f8);
            f5 = f5 * f8 + f7 * (1.0F - f8);
        }

        f3 *= celestialHelper.getSunlightFactor(EnumRGBA.Red, partialTicks)
        		* celestialHelper.getDispersionFactor(EnumRGBA.Red, partialTicks) * 0.9F + 0.1F;
        f4 *= celestialHelper.getSunlightFactor(EnumRGBA.Green, partialTicks)
        		* celestialHelper.getDispersionFactor(EnumRGBA.Green, partialTicks) * 0.9F + 0.1F;
        f5 *= celestialHelper.getSunlightFactor(EnumRGBA.Blue, partialTicks)
        		* celestialHelper.getDispersionFactor(EnumRGBA.Blue, partialTicks) * 0.85F + 0.15F;
        f7 = worldObj.getThunderStrength(partialTicks);

        if (f7 > 0.0F)
        {
            float f10 = (f3 * 0.3F + f4 * 0.59F + f5 * 0.11F) * 0.2F;
            f8 = 1.0F - f7 * 0.95F;
            f3 = f3 * f8 + f10 * (1.0F - f8);
            f4 = f4 * f8 + f10 * (1.0F - f8);
            f5 = f5 * f8 + f10 * (1.0F - f8);
        }

        return new Vec3d((double)f3, (double)f4, (double)f5);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getStarBrightness(float par1)
    {
        float f2 = 1.0F - (celestialHelper.getSunlightRenderBrightnessFactor( par1)
        		* celestialHelper.getDispersionFactor(EnumRGBA.Alpha, par1));

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
    @SideOnly(Side.CLIENT)
    public void setSkyRenderer(net.minecraftforge.client.IRenderHandler skyRenderer)
    {
    	try {
        	for(Field field : skyRenderer.getClass().getDeclaredFields())
        		if(ICelestialRenderer.class.isAssignableFrom(field.getType()))
        			super.setSkyRenderer(skyRenderer);
    	} catch(Exception exc) { }
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
    public WorldBorder createWorldBorder()
    {
        return parProvider.createWorldBorder();
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
    public Biome getBiomeForCoords(BlockPos pos)
    {
        return parProvider.getBiomeForCoords(pos);
    }

    @Override
    public boolean isDaytime()
    {
        return parProvider.isDaytime();
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
}