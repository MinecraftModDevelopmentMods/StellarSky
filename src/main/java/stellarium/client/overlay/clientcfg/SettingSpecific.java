package stellarium.client.overlay.clientcfg;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import stellarapi.lib.gui.IRectangleBound;
import stellarapi.lib.gui.IRenderModel;
import stellarapi.lib.gui.model.basic.ModelSimpleTextured;
import stellarium.StellarSkyResources;
import stellarium.client.overlay.clientcfg.model.ModelMain;

public class SettingSpecific {

	public static ModelMain generateModel() {
		ModelMain model = new ModelMain();
		
		model.addSubModel("config.category.milkyway", new ModelSimpleTextured(StellarSkyResources.milkywaycategory));
		model.addSubModel("config.property.client.milkywayfrac", new ModelSimpleTextured(StellarSkyResources.milkywayfrag));
		model.addSubModel("config.property.client.milkywaybrightness", new ModelSimpleTextured(StellarSkyResources.milkywaybrightness));

		
		model.addSubModel("config.category.solarsystem", new ModelSimpleTextured(StellarSkyResources.solarsystem));
		model.addSubModel("config.property.client.moonfrac", new ModelSimpleTextured(StellarSkyResources.moonfrag));
		
		
		model.addSubModel("config.category.display", new ModelSimpleTextured(StellarSkyResources.display));
		
		model.addSubModel("config.category.display.horcoord", new ModelOnOff(StellarSkyResources.horizontal));
		model.addSubModel("config.category.display.eqcoord", new ModelOnOff(StellarSkyResources.equatorial));
		model.addSubModel("config.category.display.eccoord", new ModelOnOff(StellarSkyResources.ecliptic));

		model.addSubModel("config.property.display.alpha", new ModelSimpleTextured(StellarSkyResources.alpha));
		model.addSubModel("config.property.display.horcoord.fragments", new ModelSimpleTextured(StellarSkyResources.fragment));
		model.addSubModel("config.property.display.eqcoord.fragments", new ModelSimpleTextured(StellarSkyResources.fragment));
		model.addSubModel("config.property.display.eccoord.fragments", new ModelSimpleTextured(StellarSkyResources.fragment));

		model.addSubModel("config.property.display.horcoord.horizon.displayed", new ModelOnOff(StellarSkyResources.horizon));
		model.addSubModel("config.property.display.eqcoord.equator.displayed", new ModelOnOff(StellarSkyResources.equator));
		model.addSubModel("config.property.display.eccoord.ecliptic.displayed", new ModelOnOff(StellarSkyResources.eclipticline));
		model.addSubModel("config.property.display.horcoord.grid.displayed", new ModelOnOff(StellarSkyResources.horizontalgrid));
		model.addSubModel("config.property.display.eqcoord.grid.displayed", new ModelOnOff(StellarSkyResources.equatorialgrid));
		model.addSubModel("config.property.display.eccoord.grid.displayed", new ModelOnOff(StellarSkyResources.eclipticgrid));
		
		model.addSubModel("config.category.landscape", new ModelSimpleTextured(StellarSkyResources.landscape));
		model.addSubModel("config.property.landscape.fragments", new ModelSimpleTextured(StellarSkyResources.fillfragment));

		model.addSubModel("config.category.optics", new ModelSimpleTextured(StellarSkyResources.optics));
		model.addSubModel("config.property.client.turbulance", new ModelSimpleTextured(StellarSkyResources.turbulance));
		model.addSubModel("config.property.client.brcontrast", new ModelSimpleTextured(StellarSkyResources.brcontrast));

		return model;
	}
	
	public static class ModelOnOff implements IRenderModel {
		
		private ModelSimpleTextured model;
		
		public ModelOnOff(ResourceLocation location) {
			this.model = new ModelSimpleTextured(location);
		}
		
		@Override
		public void renderModel(String info, IRectangleBound totalBound, IRectangleBound clipBound,
				Tessellator tessellator, VertexBuffer worldRenderer, TextureManager textureManager, float[] colors) {
			if(info.equals("true")) {
				model.renderModel(info, totalBound, clipBound, tessellator, worldRenderer, textureManager, colors);
			} else {
				colors[0] *= 0.7f;
				colors[1] *= 0.7f;
				colors[2] *= 0.7f;
				model.renderModel(info, totalBound, clipBound, tessellator, worldRenderer, textureManager, colors);
			}
		}
	}
	
	public static class ModelTF implements IRenderModel {
		
		private ModelSimpleTextured trueModel;
		private ModelSimpleTextured falseModel;
		
		public ModelTF(ResourceLocation onTrue, ResourceLocation onFalse) {
			this.trueModel = new ModelSimpleTextured(onTrue);
			this.falseModel = new ModelSimpleTextured(onFalse);
		}
		
		@Override
		public void renderModel(String info, IRectangleBound totalBound, IRectangleBound clipBound,
				Tessellator tessellator, VertexBuffer worldRenderer, TextureManager textureManager, float[] colors) {
			if(info.equals("true")) {
				trueModel.renderModel(info, totalBound, clipBound, tessellator, worldRenderer, textureManager, colors);
			} else {
				falseModel.renderModel(info, totalBound, clipBound, tessellator, worldRenderer, textureManager, colors);
			}
		}
	}

}
