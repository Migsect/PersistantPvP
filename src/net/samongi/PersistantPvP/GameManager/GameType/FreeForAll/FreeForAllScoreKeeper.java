package net.samongi.PersistantPvP.GameManager.GameType.FreeForAll;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.samongi.PersistantPvP.PersistantPvP;
import net.samongi.PersistantPvP.Score.DamageRecord;
import net.samongi.PersistantPvP.Score.StatKeeper;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class FreeForAllScoreKeeper
{
  private ScoreboardManager manager;
  private StatKeeper stat_keeper;
  
  private Map<UUID, Integer> scores = new HashMap<>();
  private Scoreboard scoreboard;
  private Objective obj;
  
  
  
  public FreeForAllScoreKeeper()
  {
    manager = Bukkit.getScoreboardManager();
    scoreboard = manager.getNewScoreboard();
    obj = scoreboard.registerNewObjective("points", "dummy");
    obj.setDisplaySlot(DisplaySlot.SIDEBAR);
    obj.setDisplayName("Points");
    PersistantPvP.group.performAction((Player p) -> p.setScoreboard(scoreboard));
  }
  public void awardPoints(Player player){this.awardPoints(stat_keeper.getDamageRecord(player));}
  public void awardPoints(DamageRecord record)
  {
    int player_score = this.getScore(record.getPlayer());
    int remove_score = (int) Math.ceil(player_score / 2.0);
    this.addScore(record.getPlayer(), -remove_score);
    
    int distribute_score = remove_score;
    if(distribute_score == 0) distribute_score = 2;
    int points_remaining = distribute_score;
    Map<UUID, Double> player_distrib = new HashMap<>();
    
    Double sum = 0.0;
    for(Player p : record.getDamagers()) sum += record.getDamage(p);
    for(Player p : record.getDamagers()) player_distrib.put(p.getUniqueId(), record.getDamage(p) / sum);
    
    while(points_remaining > 0) // points time
    {
      Player highest_scorer = record.getHighestDamage(); // Get the highest player
      if(highest_scorer == null) break;
      // player_distrib.remove(highest_scorer); // Removes them from the running
      record.resetDamage(highest_scorer); // They are cashing out
      
      int award = (int) Math.ceil(player_distrib.get(highest_scorer.getUniqueId()) * distribute_score);
      if(award > distribute_score) award = distribute_score;
      distribute_score -= award;
      this.addScore(highest_scorer, award); // Adding the points to the player
    }
  }
  
  /**
   * @see FreeForAllScoreKeeper#addScore(UUID, int)
   */
  public void addScore(String string, int amnt){this.addScore(Bukkit.getPlayer(string), amnt);}
  /**
   * @see FreeForAllScoreKeeper#addScore(UUID, int)
   */
  public void addScore(Player player, int amnt){this.addScore(player.getUniqueId(), amnt);}
  /**Adds an amnt of score points to the player
   * Negatives can be used to "subtract" points if needed.
   * 
   * @param player The player to add points to
   * @param amnt The amount being added
   */
  public void addScore(UUID player, int amnt)
  {
    if(!scores.containsKey(player)) scores.put(player, 0);
    scores.put(player, scores.get(player) + amnt);
    PersistantPvP.debugLog("  Adding score to '"+ player +"': " + amnt);
    
    Score score = obj.getScore(Bukkit.getPlayer(player).getName());
    score.setScore(scores.get(player));
    
    PersistantPvP.group.performAction((Player p) -> p.setScoreboard(scoreboard));
  }
  
  /**
   * @see FreeForAllScoreKeeper#resetPlayer(UUID)
   */
  public void resetPlayer(String player){this.resetPlayer(Bukkit.getPlayer(player));}
  /**
   * @see FreeForAllScoreKeeper#resetPlayer(UUID)
   */
  public void resetPlayer(Player player){this.resetPlayer(player.getUniqueId());}
  /**Resets the players scores and overall removes them
   * 
   * @param player The player to remove.
   */
  public void resetPlayer(UUID player)
  {
    if(scores.containsKey(player))scores.remove(player);
    scoreboard.resetScores(Bukkit.getPlayer(player).getName());
    
    PersistantPvP.group.performAction((Player p) -> p.setScoreboard(scoreboard));
  }
  /**
   * @see FreeForAllScoreKeeper#getScore(UUID)
   */
  public int getScore(String player){return this.getScore(Bukkit.getPlayer(player));}
  /**
   * @see FreeForAllScoreKeeper#getScore(UUID)
   */
  public int getScore(Player player){return this.getScore(player.getUniqueId());}
  /**Gets the score of the player, if no score is present it returns 0
   * 
   * @param player The player to get the score for
   * @return The score
   */
  public int getScore(UUID player)
  {
    if(!scores.containsKey(player)) return 0;
    else return scores.get(player);
  }
  
  public void setScoreboard(Player player){player.setScoreboard(scoreboard);}
}
