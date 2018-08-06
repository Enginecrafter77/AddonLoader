package addonloader.util;

/**
 * Data carrier is an simple interface, providing
 * way to access single type property of implementing
 * class, without much hassle around casting. The
 * object can be stored using {@link #load_carrier(Object)},
 * and retrieved using {@link #fetch_carrier()}.
 * @author Enginecrafter77
 * @param DATA_TYPE The type of carried object.
 */
public interface DataCarrier<DATA_TYPE> {
	/**
	 * Sets the data carried by this object instance.
	 * @param The data going to be carried by object's instance
	 */
	public void load_carrier(DATA_TYPE data);
	/**
	 * Retrieves the data carried by this object instance,
	 * previously set by load_carrier or internal
	 * class methods.
	 * @return The data carried by object's instance
	 */
	public DATA_TYPE fetch_carrier();
	
}
