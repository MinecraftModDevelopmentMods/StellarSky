package stellarium.client.gui.content.animation;

import stellarium.client.gui.content.IElementController;
import stellarium.client.gui.content.IGuiPosition;

public interface IRollableFluentController extends IElementController {

	public boolean isHorizontal();
	public boolean increaseCoordOnRoll();
	public boolean disableControlOnAnimating();
	
	/**
	 * Inverts roll state when actual coordinate decreases.
	 * Setting this to true ensures that roll state is 0 on rolled and 1 on unrolled,
	 * while false ensures that roll state is dependent to the coordinate.
	 * */
	public boolean isRollStateIndependent();
	
	public float rollState();
	public boolean forceState();
	
	public float rollRatePerTick();
	
	public IGuiPosition wrapExcludedPosition(IGuiPosition wrapped, IGuiPosition rollPos);

}
