package addonloader.util;

/**
 * Interface implemented by so called extra carriers.
 * Extra carriers are objects that must in some situations provide an extra argument to another
 * classes. This interface implements simple getter and setter for this purpose.
 * @author Enginecrafter77
 * @param <T> The type of carried object.
 */
public interface ExtraCarrier<T> {
	/**
	 * Sets extra object that will be stored in this object. Can be retrieved using {@link #fetchCarrier()}.
	 * @param extra The extra string to be set.
	 */
	public void loadCarrier(T extra);
	/**
	 * Returns extra stored in this object, that was previously set by {@link #loadCarrier(T)} 
	 * @return Extra object stored.
	 */
	public T fetchCarrier();
}
