package net.samongi.PersistantPvP.Maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import net.samongi.PersistantPvP.PersistantPvP;
import net.samongi.PersistantPvP.Maps.Spawn;
import net.samongi.PersistantPvP.Maps.SpawnPoint;
import net.samongi.SamongiLib.Configuration.ConfigAccessor;
import net.samongi.SamongiLib.Utilities.StringUtilities;

public class GameMap
{
  private String tag;
  private String display_name;
  
  private World world;
  private Location center;
  private double radius;
  private double bottom;
  
  private List<String> supported_games;
  
  private Map<String, List<Spawn>> spawns = new HashMap<>();
  
  
  public GameMap(ConfigAccessor config, String tag)
  {
    this.tag = config.getConfig().getString("maps."+tag+".tag");
    if(PersistantPvP.debug) PersistantPvP.logger.info(" Parsed: tag with '"+ this.tag +"' as it's value");
    if(this.tag == null) PersistantPvP.logger.info("GAME-RUNNER - CONFIG ERROR in maps.yml for key '"+tag+"', Bad or no tag field.");
    if(this.tag == null) return;
    
    this.display_name = config.getConfig().getString("maps."+tag+".display-name");
    if(PersistantPvP.debug) PersistantPvP.logger.info(" Parsed: display_name with '"+ this.display_name +"' as it's value");
    if(this.display_name == null) PersistantPvP.logger.info("GAME-RUNNER - CONFIG ERROR in maps.yml for key '"+tag+"', Bad or no displayname field.");
    if(this.display_name == null) return;
    
    this.world = Bukkit.getWorld(config.getConfig().getString("maps."+tag+".world"));
    if(this.world == null) PersistantPvP.logger.info("GAME-RUNNER - CONFIG ERROR in maps.yml for key '"+tag+"', Bad or no world field.");
    if(PersistantPvP.debug) PersistantPvP.logger.info(" Parsed: display_name with '"+ this.world.getName() +"' as it's value");
    if(this.world == null) return;
    
    this.supported_games = config.getConfig().getStringList("maps."+tag+".games");
    if(PersistantPvP.debug) PersistantPvP.logger.info(" Parsed: first supported game with '"+ supported_games.get(0) + "' as it's value");
    if(this.supported_games.size() < 1) PersistantPvP.logger.info("GAME-RUNNER - CONFIG ERROR in maps.yml for key '"+tag+"', Bad or no games fields.");
    if(this.supported_games.size() < 1) return;
      
    List<Double> center_coords = StringUtilities.extractNumbers(config.getConfig().getString("maps."+tag+".center"));
    if(center_coords.size() <= 2) PersistantPvP.logger.info("GAME-RUNNER - CONFIG ERROR in maps.yml for key '"+tag+"', Incomplete or not listed center coord.");
    if(center_coords.size() <= 2) return;
    center = new Location(this.world, center_coords.get(0),center_coords.get(1), center_coords.get(2));
    
    List<Double> radius_amount = StringUtilities.extractNumbers(config.getConfig().getString("maps."+tag+".radius"));
    if(radius_amount.size() <= 0)PersistantPvP.logger.info("GAME-RUNNER - CONFIG ERROR in maps.yml for key '"+tag+"', Incomplete or not listed radius.");
    if(radius_amount.size() <= 0) return;
    radius = radius_amount.get(0);
    
    List<Double> map_bottom_amount = StringUtilities.extractNumbers(config.getConfig().getString("maps."+tag+".map-bottom"));
    if(map_bottom_amount.size() <= 0) PersistantPvP.logger.info("GAME-RUNNER - CONFIG ERROR in maps.yml for key '"+tag+"', Incomplete or not listed map bottom.");
    if(map_bottom_amount.size() <= 0) return;
    radius = map_bottom_amount.get(0);
    
    List<String> keys = new ArrayList<>(config.getConfig().getConfigurationSection("maps."+tag+".spawnpoints").getKeys(false));
    for(String k : keys)
    {
      // Getting the list of strings (spawnpoints);
      List<String> spawnpoints = config.getConfig().getStringList("maps."+tag+".spawnpoints."+k);
      for(String s : spawnpoints)
      {
        List<Double> point_coords = StringUtilities.extractNumbers(s);
        if(center_coords.size() <= 2) PersistantPvP.logger.info("GAME-RUNNER - CONFIG ERROR in maps.yml for key '"+tag+"', Incomplete or not listed spawn coord.");
        if(center_coords.size() <= 2) return;
        Location point = new Location(this.world, point_coords.get(0),point_coords.get(1), point_coords.get(2));
        if(!spawns.containsKey(k)) spawns.put(k, new ArrayList<Spawn>()); // making a new arraylist if one doesn't exist.
        spawns.get(k).add(new SpawnPoint(point)); // Adding the spawn to it's list.
      }
    }
  }
  /**Returns the tag that identifies this map.
   * 
   * @return The tag
   */
  public String getTag()
  {
    return this.tag;
  }
  
  /**Returns a list of spawns based of the key.  Each spawn is underneath one or
   * or more different keys, all maps should have 'default' key spwns.
   * 
   * @param key The key to get the map.
   * @return A list of spawns under the key.
   */
  public List<Spawn> getSpawns(String key)
  {
    return spawns.get(key);
  }
  
  /**Returns the center of the map
   * 
   * @return The center of the map
   */
  public Location getCenter()
  {
    return this.center;
  }
  
  /**Returns the radius of the map (the radius will stop playr movement outward.
   * Any movement 5 meters outside will teleport the player to the center of the map.
   * 
   * @return The playable radius of the map
   */
  public double getRadius()
  {
    return this.radius;
  }
  
  /**Returns the bottom of the mpa (y-level) that players will either be teleported back to the center from (if creative or spectator)
   * or will be killed (survival or adventure mode)
   * 
   * @return The bottom of the map.
   */
  public double getBottom()
  {
    return this.bottom;
  }
  
  public void spawnPlayer(Player player)
  {
  	List<Spawn> def_spawns = this.spawns.get("default");
  	Random rand = new Random();
  	Spawn s = def_spawns.get(rand.nextInt(def_spawns.size()));
  	s.spawn(player);
  }
  public void spawnPlayer(Player player, String type)
  {
  	List<Spawn> type_spawns = this.spawns.get(type);
  	Random rand = new Random();
  	Spawn s = type_spawns.get(rand.nextInt(type_spawns.size()));
  	s.spawn(player);
  }
  
}
