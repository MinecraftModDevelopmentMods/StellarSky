package stellarium.client.overlay.clientcfg;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import stellarapi.lib.gui.GuiElement;
import stellarapi.lib.gui.IRenderer;
import stellarapi.lib.gui.model.basic.ModelSimpleRect;
import stellarapi.lib.gui.simple.GuiSimpleRenderElement;
import stellarapi.lib.gui.simple.ISimpleRenderController;
import stellarium.client.lib.gui.HierarchyUtil;
import stellarium.client.lib.gui.IHierarchyElement;
import stellarium.client.lib.gui.IRollHelper;
import stellarium.client.overlay.clientcfg.category.CategoryElementButton;
import stellarium.client.overlay.clientcfg.category.CategoryElementSimple;
import stellarium.client.overlay.clientcfg.model.ModelMain;
import stellarium.client.overlay.clientcfg.model.ModelRowBackground;
import stellarium.client.overlay.clientcfg.property.IPropertyElementType;
import stellarium.client.overlay.clientcfg.property.PropertyElementBoolean;
import stellarium.client.overlay.clientcfg.property.PropertyElementEnum;
import stellarium.client.overlay.clientcfg.property.PropertyElementRollable;
import stellarium.client.overlay.clientcfg.property.SubPropElementDouble;
import stellarium.client.overlay.clientcfg.property.SubPropElementInteger;

public class DefCfgHierarchyHandler implements ICfgHierarchyHandler {
	
	private Multimap<Property.Type, IPropertyElementType> controllers = HashMultimap.create();
	private Multimap<Property.Type, IPropertyElementType> listControllers = HashMultimap.create();
	
	private ModelMain model;
	private IRollHelper helper;
	
	public DefCfgHierarchyHandler(ModelMain model, IRollHelper helper) {
		this.composeControllers();
		this.model = model;
		this.helper = helper;
	}
	
	private void composeControllers() {
		controllers.put(Property.Type.BOOLEAN, new PropertyElementBoolean.Type());
		controllers.put(Property.Type.STRING, new PropertyElementEnum.Type());
		controllers.put(Property.Type.INTEGER, new SubPropElementInteger.Type());
		controllers.put(Property.Type.DOUBLE, new SubPropElementDouble.Type());
	}

	public boolean isPositionRight() {
		return false;
	}
	
	public boolean isBaseHorizontal() {
		return true;
	}
	
	public boolean isDirectionInverted(boolean isHorizontal) {
		return this.isPositionRight() || !isHorizontal;
	}

	@Override
	public boolean accept(ConfigCategory parent, ConfigCategory category) {
		return (parent != null || category.getName().contains(Configuration.CATEGORY_SPLITTER))
				&& !category.getLanguagekey().equals(category.getQualifiedName());
	}

	@Override
	public IHierarchyElement generate(ConfigCategory category, ICfgTooltipHandler tooltip) {
		for(Property property : category.values())
			if(property.getLanguageKey().endsWith(CfgConstants.SUFFIX_ENABLED)) {
				return new CategoryElementButton(category, property, this, tooltip);
			}
		return new CategoryElementSimple(category, this, tooltip);
	}

	@Override
	public boolean accept(Property property) {
		if(property.requiresWorldRestart() || property.getLanguageKey() == property.getName())
			return false;
		if(property.isList()) {
			for(IPropertyElementType type : listControllers.get(property.getType()))
				if(type.accept(property))
					return true;
			return false;
		} else {
			for(IPropertyElementType type : controllers.get(property.getType()))
				if(type.accept(property))
					return true;
			return false;
		}
	}

	@Override
	public IHierarchyElement generate(Property property, ICfgTooltipHandler tooltip) {
		if(property.isList()) {
			for(IPropertyElementType type : listControllers.get(property.getType()))
				if(type.accept(property)) {
					if(type.useRollable())
						return new PropertyElementRollable(property, this, tooltip, type);
					else return type.generate(property, this, tooltip);
				}
		} else {
			for(IPropertyElementType type : controllers.get(property.getType()))
				if(type.accept(property)) {
					if(type.useRollable())
						return new PropertyElementRollable(property, this, tooltip, type);
					else return type.generate(property, this, tooltip);
				}
		}
		
		throw new IllegalArgumentException(
				String.format("Property %s has no matching controller!\n"
						+ "Check for accept first.", property));
	}

	@Override
	public IHierarchyElement spacing(float size) {
		return HierarchyUtil.fromElement(
				new GuiElement<ISimpleRenderController>(new GuiSimpleRenderElement(),
				new ISimpleRenderController() {
			@Override
			public String setupRenderer(IRenderer renderer) {
				renderer.bindModel(ModelSimpleRect.getInstance());
				renderer.color(0.2f, 0.2f, 0.2f, 0.2f);
				return "";
			}
		}), size, 0.0f);
	}

	@Override
	public String setupBackground(boolean isHorizontal, IRenderer renderer) {
		renderer.bindModel(ModelRowBackground.getInstance());
		renderer.color(1.0f, 1.0f, 1.0f, 0.5f);
		return isHorizontal? helper.rollToDown()? "up" : "down "
			: helper.rollToRight()? "left" : "right";
	}

	@Override
	public void setupMainRenderer(boolean isHorizontal, IRenderer renderer) {
		renderer.bindModel(this.model);
	}
}
