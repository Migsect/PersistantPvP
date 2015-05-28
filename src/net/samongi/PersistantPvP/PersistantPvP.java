package net.samongi.PersistantPvP;

import java.util.logging.Logger;

import net.samongi.PersistantPvP.Listeners.EntityListener;
import net.samongi.PersistantPvP.Listeners.PlayerListener;
import net.samongi.PersistantPvP.Maps.GameHandler;
import net.samongi.PersistantPvP.Score.ScoreKeeper;
import net.samongi.SamongiLib.Configuration.ConfigAccessor;
import net.samongi.SamongiLib.Player.Group;
import net.samongi.SamongiLib.Player.ServerGroup;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PersistantPvP extends JavaPlugin
{
	public static Logger logger = Logger.getLogger("minecraft");
	public static boolean debug = false;
	public static Group group = new ServerGroup(Bukkit.getServer());
	
	private GameHandler game_handler;
	private ScoreKeeper score_keeper;
	
	
	public void onEnable()
	{
		// config handling.
	  //this.saveDefaultConfig();
    this.getConfig().options().copyDefaults(true);
    this.saveConfig();
    debug = this.getConfig().getBoolean("debug", true);
    
    // map-config handling;
    ConfigAccessor map_config = new ConfigAccessor(this, "maps.yml");
    map_config.getConfig().options().copyDefaults(true);
    map_config.saveConfig();
    
    // loadout-config handling:
    ConfigAccessor loadout_config = new ConfigAccessor(this, "loadouts.yml");
    loadout_config.getConfig().options().copyDefaults(true);
    loadout_config.saveConfig();
    
    // random-loadout-config handling:
    ConfigAccessor rand_loadout_config = new ConfigAccessor(this, "loadouts-random.yml");
    rand_loadout_config.getConfig().options().copyDefaults(true);
    rand_loadout_config.saveConfig();
    
    // Game Handler handling
    this.game_handler = new GameHandler(this);
    game_handler.parseMapConfig(map_config);
    game_handler.parseLoadoutConfig(loadout_config);
    game_handler.praseLoadoutRandomConfig(rand_loadout_config);
    // initial map switch.
    game_handler.switch_maps("twns");
    
    // Scorekeeper
    score_keeper = new ScoreKeeper();
    
    // Listeners
    PluginManager pm = this.getServer().getPluginManager();
    pm.registerEvents(new PlayerListener(this, this.game_handler, this.score_keeper), this);
    pm.registerEvents(new EntityListener(), this);
	}
	
	public void onDisable()
	{
		
	}
}
