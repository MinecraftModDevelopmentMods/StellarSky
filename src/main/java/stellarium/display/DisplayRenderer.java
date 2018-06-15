package stellarium.display;

import stellarium.render.SkyRI;

public enum DisplayRenderer {
	INSTANCE;

	public void render(DisplayModel model, boolean isPostCelestials, SkyRI info) {
		DisplayRenderInfo subInfo = new DisplayRenderInfo(info.minecraft, info.tessellator, info.worldRenderer, info.partialTicks, isPostCelestials);
		for(DisplayModel.Delegate delegate : model.displayList) {
			delegate.renderer.render(subInfo, delegate.cache);
		}
	}
}
	