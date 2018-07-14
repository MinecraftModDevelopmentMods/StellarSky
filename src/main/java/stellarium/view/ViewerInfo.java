package stellarium.view;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import stellarapi.api.event.RenderQEEvent;
import stellarapi.api.lib.math.Vector3;
import stellarapi.api.optics.EnumRGBA;
import stellarapi.api.optics.Wavelength;
import stellarapi.api.view.IAtmosphereEffect;
import stellarapi.api.view.ICCoordinates;
import stellarium.util.MCUtil;

// TODO AA This needs to be reformed
public class ViewerInfo {
	public final Vector3 currentPosition;

	public final ICCoordinates coordinate;
	public final IAtmosphereEffect sky;

	public final double multiplyingPower;

	public final Vector3 colorMultiplier = new Vector3();

	private static float getFilterQE(EntityRenderer renderer, Entity entity, IBlockState state, double renderPartialTicks, Wavelength wavelengthIn, float initialQE) {
		RenderQEEvent event = new RenderQEEvent(renderer, entity, state, renderPartialTicks, wavelengthIn, initialQE);
		MinecraftForge.EVENT_BUS.post(event);
		return event.getQE();
	}

	public ViewerInfo(ICCoordinates coordinate, IAtmosphereEffect sky, Entity viewer, float partialTicks) {
		this.coordinate = coordinate;
		this.sky = sky;

		this.currentPosition = new Vector3(viewer.posX, viewer.posY, viewer.posZ);

		Minecraft minecraft = Minecraft.getMinecraft();
		EntityRenderer renderer = minecraft.entityRenderer;
		float fov = MCUtil.getFOVModifier(renderer, partialTicks, true);
		this.multiplyingPower = 70.0 / fov;

		minecraft.gameSettings.smoothCamera = false;
		// TODO Stellar API Migrate this to Stellar API
		if(this.multiplyingPower < 8.0) {
			minecraft.gameSettings.mouseSensitivity = 0.5f;
		} else {
			minecraft.gameSettings.mouseSensitivity = 0.2f;
		}

        IBlockState state = ActiveRenderInfo.getBlockStateAtEntityViewpoint(minecraft.world, viewer, partialTicks);

		float[] eff = new float[3];
		for(EnumRGBA color : EnumRGBA.RGB)
			eff[color.ordinal()] = getFilterQE(renderer, viewer, state, partialTicks, Wavelength.colorWaveMap.get(color), 1.0f);

		colorMultiplier.set(eff[0], eff[1], eff[2]);
	}

	public float getHeight(World world) {
		return (float)((currentPosition.getY() - world.getHorizon()) / world.getHeight());
	}

}
