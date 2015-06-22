package net.samongi.PersistantPvP.Loadouts;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.samongi.PersistantPvP.PersistantPvP;
import net.samongi.SamongiLib.Configuration.ConfigAccessor;

public class LoadoutManager
{
  private final JavaPlugin plugin;
  
  private final Map<String, Map<String, Loadout>> loadouts = new HashMap<>();
  private final HashMap<UUID, Loadout> current_loadout = new HashMap<>();
  
  public LoadoutManager(JavaPlugin plugin)
  {
    this.plugin = plugin;
    this.parseLoadoutConfig();
  }
  
  public void parseLoadoutConfig()
  {
    PersistantPvP.debugLog("Parsing loadout configs");
    File loadout_folder = new File(plugin.getDataFolder(),"loadouts");
    if(!loadout_folder.exists() || !loadout_folder.isDirectory()) return;
    String[] loadout_files = loadout_folder.list();
    for(String file_name : loadout_files)
    {
      if(!file_name.endsWith(".yml")) return;
      ConfigAccessor loadout_file = new ConfigAccessor(plugin, loadout_folder, file_name);
      PersistantPvP.debugLog("Parsing loadout file: '" + file_name + "'");
      Loadout loadout = new Loadout(loadout_file, "loadout");
      
      // Getting all the tags the loadouts will be sorted into.
      List<String> sorting_tags = loadout.getSortingTags();
      // If the all key is not yet created, we are going to create it
      if(!loadouts.containsKey("all".toUpperCase())) loadouts.put("all".toUpperCase(), new HashMap<String, Loadout>());
      loadouts.get("all".toUpperCase()).put(file_name.replace(".yml", ""), loadout);
      for(String t : sorting_tags)
      {
        // if the tag is not yet added to the map, we'll make a new map to store under it.
        if(!loadouts.containsKey(t.toUpperCase())) loadouts.put(t.toUpperCase(), new HashMap<String, Loadout>());
        // sotring the loadout in the new map.
        loadouts.get(t.toUpperCase()).put(file_name.replace(".yml", ""), loadout);
      }
    }
  }
  
  public void setLoadout(String player, Loadout loadout){this.setLoadout(Bukkit.getPlayer(player), loadout);;}
  public void setLoadout(Player player, Loadout loadout){this.setLoadout(player.getUniqueId(), loadout);}
  public void setLoadout(UUID player, Loadout loadout){this.current_loadout.put(player, loadout);}
  
  public Loadout getLoadout(String player){return this.getLoadout(Bukkit.getPlayer(player));}
  public Loadout getLoadout(Player player){return this.getLoadout(player.getUniqueId());}
  public Loadout getLoadout(UUID player){return this.current_loadout.get(player);}
  
  public List<Loadout> getLoadouts(){return new ArrayList<Loadout>(this.loadouts.get("all".toUpperCase()).values());}
  public List<Loadout> getLoadouts(String sorting_tag){return new ArrayList<Loadout>(this.loadouts.get(sorting_tag.toUpperCase()).values());}
  
  public Loadout getRandomLoadout()
  {
    if(this.getLoadouts().size() == 0) return null;
    Random rand = new Random();
    return this.getLoadouts().get(rand.nextInt(this.getLoadouts().size()));
  }
  public Loadout getRandomLoadout(String sorting_tag)
  {
    if(this.getLoadouts(sorting_tag).size() == 0) return null;
    Random rand = new Random();
    return this.getLoadouts().get(rand.nextInt(this.getLoadouts(sorting_tag).size()));
  }
  
  public Loadout fetch()
  {
    List<Loadout> pool = this.getLoadouts();
    return this.getRandomLoadout(pool);
  }
  public Loadout fetch(String sorting_tag)
  {
    List<Loadout> pool = this.getLoadouts(sorting_tag);
    return this.getRandomLoadout(pool);
  }
  public Loadout fetchWeighted()
  {
    List<Loadout> pool = this.getLoadouts();
    return this.getRandomWeightedLoadout(pool);
  }
  public Loadout fetchWeighted(String sorting_tag)
  {
    List<Loadout> pool = this.getLoadouts(sorting_tag);
    return this.getRandomWeightedLoadout(pool);
  }
  private Loadout getRandomLoadout(List<Loadout> loadouts)
  {
    Random rand = new Random();
    return loadouts.get(rand.nextInt(loadouts.size()));
  }
  private Loadout getRandomWeightedLoadout(List<Loadout> loadouts)
  {
    Random rand = new Random();
    // Summing the total weights
    int total_weight = 0;
    for(Loadout l : loadouts) total_weight += l.getWeight();
    
    // Getting the random weight to grab.
    int random_weight = rand.nextInt(total_weight);
    // Scrambling the loadouts will add an extra layer of randomness.
    List<Loadout> scrambled_loadouts = LoadoutManager.scrambleList(loadouts);
    for(Loadout l : scrambled_loadouts)
    {
      // if our random_weight is less than the gotten weight, return it.
      if(random_weight < l.getWeight()) return l;
      random_weight -= l.getWeight(); // remove the wieght
    }
    return null;
  }
  private static List<Loadout> scrambleList(List<Loadout> list)
  {
    Random rand = new Random();
    List<Loadout> proxy_list = new ArrayList<>(list);
    List<Loadout> scrambled_list = new ArrayList<>();
    while(proxy_list.size() > 0)
    {
      int index = rand.nextInt(proxy_list.size());
      scrambled_list.add(proxy_list.get(index));
      proxy_list.remove(index);
    }
    return scrambled_list;
  }
  static public void sendTitle(Player player, Loadout loadout)
  {
    String command0 = "title " + player.getName() + " subtitle {text:\"" + loadout.getSubtitle() +"\", color:gray, italic:true}";
    String command1 = "title " + player.getName() + " title {text:\"You are now a " + loadout.getDisplayName() + "\"}";
    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command0);
    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command1);
  }
}
