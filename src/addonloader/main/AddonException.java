package addonloader.main;

/**
 * Exception that may be thrown during loading addons.
 * @author Enginecrafter77
 */
public class AddonException extends Exception {
	private static final long serialVersionUID = 3685105936835969469L;

	public AddonException(MenuAddon source, Exception cause, LoadingStage s)
	{
		super("Error at " + s.name() + " in addon " + source.getName() + " (" + cause.getClass().getName()+ ")");
		this.setStackTrace(cause.getStackTrace());
	}
	
}
