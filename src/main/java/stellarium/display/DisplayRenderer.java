package stellarium.display;

import stellarium.client.ClientSettings;
import stellarium.display.DisplayModel.Delegate;
import stellarium.lib.render.IGenericRenderer;
import stellarium.render.sky.SkyRenderInformation;

public class DisplayRenderer implements IGenericRenderer<ClientSettings, Boolean, DisplayModel, SkyRenderInformation> {

	@Override
	public void initialize(ClientSettings settings) { }

	@Override
	public void preRender(ClientSettings settings, SkyRenderInformation info) { }

	@Override
	public void renderPass(DisplayModel model, Boolean pass, SkyRenderInformation info) {
		for(DisplayModel.Delegate delegate : model.displayList) {
			delegate.renderer.render(new DisplayRenderInfo(info.minecraft, info.tessellator, info.partialTicks, pass, info.deepDepth),
					delegate.cache);
		}
	}

	@Override
	public void postRender(ClientSettings settings, SkyRenderInformation info) { }

}
	