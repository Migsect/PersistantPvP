package net.samongi.PersistantPvP.Score;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.samongi.PersistantPvP.PersistantPvP;
import net.samongi.PersistantPvP.Loadouts.Loadout;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class StatKeeper
{
  private final Map<UUID, StatRecord> stat_records = new HashMap<>();
  private final Map<UUID, DamageRecord> damage_records = new HashMap<>();
  private final File record_directory;
  
  public StatKeeper(JavaPlugin plugin)
  {
    this.record_directory = new File(plugin.getDataFolder(),"stat_records");
    this.record_directory.mkdirs();
  }
  

  //                       //
  //     EVENT ACTIONS     //
  //                       //
  public void onPlayerJoin(PlayerJoinEvent event)
  {
    this.loadRecord(event.getPlayer());
  }
  public void onPlayerQuit(PlayerQuitEvent event)
  {
    this.saveRecord(event.getPlayer());
  }
  
  //                     //
  // STAT RECORD SECTION //
  //                     //
  public void loadRecord(Player player)
  {
    UUID player_uuid = player.getUniqueId();
    PersistantPvP.debugLog("Loading Stat Record for " + player.getName() + " - UUID: " + player.getUniqueId());
    File file = new File(record_directory, player_uuid.toString() + ".record");
    PersistantPvP.debugLog("  Loading: " + file.getAbsolutePath());
    StatRecord record = StatRecord.loadRecord(file, player_uuid);
    this.stat_records.put(player_uuid, record);
  }
  
  public void saveRecord(Player player)
  {
    UUID player_uuid = player.getUniqueId();
    StatRecord record = this.stat_records.get(player_uuid);
    if(record == null) PersistantPvP.debugLog("Record was found to be null for: " + player.getName() + " - UUID: " + player.getUniqueId());
    if(record == null) record = new StatRecord(player_uuid);
    PersistantPvP.debugLog("Saving Stat Record for " + player.getName() + " - UUID: " + player.getUniqueId());
    File file = new File(record_directory, player_uuid.toString() + ".record");
    PersistantPvP.debugLog("  Saving to: " + file.getAbsolutePath());
    record.saveRecord(file);
  }
  
  public StatRecord getRecord(Player player){return this.stat_records.get(player.getUniqueId());}
  
  public void resetRecord(Player player)
  {
    UUID player_uuid = player.getUniqueId();
    
    File save_file = new File(record_directory, player_uuid.toString() + ".record");
    if(!save_file.exists()) try{save_file.createNewFile();}
    catch (IOException e){e.printStackTrace();}
    
    if(save_file.exists()) save_file.delete();
    stat_records.put(player_uuid, new StatRecord(player_uuid));
    
    stat_records.remove(player_uuid);
  }
  public void resetAllRecords()
  {
    // Resetting all current players.
    for(UUID id : stat_records.keySet())
    {
      stat_records.put(id, new StatRecord(id));
    }
    // Deleting all the saved records.
    String[] saved_records = record_directory.list();
    for(String file : saved_records) if(file.endsWith(".record")) (new File(record_directory, file)).delete();
  }
  
  public void saveAllPlayers()
  {
    PersistantPvP.group.performAction((Player player) -> {
      this.saveRecord(player);
    });
  }
  public void loadAllPlayers()
  {
    PersistantPvP.group.performAction((Player player) -> {
      this.loadRecord(player);
    });
  }
  
  //                       //
  // Damage RECORD SECTION //
  //                       //
  /**Returns the specified damage record for the player
   * 
   * @param player The player the damage record is for
   * @return The damage record
   */
  public DamageRecord getDamageRecord(UUID player)
  {
    if(!this.damage_records.containsKey(player)) this.damage_records.put(player, new DamageRecord(Bukkit.getPlayer(player)));
    return this.damage_records.get(player);
  }
  /**Returns the specified damage record for the player
   * 
   * @param player The player the damage record is for
   * @return The damage record
   */
  public DamageRecord getDamageRecord(Player player){return this.getDamageRecord(player.getUniqueId());}

  /**Returns the specified damage record for the player
   * 
   * @param player The player the damage record is for
   * @return The damage record
   */
  public DamageRecord getDamageRecord(String player){return this.getDamageRecord(Bukkit.getPlayer(player));}
  
  /**Removes the player from all damage records.
   * 
   * @param player The player to remove
   */
  public void removeFromDamageRecords(UUID player)
  {
    for(UUID p : damage_records.keySet()){this.getDamageRecord(p).resetDamage(player);}
  }
  /**Removes the player from all damage records.
   * 
   * @param player The player to remove
   */
  public void removeFromDamageRecords(Player player){this.removeFromDamageRecords(player.getUniqueId());}
  /**Removes the player from all damage records.
   * 
   * @param player The player to remove
   */
  public void removeFromDamageRecords(String player){this.removeFromDamageRecords(Bukkit.getPlayer(player));}

  
  /**Removes the player from all the damage_records of all other players.
   * 
   * @param player
   */
  public void removeDamageRecord(Player player){this.damage_records.remove(player.getUniqueId());}
  /**Removes the player from all the damage_records of all other players.
   * 
   * @param player
   */
  public void removeDamageRecord(String player){this.damage_records.remove(Bukkit.getPlayer(player));}
  /**Removes the player from all the damage_records of all other players.
   * 
   * @param player
   */
  public void removeDamageRecord(UUID player){this.damage_records.remove(player);}
  
  public void sendStatReport(Player player, Loadout loadout)
  {
    StatRecord record = this.getRecord(player);
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
}
