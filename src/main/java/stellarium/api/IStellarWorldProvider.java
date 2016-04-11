package stellarium.api;

public interface IStellarWorldProvider {
	
	/**
	 * Sets the sky provider for this world.
	 * Will be automatically called by Stellar Sky.
	 * @param skyProvider the Sky Provider.
	 * */
	public void setSkyProvider(ISkyProvider skyProvider);
	
	/**
	 * Gets the sky provider for this world.
	 * */
	public ISkyProvider getSkyProvider();

}
