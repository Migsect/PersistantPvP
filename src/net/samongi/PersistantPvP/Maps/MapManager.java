package net.samongi.PersistantPvP.Maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import net.samongi.PersistantPvP.PersistantPvP;
import net.samongi.PersistantPvP.GameManager.Events.MapChangeEvent;
import net.samongi.SamongiLib.Maps.MapData;
import net.samongi.SamongiLib.Maps.Spawn.Spawn;

public class MapManager
{
  Map<String, MapData> maps = new HashMap<>();
  MapData current_map = null;
  
  public MapManager()
  {
    List<MapData> map_list = MapData.getAllMapData();
    for(MapData m : map_list)
    {
      PersistantPvP.debugLog("[MAPMANAGER] Added map with tag: " + m.getTag());
      this.maps.put(m.getTag(), m);
    }
    
    // Setting up the initial map:
    List<String> map_keys = new ArrayList<>(this.getMapKeys());
    Random rand = new Random();
    String map_key = map_keys.get(rand.nextInt(map_keys.size()));
    this.setCurrentMap(map_key);
  }
  public Set<String> getMapKeys(){return this.maps.keySet();}
  public MapData getMap(String key){return this.maps.get(key);}
  public boolean mapExists(String key){return this.maps.containsKey(key);}
  public boolean hasCurrentMap(){return this.current_map != null;}
  public MapData getCurrentMap(){return this.current_map;}
  public boolean setCurrentMap(String tag)
  {
    MapData map = this.maps.get(tag);
    if(map == null) return false;
    
    MapChangeEvent event = new MapChangeEvent(this.current_map, map);
    this.current_map = map;
    Bukkit.getServer().getPluginManager().callEvent(event);
    
    World world = this.current_map.getWorld();
    world.setTime(this.current_map.getTime());
    
    return true;
  }
  
  public Spawn getRandomSpawn()
  {
    Random rand = new Random();
    if(!this.hasCurrentMap()) return null;
    return current_map.getSpawns().get(rand.nextInt(current_map.getSpawns().size()));
  }
  public Spawn getRandomSpawn(String tag)
  {
    Random rand = new Random();
    if(!this.hasCurrentMap()) return null;
    return current_map.getSpawns(tag).get(rand.nextInt(current_map.getSpawns(tag).size()));
  }
  public Spawn getRandomSpawnWithinRange(List<Player> players, double min, double max)
  {
    if(!this.hasCurrentMap()) return null;
    
    List<Spawn> spawns = current_map.getSpawns();
    List<Spawn> potential = new ArrayList<>();
    for(Spawn s : spawns) if(!s.isWithinRange(players, min) && s.isWithinRange(players, max)) potential.add(s);
    if(potential.size() == 0) return null;
    
    Random rand = new Random();
    return potential.get(rand.nextInt(potential.size()));
  }
  public Spawn getRandomSpawnWithinRange(String tag, List<Player> players, double min, double max)
  {
    if(!this.hasCurrentMap()) return null;
    
    List<Spawn> spawns = current_map.getSpawns(tag);
    List<Spawn> potential = new ArrayList<>();
    for(Spawn s : spawns) if(!s.isWithinRange(players, min) && s.isWithinRange(players, max)) potential.add(s);
    if(potential.size() == 0) return null;
    
    Random rand = new Random();
    return potential.get(rand.nextInt(potential.size()));
  }
}
