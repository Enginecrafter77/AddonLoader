package addonloader.util;

/**
 * Interface implemented by so called extra carriers.
 * Extra carriers are objects that must in some situations provide an extra argument to another
 * classes. This interface implements simple getter and setter for this purpose.
 * @author Enginecrafter77
 * @param DATA_TYPE The type of carried object.
 */
public interface DataCarrier<DATA_TYPE> {
	/**
	 * Sets extra object that will be stored in this object. Can be retrieved using {@link #fetch_carrier()}.
	 * @param data The extra string to be set.
	 */
	public void load_carrier(DATA_TYPE data);
	/**
	 * Returns extra stored in this object, that was previously set by {@link #load_carrier(DATA_TYPE)} 
	 * @return Extra object stored.
	 */
	public DATA_TYPE fetch_carrier();
	
}
