package stellarium.world;

import stellarium.StellarSky;
import net.minecraft.entity.Entity;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.ISaveHandler;

public class StellarWorld extends World {

	public StellarWorld(ISaveHandler p_i45368_1_, String p_i45368_2_,
			WorldProvider p_i45368_3_, WorldSettings p_i45368_4_,
			Profiler p_i45368_5_) {
		super(p_i45368_1_, p_i45368_2_, p_i45368_3_, p_i45368_4_, p_i45368_5_);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	  public float getCurrentMoonPhaseFactor()
    {
        return provider.getCurrentMoonPhaseFactor();
    }

	@Override
    public float getCurrentMoonPhaseFactorBody()
    {
        return StellarWorldProvider.moonPhaseFactors[this.provider.getMoonPhase(this.worldInfo.getWorldTime())];
    }
	
	
	@Override
	protected IChunkProvider createChunkProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected int func_152379_p() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Entity getEntityByID(int p_73045_1_) {
		// TODO Auto-generated method stub
		return null;
	}

}
