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
    this.getConfig().options().copyDefaults(true);
    this.saveConfig();
    debug = this.getConfig().getBoolean("debug");
    
    // map-config handling;
    ConfigAccessor map_config = new ConfigAccessor(this, "maps.yml");
    map_config.getConfig().options().copyDefaults(true);
    map_config.saveConfig();
    
    // Game Handler handling
    this.game_handler = new GameHandler();
    game_handler.parseMapConfig(map_config);
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
