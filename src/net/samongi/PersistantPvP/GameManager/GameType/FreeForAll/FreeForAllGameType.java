package net.samongi.PersistantPvP.GameManager.GameType.FreeForAll;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.samongi.PersistantPvP.PersistantPvP;
import net.samongi.PersistantPvP.GameManager.GameManager;
import net.samongi.PersistantPvP.GameManager.Events.MapChangeEvent;
import net.samongi.PersistantPvP.GameManager.GameType.GameType;
import net.samongi.PersistantPvP.Loadouts.Loadout;
import net.samongi.PersistantPvP.Loadouts.LoadoutManager;
import net.samongi.PersistantPvP.Score.DamageRecord;
import net.samongi.PersistantPvP.Score.StatKeeper;
import net.samongi.PersistantPvP.Score.StatRecord;
import net.samongi.SamongiLib.Configuration.ConfigAccessor;
import net.samongi.SamongiLib.Items.ItemUtil;
import net.samongi.SamongiLib.Maps.Spawn.Spawn;
import net.samongi.SamongiLib.Player.PlayerUtil;

public class FreeForAllGameType implements GameType
{
  private final PersistantPvP plugin;
  private final GameManager game_manager;
  private final FreeForAllScoreKeeper score_keeper;
  private final StatKeeper stat_keeper;

  // Loadout variables
  private final String display_name;
  private final String loadout_type;
  private final String single_loadout;
  
  // Kill rewards
  private HashMap<ItemStack, Integer> rewards = new HashMap<>();
  
  public FreeForAllGameType(PersistantPvP plugin, GameManager game_manager, StatKeeper stat_keeper, ConfigurationSection section)
  {
    this.plugin = plugin;
    this.game_manager = game_manager;
    this.stat_keeper = stat_keeper;
    this.score_keeper = new FreeForAllScoreKeeper();
    
    this.single_loadout = section.getString(".single-loadout");
    if(this.single_loadout == null) this.loadout_type = "RANDOM";
    else this.loadout_type = section.getString(".loadout-type", "SINGLE");
    
    this.display_name = section.getString("display-name", "DEFAULT_DISPLAY_NAME");
    
    // rewards-config handling:
    File rewards_config_file = new File(plugin.getDataFolder(),"rewards.yml");
    ConfigAccessor rewards_config = new ConfigAccessor(plugin, "rewards.yml");
    if(!rewards_config_file.exists())
    {
      PersistantPvP.log("Found no rewards config file, copying over defaults...");
      rewards_config.getConfig().options().copyDefaults(true);
      rewards_config.saveConfig();
    }
    this.parseRewardsConfig(rewards_config);
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
  @Override
  public void onPlayerDeath(PlayerDeathEvent event)
  {
    event.setKeepInventory(true);
    // Check to see if the player was killed by an entity.
    Player death_player = event.getEntity();
    DamageRecord dmg_record = stat_keeper.getDamageRecord(death_player);

    Player high_dmg_player = dmg_record.getHighestDamage();
    if(high_dmg_player != null) PersistantPvP.debugLog("Found Highest Damage Player to be: '" + high_dmg_player.getName() + "' for '" + death_player.getName() + "'");
    
    if(high_dmg_player != null) high_dmg_player.getInventory().addItem(this.fetchReward());
    score_keeper.awardPoints(dmg_record);
    
    
    Player kill_player = dmg_record.getLastDamager();
    if(kill_player != null)
    {
      Loadout death_loadout = game_manager.getLoadoutManager().getLoadout(death_player);
      Loadout kill_loadout = game_manager.getLoadoutManager().getLoadout(kill_player);
      StatRecord death_stat_record = stat_keeper.getRecord(death_player);
      StatRecord kill_stat_record = stat_keeper.getRecord(kill_player);
      death_stat_record.incrementDeath(kill_player, death_loadout); // killed by kill_player while using death_loadout
      kill_stat_record.incrementKill(death_player, kill_loadout); // killed death_player using kill_loadout
      
      PersistantPvP.announcer.onKill(kill_player);
    }
    BukkitRunnable task = new BukkitRunnable()
    {
      @Override
      public void run(){death_player.spigot().respawn(); }
    };
    task.runTaskLater(plugin, 1);
    
  }

  @Override
  public void onPlayerRespawn(PlayerRespawnEvent event)
  {
    this.spawnPlayer(event.getPlayer()); 
  }

  @Override
  public void onPlayerJoin(PlayerJoinEvent event)
  {
    this.spawnPlayer(event.getPlayer()); 
  }

  @Override
  public void onPlayerQuit(PlayerQuitEvent event)
  {
    DamageRecord record = stat_keeper.getDamageRecord(event.getPlayer());
    score_keeper.awardPoints(record);
  }

  @Override
  public void onPlayerDropItem(PlayerDropItemEvent event)
  {
    event.setCancelled(true); // we're just going to cancel it because we don't want people dropping items.
    // We could also use this for abilities if we wanted to.
  }
  @Override
  public void onPlayerPickupItem(PlayerPickupItemEvent event){}

  @Override
  public void displayScoreboard(Player player)
  {
    this.score_keeper.setScoreboard(player);
  }

  @Override
  public void spawnPlayer(Player player)
  {
    if(player == null) return;
    
    Loadout loadout = this.fetchLoadout();
    Loadout old_loadout = this.game_manager.getLoadoutManager().getLoadout(player);
    
    Spawn spawn_loc = this.game_manager.getMapManager().getRandomSpawn();
    if(spawn_loc == null) return;
    
    BukkitRunnable task = new BukkitRunnable(){
      @Override
      public void run()
      {
        PersistantPvP.debugLog("Setting '" + player.getName() + "' to loadout '" + loadout.getDisplayName() + "'");
        game_manager.getLoadoutManager().setLoadout(player, loadout);
        PersistantPvP.debugLog("Checking if loadout was set: '" +  old_loadout);
        loadout.equipe(player);

        PersistantPvP.debugLog("Spawning player " + player.getName());
        spawn_loc.spawn(player);
        
        // sending the title
        LoadoutManager.sendTitle(player, loadout);
        // removing all the arrows.
        PlayerUtil.removeArrows(player);
        
        if(old_loadout != null) stat_keeper.sendStatReport(player, old_loadout);
      }
    };
    task.runTask(plugin);
    
  }

  @Override
  public void onTypeEnable()
  {
    this.spawnAllPlayers();
  }

  @Override
  public void onTypeDisable()
  {
    
  }

  @Override
  public void onMapChange(MapChangeEvent event)
  {
    this.spawnAllPlayers();
  }
  
  public void spawnAllPlayers()
  {
    PersistantPvP.group.performAction((Player p)->this.spawnPlayer(p));
  }
  
  private Loadout fetchLoadout()
  {
    if(loadout_type.equals("SINGLE"))
    {
      return Loadout.getLoadout(this.single_loadout);
    }
    if(loadout_type.equals("RANDOM"))
    {
      return this.game_manager.getLoadoutManager().fetchWeighted();
    }
    return null;
  }
  private ItemStack fetchReward()
  {
    Random rand = new Random();
    List<ItemStack> items = new ArrayList<>(this.rewards.keySet());
    return items.get(rand.nextInt(items.size()));
  }

  @Override
  public String getDisplayName(){return this.display_name;}

}
