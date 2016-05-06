package stellarium.client.gui.content.animation;

import stellarium.client.gui.content.IElementController;

public interface ILinearMoveController extends IElementController {

	/**
	 * Initial X ratio on the animation bounds, should be in [0,1].
	 * */
	public float initialRatioX();
	
	/**
	 * Initial Y ratio on the animation bounds, should be in [0,1].
	 * */
	public float initialRatioY();
	
	public boolean disableControlOnAnimating();
	public boolean forceState();
	
	public boolean doesStartAnimation();
	public boolean needHaltAnimation();
	public void onAnimationEnded();
	
	public int getAnimationDuration();
	
	/**
	 * Next X ratio on the animation bounds, should be in [0,1].
	 * */
	public float nextRatioX();
	
	/**
	 * Next Y ratio on the animation bounds, should be in [0,1].
	 * */
	public float nextRatioY();


}
