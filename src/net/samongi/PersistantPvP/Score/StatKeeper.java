package net.samongi.PersistantPvP.Score;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import net.samongi.PersistantPvP.PersistantPvP;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class StatKeeper
{
  private final HashMap<UUID, StatRecord> records = new HashMap<>();
  private final File record_directory;
  
  public StatKeeper(JavaPlugin plugin)
  {
    this.record_directory = new File(plugin.getDataFolder(),"stat_records");
    this.record_directory.mkdirs();
  }
  
  public void loadRecord(Player player)
  {
    UUID player_uuid = player.getUniqueId();
    PersistantPvP.debugLog("Loading Stat Record for " + player.getName() + " - UUID: " + player.getUniqueId());
    File file = new File(record_directory, player_uuid.toString() + ".record");
    PersistantPvP.debugLog("  Loading: " + file.getAbsolutePath());
    StatRecord record = StatRecord.loadRecord(file, player_uuid);
    this.records.put(player_uuid, record);
  }
  
  public void saveRecord(Player player)
  {
    UUID player_uuid = player.getUniqueId();
    StatRecord record = this.records.get(player_uuid);
    if(record == null) PersistantPvP.debugLog("Record was found to be null for: " + player.getName() + " - UUID: " + player.getUniqueId());
    if(record == null) record = new StatRecord(player_uuid);
    PersistantPvP.debugLog("Saving Stat Record for " + player.getName() + " - UUID: " + player.getUniqueId());
    File file = new File(record_directory, player_uuid.toString() + ".record");
    PersistantPvP.debugLog("  Saving to: " + file.getAbsolutePath());
    record.saveRecord(file);
  }
  
  public StatRecord getRecord(Player player){return this.records.get(player.getUniqueId());}
  
  public void resetRecord(Player player)
  {
    UUID player_uuid = player.getUniqueId();
    
    File save_file = new File(record_directory, player_uuid.toString() + ".record");
    if(!save_file.exists()) try{save_file.createNewFile();}
    catch (IOException e){e.printStackTrace();}
    
    if(save_file.exists()) save_file.delete();
    records.put(player_uuid, new StatRecord(player_uuid));
    
    records.remove(player_uuid);
  }
  public void resetAllRecords()
  {
    // Resetting all current players.
    for(UUID id : records.keySet())
    {
      records.put(id, new StatRecord(id));
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
}
