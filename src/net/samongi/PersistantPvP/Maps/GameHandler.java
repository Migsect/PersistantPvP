package net.samongi.PersistantPvP.Maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	  String loadouts_key = "loadouts";
	  List<String> lo_keys = new ArrayList<>(config.getConfig().getConfigurationSection(loadouts_key).getKeys(false));
	  for(String lo_key : lo_keys )
	  {
	    if(PersistantPvP.debug) PersistantPvP.logger.info("Parsing loadout with key: '" + lo_key + "'");
	    Loadout loadout = new Loadout(config, loadouts_key + "." + lo_key);
	    loadouts.put(lo_key, loadout);
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
		PersistantPvP.group.performAction((Player player) -> this.fetchLoadout().equipe(player));
		PersistantPvP.group.performAction((Player player) -> current_map.spawnPlayer(player));
	}
	
	public GameMap getCurrentMap(){return this.current_map;}
	public Loadout fetchLoadout(){
	  if(loadout_type.equals("SINGLE"))
	  {
	    return this.loadouts.get(single_loadout);
	  }
	  return null;
	}
	
}
