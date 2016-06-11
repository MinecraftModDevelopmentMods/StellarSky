package stellarium.api;

import com.google.common.collect.ImmutableList;

public interface ISkyType {
	/**
	 * Adds external render type.
	 * Add type here if this sky type accepts the render type.
	 * The first type is the default type.
	 * @param type sky render type
	 * */
	public void addRenderType(ISkyRenderType type);
	
	/**
	 * Whether this sky type needs update or not.
	 * */
	public boolean needUpdate();
	
	/**
	 * Gives possible render types.
	 * */
	public ImmutableList<ISkyRenderType> possibleTypes();


	/**
	 * Gets default double value.
	 * */
	public double getDefaultDouble(EnumSkyProperty property);
	
	/**
	 * Gets default double list.
	 * */
	public double[] getDefaultDoubleList(EnumSkyProperty property);
	
	/**
	 * Gets default boolean value.
	 * */
	public boolean getDefaultBoolean(EnumSkyProperty property);
}