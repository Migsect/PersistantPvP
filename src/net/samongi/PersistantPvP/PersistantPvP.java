package net.samongi.PersistantPvP;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import net.samongi.PersistantPvP.Commands.CommandHelp;
import net.samongi.PersistantPvP.Commands.CommandMap;
import net.samongi.PersistantPvP.Commands.CommandStats;
import net.samongi.PersistantPvP.GameManager.GameManager;
import net.samongi.PersistantPvP.Listeners.EntityListener;
import net.samongi.PersistantPvP.Listeners.GameListener;
import net.samongi.PersistantPvP.Listeners.PlayerListener;
import net.samongi.PersistantPvP.Maps.MapManager;
import net.samongi.PersistantPvP.Score.Announcer;
import net.samongi.PersistantPvP.Score.StatKeeper;
import net.samongi.SamongiLib.CommandHandling.CommandHandler;
import net.samongi.SamongiLib.Player.Group;
import net.samongi.SamongiLib.Player.ServerGroup;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PersistantPvP extends JavaPlugin
{
	public static Logger logger;
	public static boolean debug = false;
	public static Group group;
	public static Announcer announcer;
	
	private GameManager game_handler;
	private StatKeeper stat_keeper;
	
	private CommandHandler command_handler;
	
	
	public void onEnable()
	{
	  logger = this.getLogger();
	  group = new ServerGroup(Bukkit.getServer());
	  
		// config handling.
    File config_file = new File(this.getDataFolder(),"config.yml");
    if(!config_file.exists())
    {
      PersistantPvP.log("Found no config file, copying over defaults...");
      this.getConfig().options().copyDefaults(true);
      this.saveConfig();
    }
    debug = this.getConfig().getBoolean("debug", true);
    
    // Statkeeper
    stat_keeper = new StatKeeper(this);
    stat_keeper.loadAllPlayers();
    
    // Game Handler handling
    this.game_handler = new GameManager(this, stat_keeper);
    
    // Setting up the initial map:
    MapManager map_manager = this.game_handler.getMapManager();
    List<String> map_keys = new ArrayList<>(map_manager.getMapKeys());
    Random rand = new Random();
    String map_key = map_keys.get(rand.nextInt(map_keys.size()));
    map_manager.setCurrentMap(map_key);
    
    // announcer setup
    announcer = new Announcer(stat_keeper, game_handler);
    
    // Listeners
    PluginManager pm = this.getServer().getPluginManager();
    pm.registerEvents(new PlayerListener(this, this.game_handler, this.stat_keeper), this);
    pm.registerEvents(new EntityListener(this, this.game_handler, this.stat_keeper), this);
    pm.registerEvents(new GameListener(this.game_handler), this);
    
    // Commands
    command_handler = new CommandHandler(this);
    command_handler.registerCommand(new CommandHelp("persistantpvp",command_handler));
    command_handler.registerCommand(new CommandStats("persistantpvp stats",stat_keeper, game_handler));
    command_handler.registerCommand(new CommandMap("persistantpvp map", this.game_handler.getMapManager()));
	}
	
	public void onDisable()
	{
    stat_keeper.saveAllPlayers();
	}
	
  static final public void log(String to_log)
  {
    logger.info(to_log);
  }
  static final public void debugLog(String to_log)
  {
    if(debug == true) logger.info(to_log);
  }
  static final public boolean debug(){return debug;}

}
