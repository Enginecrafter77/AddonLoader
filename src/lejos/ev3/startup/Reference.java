package lejos.ev3.startup;

public class Reference
{    
    /**
     * API Version is version of the base menu library, that defines level of the api.
     * Higher means newer. If addon is built for older version, it won't get loaded.
     * You should NOT reference this as your API version. You might run into compatibility issues.
     */
    public static final int API_LEVEL = 3;
    
	public static final int ANIM_DELAY = 250;
	public static final int ACTIVITY_TIMEOUT = 1000;
	public static final int IP_UPDATE = 30*1000;
	public static final int ICON_BATTERY_POS = 0;
	public static final int ICON_BATTERY_WIDTH = 12;
	public static final int ICON_BATTERY_BLINK = 4 * Reference.ANIM_DELAY;
	public static final int RCONSOLE_PORT = 8001;
	public static final int REMOTE_MENU_PORT = 8002;
	public static final int BROADCAST_PORT = 3016;
	
	public static final String JAVA_RUN_CP = "jrun -cp ";
	public static final String JAVA_DEBUG_CP = "jrun -Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8000,suspend=y -cp ";
	
	public static final String defaultProgramProperty = "lejos.default_program";
	public static final String defaultProgramAutoRunProperty = "lejos.default_autoRun";
	public static final String pinProperty = "lejos.bluetooth_pin";
	public static final String ntpProperty = "lejos.ntp_host";
	
	public static final String PROGRAMS_DIRECTORY = "/home/lejos/programs";
	public static final String LIB_DIRECTORY = "/home/lejos/lib";
	public static final String SAMPLES_DIRECTORY = "/home/root/lejos/samples";
	public static final String TOOLS_DIRECTORY = "/home/root/lejos/tools";
	public static final String MENU_DIRECTORY = "/home/root/lejos/menu";
	public static final String START_BLUETOOTH = "/home/root/lejos/bin/startbt";
	public static final String START_WLAN = "/home/root/lejos/bin/startwlan";
	public static final String START_PAN = "/home/root/lejos/bin/startpan";
	public static final String PAN_CONFIG = "/home/root/lejos/config/pan.config";
	public static final String WLAN_INTERFACE = "wlan0";
	public static final String PAN_INTERFACE = "br0";
	
	public static final int IND_SUSPEND = -1; //ORIG -1
	public static final int IND_NORMAL = 1;
	public static final int IND_FULL = 2;
	//public static final int IND_NONE = 0; NOT USED ANYWHERE
	
}
