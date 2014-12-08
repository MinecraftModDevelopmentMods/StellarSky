package stellarium.catalog;

import java.util.List;

import sciapi.api.value.IValRef;
import sciapi.api.value.euclidian.EVector;
import stellarium.config.IStellarConfig;
import stellarium.objs.IStellarObj;
import stellarium.util.math.SpCoord;
import stellarium.view.ViewPoint;

public interface IStellarCatalog {
	
	/**gives the name of this catalog*/
	public String getCatalogName();
	
	/**
	 * @return true iff. this catalog contains variables, etc.
	 * */
	public boolean isVariable();
	
	/**
	 * @return true iff. this catalog contains only pointy objects.
	 * */
	public boolean isPointy();
	
	/**
	 * gives render update tick duration for this catalog.
	 * */
	public int getRUpTick();
	
	/**
	 * gives the list of Stellar Objects in the catalog within certain (circular) range.
	 * 
	 * @param vp ViewPoint viewing the objects. Can be null for client
	 * @param dir the center of the range
	 * @param hfov the radius of the range
	 * @return the list of Stellar Objects within the range
	 * */
	public <T extends IStellarObj> List<T> getList(ViewPoint vp, SpCoord dir, double hfov);
	
	/**
	 * gives the (Average) Magnitude of this catalog.
	 * this will be used to determine render or not.
	 * */
	public double getMag();
	
	/**
	 * Priority value for Search.
	 * The bigger value, the prior it be.
	 * */
	public double prioritySearch();
	
	/**
	 * Priority value for Render.
	 * The bigger value, the prior it be.
	 * */
	public double priorityRender();
	
	/**
	 * called by TickHandler to update this catalog.
	 * */
	public void update(int tick);
	
	/**
	 * formats configuration to get certain input.
	 * always called before editing / loading configuration.
	 * */
	public void formatConfig(IStellarConfig cfg);
	
	/**
	 * called while initialization to load this catalog.
	 * NOTE: you have to apply configuration here,
	 *  for applyConfig is not called while initialization.
	 * */
	public void load(IStellarConfig cfg);
	
	/**
	 * called when configuration is finished to apply the settings.
	 * */
	public void applyConfig(IStellarConfig cfg);

	/**
	 * called when saving information as configuration
	 * */
	public void saveConfig(IStellarConfig subConfig);
	
	/**
	 * gives the type of object this catalog contains.
	 * */
	public EnumCatalogType getType();

}
