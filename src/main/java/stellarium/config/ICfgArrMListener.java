package stellarium.config;

/**
 * Category Arrangement Modification Listener.
 * */
public interface ICfgArrMListener {

	/**called when a category is added in any ways.*/
	public void onNew(IConfigCategory cat);
	
	/**called when a category is removed in any ways. (sub-configurations will all be removed)*/
	public void onRemove(IConfigCategory cat);
	
	/**called when a category changed its parent, only for tree-type arrangement*/
	public void onChangeParent(IConfigCategory cat, IConfigCategory from, IConfigCategory to);
	
	/**called when a category changed its order*/
	public void onChangeOrder(IConfigCategory cat, int before, int after);
	
	/**Always called when display name of category is changed.*/
	public void onDispNameChange(IConfigCategory cat, String before);
	
}
