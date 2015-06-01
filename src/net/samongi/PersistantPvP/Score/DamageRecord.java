package net.samongi.PersistantPvP.Score;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

/**A damage record of who did what amount of damage to a player.
 * This damage record will be reset whenever a player dies
 * 
 * @author Migsect
 *
 */
public class DamageRecord
{
  private final ScoreKeeper keeper;
  
  private final Map<String, Double> player_damage = new HashMap<>();
  private final Player player;
  
  public DamageRecord(Player player, ScoreKeeper keeper)
  {
    this.player = player;
    this.keeper = keeper;
  }
  
  public void addDamage(String player, double amount)
  {
    if(this.player.getName().equals(player)) return;
    if(!player_damage.containsKey(player)) player_damage.put(player, 0.0);
    player_damage.put(player, player_damage.get(player) + amount);
  }
  public void resetDamage(String player)
  {
    player_damage.remove(player);
  }
  public void resetAllDamage()
  {
    for(String k : player_damage.keySet()) this.resetDamage(k);
  }
  public void awardPoints()
  {
    int player_score = keeper.getScore(player);
    int remove_score = (int) Math.ceil(player_score / 2.0);
    keeper.addScore(player, -remove_score);
    
    int distribute_score = (int) Math.floor(player_score / 2.0);
    if(distribute_score == 0) distribute_score = 2;
    int points_remaining = distribute_score;
    Map<String, Double> player_distrib = new HashMap<>();
    
    Double sum = 0.0;
    for(String p : player_damage.keySet()) sum += player_damage.get(p);
    for(String p : player_damage.keySet()) player_distrib.put(p, player_damage.get(p) / sum);
    
    while(points_remaining > 0) // points time
    {
      String highest_scorer = this.getHighestDamage(); // Get the highest player
      if(highest_scorer == null) break;
      // player_distrib.remove(highest_scorer); // Removes them from the running
      player_damage.remove(highest_scorer); // They are cashing out
      
      int award = (int) Math.ceil(player_distrib.get(highest_scorer) * distribute_score);
      if(award > distribute_score) award = distribute_score;
      distribute_score -= award;
      keeper.addScore(highest_scorer, award); // Adding the points to the player
    }
  }
  public String getHighestDamage()
  {
    String highest_player = null;
    double highest_damage = 0;
    for(String p : player_damage.keySet()) if(player_damage.get(p) > highest_damage)
    {
      highest_player = p;
      highest_damage = player_damage.get(p);
    } 
    return highest_player;
  }
  public String getLowestDamage()
  {
    String lowest_player = null;
    double lowest_damage = Double.MAX_VALUE;
    for(String p : player_damage.keySet()) if(player_damage.get(p) < lowest_damage)
    {
      lowest_player = p;
      lowest_damage = player_damage.get(p);
    }
    return lowest_player;
  }
  
}
