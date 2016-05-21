package stellarium.client.lib.gui;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import stellarapi.lib.gui.GuiElement;
import stellarapi.lib.gui.IRenderer;

public class HierarchyUtil {
	
	/**
	 * Gets hierarchy elements from gui element.
	 * */
	public static IHierarchyElement fromElement(GuiElement element, float size, float spacing) {
		return new SimpleElement(element, size, spacing);
	}
	
	private static class SimpleElement implements IHierarchyElement {
		private GuiElement fixedElement;
		private float size, spacing;
		
		public SimpleElement(GuiElement element, float size, float spacing) {
			this.fixedElement = element;
			this.size = size;
			this.spacing = spacing;
		}

		@Override
		public List<IHierarchyElement> generateChildElements() {
			return null;
		}

		@Override
		public GuiElement generateGui(boolean isHorizontal, IRollHelper helper) {
			return this.fixedElement;
		}

		@Override
		public boolean hasRoll() {
			return false;
		}

		@Override
		public float getSize() {
			return this.size;
		}

		@Override
		public int rollDuration() {
			return 0;
		}

		@Override
		public boolean checkSettingsChanged() {
			return false;
		}

		@Override
		public boolean needUpdate() {
			return false;
		}

		@Override
		public String setupBackground(boolean isHorizontal, IRenderer renderer) {
			return null;
		}

		@Override
		public float rollSpacingSize() {
			return this.spacing;
		}

		@Override
		public boolean handleSpacingInElement() {
			return false;
		}

		@Override
		public boolean updateRollOnSpacing(boolean isRolled, boolean clicked, boolean hovering) {
			return isRolled;
		}
	}
	
	/**Prepend fixed hierarchy elements.*/
	public static IHierarchyElement prepend(IHierarchyElement main, IHierarchyElement... additionals) {
		return new HierarchyElementAdditional(main, true, additionals);
	}
	
	/**Append fixed hierarchy elements.*/
	public static IHierarchyElement append(IHierarchyElement main, IHierarchyElement... additionals) {
		return new HierarchyElementAdditional(main, false, additionals);
	}

	private static class HierarchyElementAdditional implements IHierarchyElement {
		private boolean pre;
		private IHierarchyElement wrapped;
		private ImmutableList<IHierarchyElement> fixedAdditionals;
		
		public HierarchyElementAdditional(IHierarchyElement element, boolean pre, IHierarchyElement... additionals) {
			this.wrapped = element;
			this.pre = pre;
			this.fixedAdditionals = ImmutableList.copyOf(additionals);
		}

		@Override
		public List<IHierarchyElement> generateChildElements() {
			if(this.pre)
				return ImmutableList.copyOf(
						Iterables.concat(this.fixedAdditionals, wrapped.generateChildElements()));
			else return ImmutableList.copyOf(
					Iterables.concat(wrapped.generateChildElements(), this.fixedAdditionals));
		}

		@Override
		public GuiElement generateGui(boolean isHorizontal, IRollHelper helper) {
			return wrapped.generateGui(isHorizontal, helper);
		}

		@Override
		public boolean hasRoll() {
			return wrapped.hasRoll();
		}

		@Override
		public float getSize() {
			return wrapped.getSize();
		}

		@Override
		public int rollDuration() {
			return wrapped.rollDuration();
		}

		@Override
		public boolean checkSettingsChanged() {
			return wrapped.checkSettingsChanged();
		}

		@Override
		public boolean needUpdate() {
			return wrapped.needUpdate();
		}

		@Override
		public String setupBackground(boolean isHorizontal, IRenderer renderer) {
			return wrapped.setupBackground(isHorizontal, renderer);
		}

		@Override
		public float rollSpacingSize() {
			return wrapped.rollSpacingSize();
		}

		@Override
		public boolean handleSpacingInElement() {
			return wrapped.handleSpacingInElement();
		}

		@Override
		public boolean updateRollOnSpacing(boolean isRolled, boolean clicked, boolean hovering) {
			return wrapped.updateRollOnSpacing(isRolled, clicked, hovering);
		}

	}

}
