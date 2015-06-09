package net.samongi.PersistantPvP.Score;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.samongi.PersistantPvP.PersistantPvP;
import net.samongi.PersistantPvP.Players.Loadout;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class StatRecord implements Serializable
{
  private static final long serialVersionUID = 1839940873772376270L;
  
  final private UUID player;
  private int total_kills = 0;
  private int total_deaths = 0;
  final private Map<UUID, Integer> player_kills = new HashMap<>(); // Kills of other players.
  final private Map<UUID, Integer> player_deaths = new HashMap<>(); // Deaths from other players.
  final private Map<String, Integer> loadout_kills = new HashMap<>(); // Kills with a loadout
  final private Map<String, Integer> loadout_deaths = new HashMap<>(); // Deaths with a loadout
  
  private int chain_kill_threshold = 140; // 4 seconds or 80 ticks
  private int chain_kill_count = 0;
  private int last_kill_time = 0;
  
  private int current_kills = 0;
  private int last_streak = 0;
  private int largest_streak = 0;
  private Map<String, Integer> loadout_streak = new HashMap<>();
  
  File file;
  
  public StatRecord(UUID player)
  {
    this.player = player;
  }
  
  /**Increments kills
   * 
   * @param player_uuid The UUID of the player that got killed by this player
   * @param loadout The current loadout of this player in string form.
   */
  public void incrementKill(UUID player_uuid, Loadout loadout){this.addKill(player, loadout, 1);}
  /**Increments kills
   * 
   * @param player_uuid The UUID of the player that got killed by this player
   * @param loadout The current loadout of this player in string form.
   */
  public void incrementKill(Player player, Loadout loadout){this.addKill(player.getUniqueId(), loadout, 1);}
  /**Adds a kill to the stats.
   * 
   * @param player The UUID of the player that got killed by this player
   * @param loadout The current loadout of this player in string form.
   * @param amount The amount to increment
   */
  public void addKill(UUID player_uuid, Loadout loadout, int amount)
  {
    this.total_kills += amount;
    this.current_kills += amount;
    
    if(!this.player_kills.containsKey(player_uuid)) this.player_kills.put(player_uuid, 0);
    this.player_kills.put(player, this.player_kills.get(player_uuid) + amount);
    
    if(!this.loadout_kills.containsKey(loadout.getDisplayName())) this.loadout_kills.put(loadout.getDisplayName(), 0);
    this.loadout_kills.put(loadout.getDisplayName(), this.loadout_kills.get(loadout.getDisplayName()) + amount);
    
    // Get the last time a kill was made
    int last_kill_t = this.last_kill_time;
    // get the current ticks and set it
    int current_t = (int) (System.currentTimeMillis() / 50);
    this.last_kill_time = current_t;
    // check to see if current - last is less than the threshold:
    if(current_t - last_kill_t < chain_kill_threshold)
    {
      // Adding one if it is less
      chain_kill_count++;
    }
    // else reset it to just 1.
    else chain_kill_count = 1; // they only have one kill  now
  }
  /**Adds a kill to the stats.
   * 
   * @param player Player that got killed by this player
   * @param loadout The current loadout of this player in string form.
   * @param amount The amount to increment
   */
  public void addKill(Player player, Loadout loadout, int amount){this.addKill(player.getUniqueId(), loadout, amount);}
  
  
  /**Increments deaths
   * 
   * @param player_uuid The UUID of the player that got deathed by this player
   * @param loadout The current loadout of this player in string form.
   */
  public void incrementDeath(UUID player_uuid, Loadout loadout){this.addDeath(player, loadout, 1);}
  /**Increments deaths
   * 
   * @param player_uuid The UUID of the player that got deathed by this player
   * @param loadout The current loadout of this player in string form.
   */
  public void incrementDeath(Player player, Loadout loadout){this.addDeath(player.getUniqueId(), loadout, 1);}
  /**Adds a death to the stats.
   * 
   * @param player The UUID of the player that got deathed by this player
   * @param loadout The current loadout of this player in string form.
   * @param amount The amount to increment
   */
  public void addDeath(UUID player_uuid, Loadout loadout, int amount)
  {
    this.total_deaths += amount;
    if(this.current_kills > this.largest_streak) this.largest_streak = this.current_kills;
    if(this.loadout_streak.get(loadout.getDisplayName()) == null) this.loadout_streak.put(loadout.getDisplayName(),0);
    if(this.current_kills > this.loadout_streak.get(loadout.getDisplayName())) this.loadout_streak.put(loadout.getDisplayName(), this.current_kills);
    this.last_streak = this.current_kills;
    this.current_kills = 0;
    
    if(!this.player_deaths.containsKey(player_uuid)) this.player_deaths.put(player_uuid, 0);
    this.player_deaths.put(player, this.player_deaths.get(player_uuid) + amount);
    
    if(!this.loadout_deaths.containsKey(loadout.getDisplayName())) this.loadout_deaths.put(loadout.getDisplayName(), 0);
    this.loadout_deaths.put(loadout.getDisplayName(), this.loadout_deaths.get(loadout.getDisplayName()) + amount);
  }
  /**Adds a death to the stats.
   * 
   * @param player Player that got deathed by this player
   * @param loadout The current loadout of this player in string form.
   * @param amount The amount to increment
   */
  public void addDeath(Player player, Loadout loadout, int amount){this.addDeath(player.getUniqueId(), loadout, amount);}
  
  
  /**Gets the player this record is for.
   * 
   * @return The player this record is for.
   */
  public Player getPlayer(){return Bukkit.getPlayer(this.player);}
  
  public int getTotalKills(){return this.total_kills;}
  public int getLoadoutKills(Loadout loadout)
  {
    if(!this.loadout_kills.containsKey(loadout)) return 0;
    return this.loadout_kills.get(loadout);
  }
  public int getPlayerKills(Player player){return this.getPlayerKills(player.getUniqueId());}
  public int getPlayerKills(UUID player)
  {
    if(!this.player_kills.containsKey(player)) return 0;
    return this.player_kills.get(player);
  }
  
  public int getTotalDeaths(){return this.total_deaths;}
  public int getLoadoutDeaths(Loadout loadout)
  {
    if(!this.loadout_deaths.containsKey(loadout)) return 0;
    return this.loadout_deaths.get(loadout);
  }
  public int getPlayerDeaths(Player player){return this.getPlayerDeaths(player.getUniqueId());}
  public int getPlayerDeaths(UUID player)
  {
    if(!this.player_kills.containsKey(player)) return 0;
    return this.player_kills.get(player);
  }
  
  public double getTotalKillDeathRatio()
  {
    if(this.total_deaths <= 0) return -1;
    return this.total_kills/(double)this.total_deaths;
  }
  public double getPlayerKillDeathRatio(Player player)
  {
    int kills = this.getPlayerKills(player);
    int deaths = this.getPlayerDeaths(player);
    if(deaths <= 0) return -1;
    return kills/(double)deaths;
  }
  public double getLoadoutKillDeathRatio(Loadout loadout)
  {
    int kills = this.getLoadoutKills(loadout);
    int deaths = this.getLoadoutDeaths(loadout);
    if(deaths <= 0) return -1;
    return kills/(double)deaths;
  }
  
  public int getCurrentStreak(){return this.current_kills;}
  public int getPriorStreak(){return this.last_streak;}
  public int getLargestStreak(){return this.largest_streak;}
  public int getLargestStreak(Loadout loadout)
  {
    if(this.loadout_streak.get(loadout.getDisplayName()) == null) this.loadout_streak.put(loadout.getDisplayName(), 0);
    return this.loadout_streak.get(loadout.getDisplayName());
  }
  
  public int getCurrentChainKill(){return this.chain_kill_count;}
  
  public boolean saveRecord()
  {
    if(file != null) return this.saveRecord(file);
    return false;
  }
  public boolean saveRecord(File file)
  {
    try
    {
      if(!file.exists())
      {
        PersistantPvP.debugLog("File does not yet exist, making file: " + file.getAbsolutePath());
        file.createNewFile();
      }
      FileOutputStream file_out = new FileOutputStream(file);
      ObjectOutputStream obj_out = new ObjectOutputStream(file_out);
      
      obj_out.writeObject(this);

      obj_out.close();
      file_out.close();
    }
    catch(IOException e)
    {
      e.printStackTrace();
      return false;
    }
    return true;
  }
  
  static public StatRecord loadRecord(File file, UUID player_uuid)
  {
    if(!file.exists() || file.isDirectory()) 
    {
      PersistantPvP.debugLog("Record not found or is directory for file: " + file.getAbsolutePath());
      PersistantPvP.debugLog("Returning new record object: " + file.getAbsolutePath());
      return new StatRecord(player_uuid);
    }
    StatRecord ret = null;
    try
    {
      FileInputStream file_in = new FileInputStream(file);
      ObjectInputStream obj_in = new ObjectInputStream(file_in);
      
      Object o = obj_in.readObject();
      ret = (StatRecord)o;
      
      obj_in.close();
      file_in.close();
    }
    catch(IOException e)
    {
      e.printStackTrace();
      return new StatRecord(player_uuid);
    }
    catch (ClassNotFoundException e)
    {
      e.printStackTrace();
      return new StatRecord(player_uuid);
    }
    ret.file = file;
    ret.current_kills = 0;
    return ret;
  }
  
}
