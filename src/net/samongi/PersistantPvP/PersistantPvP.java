package net.samongi.PersistantPvP;

import java.io.File;
import java.util.logging.Logger;

import net.samongi.PersistantPvP.GameManager.GameManager;
import net.samongi.PersistantPvP.Listeners.EntityListener;
import net.samongi.PersistantPvP.Listeners.PlayerListener;
import net.samongi.PersistantPvP.Score.Announcer;
import net.samongi.PersistantPvP.Score.ScoreKeeper;
import net.samongi.PersistantPvP.Score.StatKeeper;
import net.samongi.SamongiLib.Configuration.ConfigAccessor;
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
	private ScoreKeeper score_keeper;
	private StatKeeper stat_keeper;
	
	
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
    
    // map-config handling;
    File map_config_file = new File(this.getDataFolder(),"maps.yml");
    ConfigAccessor map_config = new ConfigAccessor(this, "maps.yml");
    if(!map_config_file.exists())
    {
      PersistantPvP.log("Found no map config file, copying over defaults...");
      map_config.getConfig().options().copyDefaults(true);
      map_config.saveConfig();
    }
    
    // rewards-config handling:
    File rewards_config_file = new File(this.getDataFolder(),"rewards.yml");
    ConfigAccessor rewards_config = new ConfigAccessor(this, "rewards.yml");
    if(!rewards_config_file.exists())
    {
      PersistantPvP.log("Found no rewards config file, copying over defaults...");
      rewards_config.getConfig().options().copyDefaults(true);
      rewards_config.saveConfig();
    }
    
    // loadout-config-generation if not there.
    // this.saveResource("loadouts", false);

    // Scorekeeper
    score_keeper = new ScoreKeeper();
    
    // Statkeeper
    stat_keeper = new StatKeeper(this);
    stat_keeper.loadAllPlayers();
    
    // Game Handler handling
    this.game_handler = new GameManager(this, stat_keeper);
    game_handler.parseMapConfig(map_config);
    game_handler.parseLoadoutConfig();
    game_handler.parseRewardsConfig(rewards_config);
    // initial map switch.
    game_handler.switch_maps("twns");
    
    // announcer setup
    announcer = new Announcer(stat_keeper, score_keeper, game_handler);
    
    // Listeners
    PluginManager pm = this.getServer().getPluginManager();
    pm.registerEvents(new PlayerListener(this, this.game_handler, this.score_keeper, this.stat_keeper), this);
    pm.registerEvents(new EntityListener(this, this.game_handler, this.score_keeper), this);
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
