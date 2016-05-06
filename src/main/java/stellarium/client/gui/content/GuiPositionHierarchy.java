package stellarium.client.gui.content;

import java.util.List;

import com.google.common.collect.Lists;

public class GuiPositionHierarchy {
	
	private IGuiPosition position;
	private List<GuiPositionHierarchy> childs = Lists.newArrayList();
	
	GuiPositionHierarchy(IGuiPosition position) {
		this.position = position;
	}
	
	public IGuiPosition getPosition() {
		return this.position;
	}

	public GuiPositionHierarchy addChild(IGuiPosition child) {
		GuiPositionHierarchy createdChild = new GuiPositionHierarchy(child);
		childs.add(createdChild);
		return createdChild;
	}
	
	public void initializeBounds() {
		position.initializeBounds();
		for(GuiPositionHierarchy child : childs)
			child.initializeBounds();
	}
	
	public void updateBounds() {
		position.updateBounds();
		for(GuiPositionHierarchy child : childs)
			child.updateBounds();
	}
	
	public void updateAnimation(float partialTicks) {
		position.updateAnimation(partialTicks);
		for(GuiPositionHierarchy child : childs)
			child.updateAnimation(partialTicks);
	}

}
