package fr.pmk_bungee;

//IMPORT SQL
import fr.pmk_bungee.MySQL.MySQLCredentials;
//IMPORT COMMAND
import fr.pmk_bungee.command.BanCommand;
import fr.pmk_bungee.command.CheckPlayer;
import fr.pmk_bungee.command.HistoryCommand;
import fr.pmk_bungee.command.InformationCommand;
import fr.pmk_bungee.command.KickCommand;
import fr.pmk_bungee.command.MuteCommand;
import fr.pmk_bungee.command.UnbanCommand;
import fr.pmk_bungee.command.UnmuteCommand;
import fr.pmk_bungee.command.WarnCommand;
//IMPORT LISTENER
import fr.pmk_bungee.listener.ChatEvent;
import fr.pmk_bungee.listener.LoginEvent;
//IMPORT CONFIG
import fr.pmk_bungee.utils.ConfigManager;
//IMPORT BUNGEE THINGS
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class Main extends Plugin{

	public static ConfigManager configManager;

	static Main sharedInstance = null;
	static MySQL mySQL;
	public static String host = "";
	public static String username = "";
	public static String password = "";
	public static String database = "";
	public static String PREFIX = "", CONSOLE_PREFIX = "";
	public static int port = 3306;

	public void onEnable() {

		sharedInstance = this;

		//Config
		configManager = new ConfigManager();
		configManager.init();

		//SQL
		MySQLCredentials credentials = new MySQLCredentials(host, port, username, password, database);
		mySQL = new MySQL(credentials);
		mySQL.openConnection();
		if(mySQL.isConnected()) {

			logToConsole("§aMySQL connection success.");
			//BASE DE DONNER
			mySQL.update("CREATE TABLE IF NOT EXISTS MinecraftPlayer( `userID` INT NOT NULL AUTO_INCREMENT , `username` VARCHAR(16) NOT NULL , `uuid` VARCHAR(256) NOT NULL ,`ip` VARCHAR(256) NOT NULL,`isPresent` BOOLEAN,PRIMARY KEY (`userID`))");
			//BDD Relation 1:N (1 = USER; 1 = OTHER)
			mySQL.update("CREATE TABLE IF NOT EXISTS ActualBungeeBan(userID INT, banEnd LONG, banReason VARCHAR(256),banBy INT, banAt DATETIME)");
			mySQL.update("CREATE TABLE IF NOT EXISTS ActualBungeeMutes(userID INT, muteEnd LONG, muteReason VARCHAR(256),muteBy INT, muteAt DATETIME)");
			mySQL.update("CREATE TABLE IF NOT EXISTS ActualBungeeKicks(userID INT, muteEnd LONG, muteReason VARCHAR(256),kickBy INT, kickAt DATETIME)");
			mySQL.update("CREATE TABLE IF NOT EXISTS PastBungeeBan(banID INT NOT NULL AUTO_INCREMENT, userID INT, banAt DATETIME, banReason VARCHAR(256), banBy INT, PRIMARY KEY (banID) )");
			mySQL.update("CREATE TABLE IF NOT EXISTS PastBungeeMutes(muteID INT NOT NULL AUTO_INCREMENT, userID INT, muteAt DATETIME, muteReason VARCHAR(256), muteBy INT, PRIMARY KEY (muteID))");
			mySQL.update("CREATE TABLE IF NOT EXISTS PastBungeeKicks(kickID INT NOT NULL AUTO_INCREMENT, userID INT, muteAt DATETIME, muteReason VARCHAR(256), muteBy INT, PRIMARY KEY (kickID))");
			mySQL.update("CREATE TABLE IF NOT EXISTS BungeeWarn(warnID INT NOT NULL AUTO_INCREMENT, userID INT, warnAt DATETIME, warnReason VARCHAR(256), warnBy INT, PRIMARY KEY (warnID))");

		}

		getProxy();
		PluginManager pm = ProxyServer.getInstance().getPluginManager();

		//UNBAN
		pm.registerCommand(this, new UnbanCommand("unban"));
		//UNMUTE
		pm.registerCommand(this, new UnmuteCommand("unmute"));
		//MUTE
		pm.registerCommand(this, new MuteCommand("mute"));
		//BAN
		pm.registerCommand(this, new BanCommand("ban"));
		//CHECK IF BAN / MUTE
		pm.registerCommand(this, new CheckPlayer("check"));
		//GET HISTORY OF THE PLAYER
		pm.registerCommand(this, new HistoryCommand("history"));
		// GET ALL INFORMATION OF A PLAYER
		pm.registerCommand(this, new InformationCommand("info"));
		//Kick a Player
		pm.registerCommand(this, new KickCommand("kick"));
		//Warn a player (FOREVER)
		pm.registerCommand(this, new WarnCommand("warn"));

		pm.registerListener(this, new ChatEvent());

		pm.registerListener(this, new LoginEvent());
	}

	//GETTER AND SETTER 

	@SuppressWarnings("deprecation")
	private void logToConsole(String string) {
		ProxyServer.getInstance().getConsole().sendMessage(string);
	}

	public static Main sharedInstance() {
		return sharedInstance;
	}

	public static MySQL getMySQL() {
		return mySQL;
	}

	public static ConfigManager getConfigManager() {
		return configManager;
	}

	public enum TimeUnit
	{
		SECOND(new String[] { "s", "sec", "secs", "second", "seconds" }, 1L),  MINUTE(new String[] { "m", "min", "mins", "minute", "minutes" }, 60L),  HOUR(new String[] { "h", "hs", "hour", "hours" }, 3600L),  DAY(new String[] { "d", "ds", "day", "days" }, 86400L);

		private String[] names;
		private long seconds;

		private TimeUnit(String[] names, long seconds)
		{
			this.names = names;
			this.seconds = seconds;
		}

		public long getSeconds()
		{
			return this.seconds;
		}

		public String[] getNames()
		{
			return this.names;
		}

		public static TimeUnit getByString(String str)
		{
			for (TimeUnit timeUnit : TimeUnit.values()) {
				for (String name : timeUnit.getNames()) {
					if (name.equalsIgnoreCase(str)) {
						return timeUnit;
					}
				}
			}
			return null;
		}
	}

}
