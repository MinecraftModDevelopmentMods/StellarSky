package stellarium.render.stellars.phased;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import stellarium.render.shader.IShaderObject;
import stellarium.render.sky.SkyRenderInformation;
import stellarium.view.ViewerInfo;

public class StellarRenderInformation {
	public final Minecraft minecraft;
	public final WorldClient world;
	public final float partialTicks;
	public final boolean isFrameBufferEnabled;
	public final double deepDepth;

	public final ViewerInfo info;
	public final double screenSize;

	public StellarRenderInformation(SkyRenderInformation info) {
		this.minecraft = info.minecraft;
		this.world = info.world;
		this.partialTicks = info.partialTicks;
		this.isFrameBufferEnabled = info.isFrameBufferEnabled;
		this.deepDepth = info.deepDepth;
		
		this.info = info.info;
		this.screenSize = info.screenSize;
	}
	
	private IShaderObject activeShader;
	private int callList;
	
	public void setActiveShader(IShaderObject activeShader) {
		this.activeShader = activeShader;
	}
		
	public IShaderObject getActiveShader() {
		return this.activeShader;
	}
	
	public int getAtmCallList() {
		return this.callList;
	}

	public void setAtmCallList(int list) {
		this.callList = list;
	}
}
