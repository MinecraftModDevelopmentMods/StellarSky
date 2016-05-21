package stellarium.client.lib.gui;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import stellarapi.lib.gui.GuiElement;
import stellarapi.lib.gui.IGuiPosition;
import stellarapi.lib.gui.IRectangleBound;
import stellarapi.lib.gui.IRenderer;
import stellarapi.lib.gui.RectangleBound;
import stellarapi.lib.gui.animation.GuiRollableSimple;
import stellarapi.lib.gui.animation.IRollableSimpleController;
import stellarapi.lib.gui.button.GuiButtonSimple;
import stellarapi.lib.gui.button.IButtonController;
import stellarapi.lib.gui.dynamic.GuiDynamic;
import stellarapi.lib.gui.dynamic.IDynamicController;
import stellarapi.lib.gui.list.GuiHasFixedList;
import stellarapi.lib.gui.list.IHasFixedListController;
import stellarapi.lib.gui.simple.GuiEmptyElement;
import stellarapi.lib.gui.spacing.GuiSpacingButton;
import stellarapi.lib.gui.spacing.ISpacingButtonController;

public class RollHierarchyController implements IDynamicController {

	private RollHierarchyController parent;
	
	private IHierarchyElement element;
	private List<RollHierarchyController> childs = Lists.newArrayList();
	private boolean isHorizontal;
	private IRollHelper helper;
	private float parentSpacing;
	
	private float rollSize;
	private boolean forceRoll = false, isRolled = true;
	private boolean forceUpdate = false;
	private boolean preventParentRolling = false;
	
	public RollHierarchyController(IHierarchyElement element, boolean isHorizontal,
			IRollHelper helper, float parentSpacingSize) {
		this(null, element, isHorizontal, helper, parentSpacingSize);
	}
	
	public RollHierarchyController(RollHierarchyController parent, IHierarchyElement element, boolean isHorizontal,
			IRollHelper helper, float parentSpacingSize) {
		this.element = element;
		this.isHorizontal = isHorizontal;
		this.helper = helper;
		this.parent = parent;
		this.parentSpacing = parentSpacingSize;
	}

	@Override
	public boolean needUpdate() {
		boolean flag = this.forceUpdate;
		this.forceUpdate = false;
		return flag || element.needUpdate();
	}
	
	public void forceUpdate() {
		this.forceUpdate = true;
	}

	@Override
	public GuiElement generateElement() {
		this.rollSize = 0.0f;
		List<Pair<GuiElement, Float>> rowList = Lists.newArrayList();

		List<IHierarchyElement> childList = element.generateChildElements();
		childs.clear();
		
		if(childList != null && !childList.isEmpty()) {
			for(IHierarchyElement child : childList) {
				RollHierarchyController subController = new RollHierarchyController(this, child, !this.isHorizontal, this.helper, element.rollSpacingSize());
				childs.add(subController);
				this.rollSize += (child.getSize() + 2 * child.rollSpacingSize());
				GuiElement childGui = new GuiElement<IDynamicController>(new GuiDynamic(), subController);
				rowList.add(Pair.of(childGui, child.getSize() + 2 * child.rollSpacingSize()));
			}

			GuiElement simpleSpace = GuiEmptyElement.generateEmptyElement();

			GuiElement row = new GuiElement<IHasFixedListController>(
					new GuiHasFixedList(simpleSpace, rowList),
					new ListController());
			
			if(element.hasRoll()) {
				GuiElement roll;
				if(helper.hasRollButton()) {
					GuiElement rolBtn = new GuiElement<IButtonController>(new GuiButtonSimple(), new RollBtnController());
					roll = new GuiElement<IRollableSimpleController>(
							new GuiRollableSimple(row, rolBtn, helper.rollBtnSize()),
							new RollController());
				} else {
					roll = new GuiElement<IRollableSimpleController>(
							new GuiRollableSimple(row, simpleSpace, 0.0f),
							new RollController());
				}

				GuiElement context = element.generateGui(this.isHorizontal, this.helper);

				GuiElement spacedContext = new GuiElement<ISpacingButtonController>(
						new GuiSpacingButton(context), new SpacingController());
				
				return new GuiElement<IHasFixedListController>(
						new GuiHasFixedList(roll, spacedContext, element.getSize() + 2 * parentSpacing),
						new ModalController());
			} else return row;
		} else return new GuiElement<ISpacingButtonController>(
				new GuiSpacingButton(element.generateGui(this.isHorizontal, this.helper)),
				new SpacingController());
	}
	
	public void forceRoll() {
		this.forceRoll = true;
		this.isRolled = true;
		this.forceRollSubElements();
	}
	
	public boolean isSubElementsRolled() {
		if(this.preventParentRolling)
			return false;

		for(RollHierarchyController child : this.childs)
			if(!child.isSubElementsRolled())
				return false;

		return true;
	}
	
	public void forceRollSubElements() {
		for(RollHierarchyController child : this.childs)
			child.forceRoll();
	}
	
	private void forceRollOthers(RollHierarchyController unrolled) {
		for(RollHierarchyController child : this.childs)
			if(unrolled != child) {
				child.forceRoll();
				child.forceRoll = false;
			}
	}
	
	public void updateRoll(boolean isRolled, boolean withForce) {
		if(isRolled) {
			if(withForce) {
				this.forceRollSubElements();
				this.isRolled = true;
			} else if(this.isSubElementsRolled()) {
				this.isRolled = true;
			}
		} else if(parent != null) {
			parent.forceRollOthers(this);
			this.isRolled = false;
		}
	}
	
	public boolean checkSettingsChanged() {
		boolean isChanged = element.checkSettingsChanged();
		
		for(RollHierarchyController child : this.childs)
			isChanged = isChanged || child.checkSettingsChanged();
		
		return isChanged;
	}
	
	public float rollSize() {
		return this.rollSize;
	}
	
	public class SpacingController implements ISpacingButtonController {
		private boolean clicked, hovering;
		private long updateTimeMillis;
		
		@Override
		public String setupSpacingRenderer(IRenderer renderer) {
			return null;
		}

		@Override
		public float getSpacingLeft() {
			return isHorizontal? parentSpacing : element.rollSpacingSize();
		}

		@Override
		public float getSpacingRight() {
			return isHorizontal? parentSpacing : element.rollSpacingSize();
		}

		@Override
		public float getSpacingUp() {
			return !isHorizontal? parentSpacing : element.rollSpacingSize();
		}

		@Override
		public float getSpacingDown() {
			return !isHorizontal? parentSpacing : element.rollSpacingSize();
		}

		@Override
		public boolean canClick(int eventButton) {
			return eventButton == 0;
		}

		@Override
		public void onClicked(int eventButton) {
			if(childs.isEmpty())
				return;
			this.clicked = true;
			updateRoll(element.updateRollOnSpacing(isRolled, this.clicked, this.hovering), false);
		}

		@Override
		public void onClickEnded(int eventButton) {
			if(childs.isEmpty())
				return;
			this.clicked = false;
			updateRoll(element.updateRollOnSpacing(isRolled, this.clicked, this.hovering), false);
		}

		@Override
		public void setupRenderer(boolean mouseOver, IRenderer renderer) {
			if(childs.isEmpty())
				return;
			helper.setupSpacingBtnRenderer(isHorizontal, mouseOver, renderer);
		}

		@Override
		public String setupOverlay(boolean mouseOver, IRenderer renderer) {
			if(childs.isEmpty())
				return null;
			return helper.setupSpacingBtnOverlay(isHorizontal, mouseOver, renderer);
		}

		@Override
		public String setupMain(boolean mouseOver, IRenderer renderer) {
			if(childs.isEmpty())
				return null;
			return helper.setupSpacingBtnMain(isHorizontal, mouseOver, renderer);
		}
		
		@Override
		public void updateHovering(boolean newHover) {
			if(this.hovering != newHover) {
				this.updateTimeMillis = System.currentTimeMillis();
				this.hovering = newHover;
				if(this.hovering)
					preventParentRolling = true;
			}
			
			if(System.currentTimeMillis() - this.updateTimeMillis > helper.hoverUpdateDelay()) {
				this.updateTimeMillis = -1;
				boolean roll = element.updateRollOnSpacing(isRolled, this.clicked, this.hovering);
				updateRoll(roll, false);
				if(!this.hovering)
					preventParentRolling = false;
			}
		}

		@Override
		public boolean handleInElement() {
			return true;
		}
		
	}

	public class ListController implements IHasFixedListController {
		@Override
		public boolean isHorizontal() {
			return isHorizontal;
		}
		
		@Override
		public String setupRenderer(IRenderer renderer) {
			return element.setupBackground(isHorizontal, renderer);
		}
		
		@Override
		public boolean isModifiableFirst() {
			return false;
		}
		@Override
		public IGuiPosition wrapFixedPosition(IGuiPosition position, IGuiPosition listPos) {
			return position;
		}
		@Override
		public IGuiPosition wrapModifiablePosition(IGuiPosition position, IGuiPosition listPos) {
			return position;
		}
	}
	
	public class ModalController implements IHasFixedListController {
		@Override
		public boolean isHorizontal() {
			return isHorizontal;
		}
		
		@Override
		public String setupRenderer(IRenderer renderer) {
			return null;
		}

		@Override
		public boolean isModifiableFirst() {
			return helper.isDirectionInverted(isHorizontal);
		}

		@Override
		public IGuiPosition wrapFixedPosition(IGuiPosition position, IGuiPosition listPos) {
			return position;
		}

		@Override
		public IGuiPosition wrapModifiablePosition(IGuiPosition position, IGuiPosition listPos) {
			return new Wrapper(position);
		}
		
		public class Wrapper implements IGuiPosition {
			private IGuiPosition wrapped;
			private RectangleBound element, clip;
			
			public Wrapper(IGuiPosition position) {
				this.wrapped = position;
			}

			@Override
			public IRectangleBound getElementBound() {
				return this.element;
			}

			@Override
			public IRectangleBound getClipBound() {
				return this.clip;
			}

			@Override
			public IRectangleBound getAdditionalBound(String boundName) {
				return null;
			}

			@Override
			public void initializeBounds() {
				wrapped.initializeBounds();
				element = new RectangleBound(wrapped.getElementBound());
				clip = new RectangleBound(wrapped.getClipBound());
				this.setup();
			}

			@Override
			public void updateBounds() {
				wrapped.updateBounds();
				element.set(wrapped.getElementBound());
				clip.set(wrapped.getClipBound());
				this.setup();
			}
			
			private void setup() {
				if(isHorizontal) {
					if(helper.rollToRight())
						element.posX -= rollSize;
					element.width += rollSize;
					clip.posX = element.posX;
					clip.width = element.width;
				} else {
					if(helper.rollToDown())
						element.posY -= rollSize;
					element.height += rollSize;
					clip.posY = element.posY;
					clip.height = element.height;
				}
			}

			@Override
			public void updateAnimation(float partialTicks) {
				wrapped.updateAnimation(partialTicks);
				element.set(wrapped.getElementBound());
				clip.set(wrapped.getClipBound());
				this.setup();
			}
		}
	}
	
	public class RollBtnController implements IButtonController {
		@Override
		public boolean canClick(int eventButton) {
			return eventButton == 0;
		}

		@Override
		public void onClicked(int eventButton) {
			updateRoll(!isRolled, true);
		}

		@Override
		public void onClickEnded(int eventButton) { }

		@Override
		public void setupRenderer(boolean mouseOver, IRenderer renderer) {
			helper.setupRollBtnRenderer(isHorizontal, mouseOver, renderer);
		}

		@Override
		public String setupOverlay(boolean mouseOver, IRenderer renderer) {
			return helper.setupRollBtnOverlay(isHorizontal, mouseOver, renderer);
		}

		@Override
		public String setupMain(boolean mouseOver, IRenderer renderer) {
			return helper.setupRollBtnMain(isHorizontal, mouseOver, renderer);
		}
	}
	
	public class RollController implements IRollableSimpleController {
		@Override
		public boolean isHorizontal() {
			return isHorizontal;
		}

		@Override
		public boolean increaseCoordOnRoll() {
			return helper.isDirectionInverted(isHorizontal);
		}

		@Override
		public boolean shouldBeRolled() {
			return isRolled;
		}

		@Override
		public boolean forceState() {
			boolean flag = forceRoll;
			forceRoll = false;
			return flag;
		}

		@Override
		public int onRollingStart(boolean isRolling) {
			return element.rollDuration();
		}

		@Override
		public void onRollingEnded(boolean isRolling) { }
	}
}
