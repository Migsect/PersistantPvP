package net.samongi.PersistantPvP.Maps;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.samongi.PersistantPvP.PersistantPvP;
import net.samongi.PersistantPvP.Players.Loadout;
import net.samongi.SamongiLib.Configuration.ConfigAccessor;

public class GameHandler
{
  PersistantPvP plugin;
  
  String loadout_type = "SINGLE";
  String single_loadout = "basic-loadout";
	Map<String,Loadout> loadouts = new HashMap<>();
	GameMap current_map;
	
	
	HashMap<String, GameMap> maps = new HashMap<String, GameMap>();
	
	public GameHandler(PersistantPvP plugin)
	{
		this.plugin = plugin;
		
		this.loadout_type = plugin.getConfig().getString("loadout-type", "SINGLE");
		this.single_loadout = plugin.getConfig().getString("single-loadout", "basic");
	}
	
	public void parseMapConfig(ConfigAccessor map_config)
	{
    if(PersistantPvP.debug) PersistantPvP.logger.info("Parsing map config");
		// Get keys and generate maps based off them
    List<String> keys = new ArrayList<>(map_config.getConfig().getConfigurationSection("maps").getKeys(false));
    for(String k : keys)
    {
      if(PersistantPvP.debug) PersistantPvP.logger.info("  Parsing map with key: '"+k+"'");
      GameMap map = new GameMap(map_config, k);
      this.maps.put(map.getTag(), map);
      if(PersistantPvP.debug) PersistantPvP.logger.info("    Adding '"+map.getTag()+"' to the maps list.");
    }
	}
	
	public void parseLoadoutConfig(ConfigAccessor config)
	{
    if(PersistantPvP.debug) PersistantPvP.logger.info("Parsing loadout config");
	  // Reading all the keys.
    /*
	  String loadouts_key = "loadouts";
	  List<String> lo_keys = new ArrayList<>(config.getConfig().getConfigurationSection(loadouts_key).getKeys(false));
	  for(String lo_key : lo_keys )
	  {
	    if(PersistantPvP.debug) PersistantPvP.logger.info("Parsing loadout with key: '" + lo_key + "'");
	    Loadout loadout = new Loadout(config, loadouts_key + "." + lo_key);
	    loadouts.put(lo_key, loadout);
	  }
	  */
	  File loadout_folder = new File(plugin.getDataFolder(),"loadouts");
	  if(!loadout_folder.exists() || !loadout_folder.isDirectory()) return;
	  String[] loadout_files = loadout_folder.list();
	  for(String file_name : loadout_files)
	  {
	    if(!file_name.endsWith(".yml")) return;
	    ConfigAccessor loadout_file = new ConfigAccessor(plugin, loadout_folder, file_name);
	    if(PersistantPvP.debug) PersistantPvP.logger.info("Parsing loadout file: '" + file_name + "'");
	    Loadout loadout = new Loadout(loadout_file, "loadout");
      loadouts.put(file_name.replace(".yml", ""), loadout);
	  }
	}
	public void praseLoadoutRandomConfig(ConfigAccessor config)
	{
	  
	}
	
	public void switch_maps(String map)
	{
		if(!this.maps.containsKey(map)) return; // Tell them this.
		this.current_map = this.maps.get(map);
		// Teleport the players to the new map.
		PersistantPvP.group.performAction((Player player) -> {
		  Loadout lo = this.fetchLoadout();
		  lo.equipe(player);
		  current_map.spawnPlayer(player);
		  String command0 = "title " + player.getName() + " subtitle {text:\"" + lo.getSubtitle() +"\", color:gray, italic:true}";
	    String command1 = "title " + player.getName() + " title {text:\"You are now a " + lo.getDisplayName() + "\"}";
	    plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), command0);
	    plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), command1);
		});
	}
	
	public GameMap getCurrentMap(){return this.current_map;}
	/**fetches a loadout depending on the loadouts.
	 * 
	 * @return
	 */
	public Loadout fetchLoadout(){
	  if(loadout_type.equals("SINGLE"))
	  {
	    return this.loadouts.get(single_loadout);
	  }
	  if(loadout_type.equals("RANDOM"))
	  {
	    Random rand = new Random();
	    List<Loadout> rand_loadouts = new ArrayList<Loadout>(loadouts.values());
	    int total_weight = 0;
	    for(Loadout l : rand_loadouts) total_weight += l.getWeight();
	    int random_weight = rand.nextInt(total_weight);
	    for(Loadout l : rand_loadouts)
	    {
	      // if our random_weight is less than the gotten weight, return it.
	      if(random_weight < l.getWeight()) return l;
	      random_weight -= l.getWeight(); // remove the wieght
	      
	    }
	  }
	  return null;
	}
}
