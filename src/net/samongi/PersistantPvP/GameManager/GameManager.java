package net.samongi.PersistantPvP.GameManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.samongi.PersistantPvP.PersistantPvP;
import net.samongi.PersistantPvP.Maps.GameMap;
import net.samongi.PersistantPvP.Players.Loadout;
import net.samongi.PersistantPvP.Score.StatKeeper;
import net.samongi.PersistantPvP.Score.StatRecord;
import net.samongi.SamongiLib.Configuration.ConfigAccessor;
import net.samongi.SamongiLib.Items.ItemUtil;
import net.samongi.SamongiLib.Player.PlayerUtil;

public class GameManager
{
  PersistantPvP plugin;
  
  // Loadout variables
  private String loadout_type = "SINGLE";
  private String single_loadout = "golemite";
  private Map<String, Loadout> loadouts = new HashMap<>();
	
	// Current_map is the map that is currently loadout.
  private GameMap current_map;
	// Maps will consist of all gamemaps that can be played on.
  private HashMap<String, GameMap> maps = new HashMap<String, GameMap>();
	
	// Current_loadout will track the last loadout assigned to a player.
  private HashMap<String, Loadout> current_loadout = new HashMap<>();
	
	// Kill rewards
  private HashMap<ItemStack, Integer> rewards = new HashMap<>();
	
	// Keepers
  private StatKeeper keeper;
	
	public GameManager(PersistantPvP plugin, StatKeeper keeper)
	{
		this.plugin = plugin;
		
		this.loadout_type = plugin.getConfig().getString("loadout-type", "SINGLE");
		this.single_loadout = plugin.getConfig().getString("single-loadout", "basic");
		this.keeper = keeper;
	}
	
	public void parseMapConfig(ConfigAccessor config)
	{
    PersistantPvP.debugLog("Parsing map config");
		// Get keys and generate maps based off them
    List<String> keys = new ArrayList<>(config.getConfig().getConfigurationSection("maps").getKeys(false));
    for(String k : keys)
    {
      PersistantPvP.debugLog("  Parsing map with key: '"+k+"'");
      GameMap map = new GameMap(config, k);
      this.maps.put(map.getTag(), map);
      PersistantPvP.debugLog("    Adding '"+map.getTag()+"' to the maps list.");
    }
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
      loadouts.put(file_name.replace(".yml", ""), loadout);
	  }
	}
	
	public void parseRewardsConfig(ConfigAccessor config)
	{
	  PersistantPvP.debugLog("Parsing map config");
	  List<String> keys = new ArrayList<>(config.getConfig().getConfigurationSection("rewards").getKeys(false));
	  for(String k : keys)
    {
      ItemStack reward_item = ItemUtil.getConfigItemStack(config, "rewards."+k);
      this.rewards.put(reward_item, 100);
    }
	}
	
	public void switch_maps(String map)
	{
		if(!this.maps.containsKey(map)) return; // Tell them this.
		this.current_map = this.maps.get(map);
		// Teleport the players to the new map.
		PersistantPvP.group.performAction((Player player) -> {
		  spawnPlayer(player);
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
	public ItemStack fetchReward()
	{
	  Random rand = new Random();
    List<ItemStack> items = new ArrayList<>(this.rewards.keySet());
    return items.get(rand.nextInt(items.size()));
	}
	
	public void spawnPlayer(Player player)
  {
    BukkitRunnable task = new BukkitRunnable(){
      @Override
      public void run()
      {
        Loadout loadout = fetchLoadout();
        Loadout old_loadout = getCurrentLoadout(player);
        PersistantPvP.debugLog("Setting '" + player.getName() + "' to loadout '" + loadout.getDisplayName() + "'");
        setCurrentLoadout(player, loadout);
        PersistantPvP.debugLog("Checking if loadout was set: '" +  getCurrentLoadout(player));
        loadout.equipe(player);

        PersistantPvP.debugLog("Spawning player " + player.getName());
        getCurrentMap().spawnPlayer(player);
        
        // sending the title
        sendTitle(player, loadout);
        // removing all the arrows.
        PlayerUtil.removeArrows(player);
        
        sendStatReport(player, old_loadout);
      }
    };
    task.runTask(plugin);
  }
	
	public void sendStatReport(Player player, Loadout loadout)
  {
    StatRecord record = keeper.getRecord(player);
    player.sendMessage(ChatColor.AQUA + "Lifetime Report:");
    player.sendMessage(ChatColor.WHITE + "- " + ChatColor.YELLOW + "Kills - Deaths: " + ChatColor.BOLD + ChatColor.GREEN + record.getTotalKills() + " - " + record.getTotalDeaths());
    double total_kd = record.getTotalKillDeathRatio();
    String total_kd_str = "";
    if(total_kd < 0) total_kd_str = "N/A";
    else total_kd_str = String.format("%.2f", total_kd);
    player.sendMessage(ChatColor.WHITE + "- " + ChatColor.YELLOW + "K/D Ratio: " + ChatColor.BOLD + ChatColor.GREEN + total_kd_str);
    player.sendMessage(ChatColor.WHITE + "- " + ChatColor.YELLOW + "Last Killstreak / Killstreak High: " + ChatColor.BOLD + ChatColor.GREEN + record.getPriorStreak() + " / " + record.getLargestStreak());
    if(loadout != null)
    {
      player.sendMessage(ChatColor.AQUA + "Loadout '" + ChatColor.BLUE + loadout.getDisplayName() + ChatColor.AQUA + "' Report:");
      player.sendMessage(ChatColor.WHITE + "- " + ChatColor.YELLOW + "Kill - Deaths: " + ChatColor.BOLD + ChatColor.GREEN + record.getLoadoutKills(loadout) + " - " + record.getLoadoutDeaths(loadout));
      double loadout_kd = record.getLoadoutKillDeathRatio(loadout);
      String loadout_kd_str = "";
      if(loadout_kd < 0) loadout_kd_str = "N/A";
      else loadout_kd_str = String.format("%.2f", loadout_kd);
      player.sendMessage(ChatColor.WHITE + "- " + ChatColor.YELLOW + "K/D Ration: " + ChatColor.BOLD + ChatColor.GREEN + loadout_kd_str);
      player.sendMessage(ChatColor.WHITE + "- " + ChatColor.YELLOW + "Last Killstreak / Killstreak High: " + ChatColor.BOLD + ChatColor.GREEN + record.getPriorStreak() + " / " + record.getLargestStreak(loadout));
    }
  }
	private void sendTitle(Player player, Loadout lo)
  {
    String command0 = "title " + player.getName() + " subtitle {text:\"" + lo.getSubtitle() +"\", color:gray, italic:true}";
    String command1 = "title " + player.getName() + " title {text:\"You are now a " + lo.getDisplayName() + "\"}";
    plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), command0);
    plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), command1);
  }
	
	public void setCurrentLoadout(Player player, Loadout loadout){this.current_loadout.put(player.getName(), loadout);}
	public Loadout getCurrentLoadout(Player player){return this.current_loadout.get(player.getName());}
	
}
