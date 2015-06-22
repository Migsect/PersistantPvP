package net.samongi.PersistantPvP.Score;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**A damage record of who did what amount of damage to a player.
 * This damage record will be reset whenever a player dies
 * 
 * @author Migsect
 *
 */
public class DamageRecord
{
  private final Map<UUID, Double> player_damage = new HashMap<>();
  private final UUID player;
  private UUID last_damager;
  
  public DamageRecord(Player player)
  {
    this.player = player.getUniqueId();
  }
  public Player getLastDamager(){return Bukkit.getPlayer(this.last_damager);}
  public Set<Player> getDamagers()
  {
    Set<UUID> damagers = this.player_damage.keySet();
    Set<Player> converted = new HashSet<>();
    for(UUID p : damagers) converted.add(Bukkit.getPlayer(p));
    return converted;
  }
  public Player getPlayer(){return Bukkit.getPlayer(this.player);}
  
  public double getDamage(String player){return this.getDamage(Bukkit.getPlayer(player));}
  public double getDamage(Player player){return this.getDamage(player.getUniqueId());}
  public double getDamage(UUID player)
  {
    if(!player_damage.containsKey(player)) return 0;
    return this.player_damage.get(player);
  }
  
  public void addDamage(String player, double amount){this.addDamage(Bukkit.getPlayer(player), amount);}
  public void addDamage(Player player, double amount){this.addDamage(player.getUniqueId(), amount);}
  public void addDamage(UUID player, double amount)
  {
    if(this.player.equals(player)) return;
    if(!player_damage.containsKey(player)) player_damage.put(player, 0.0);
    this.player_damage.put(player, player_damage.get(player) + amount);
    this.last_damager = player;
  }
  
  public void resetDamage(String player){this.resetDamage(Bukkit.getPlayer(player));}
  public void resetDamage(Player player){this.resetDamage(player.getUniqueId());}
  public void resetDamage(UUID player)
  {
    player_damage.remove(player);
  }
  
  public void resetAllDamage()
  {
    for(UUID k : player_damage.keySet()) this.resetDamage(k);
  }
  
  
  public Player getHighestDamage()
  {
    Player highest_player = null;
    double highest_damage = 0;
    for(UUID p : player_damage.keySet()) if(player_damage.get(p) > highest_damage)
    {
      highest_player = Bukkit.getPlayer(p);
      highest_damage = player_damage.get(p);
    } 
    return highest_player;
  }
  public Player getLowestDamage()
  {
    Player lowest_player = null;
    double lowest_damage = Double.MAX_VALUE;
    for(UUID p : player_damage.keySet()) if(player_damage.get(p) < lowest_damage)
    {
      lowest_player = Bukkit.getPlayer(p);
      lowest_damage = player_damage.get(p);
    }
    return lowest_player;
  }
  
}
