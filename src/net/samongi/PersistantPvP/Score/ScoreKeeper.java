package net.samongi.PersistantPvP.Score;

import java.util.HashMap;
import java.util.Map;

import net.samongi.PersistantPvP.PersistantPvP;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class ScoreKeeper
{
	private Map<String, Integer> scores = new HashMap<>();
	private Map<String, DamageRecord> damage_records = new HashMap<>();
	private ScoreboardManager manager;
	private Scoreboard scoreboard;
	private Objective obj;
	
	
	public ScoreKeeper()
	{
		manager = Bukkit.getScoreboardManager();
		scoreboard = manager.getNewScoreboard();
		obj = scoreboard.registerNewObjective("points", "dummy");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		obj.setDisplayName("Points");
		PersistantPvP.group.performAction((Player p) -> p.setScoreboard(scoreboard));
	}
	
	/**Adds an amnt of score points to the player
	 * Negatives can be used to "subtract" points if needed.
	 * 
	 * @param player The player to add points to
	 * @param amnt The amount being added
	 */
	public void addScore(String player, int amnt)
	{
		if(!scores.containsKey(player)) scores.put(player, 0);
		scores.put(player, scores.get(player) + amnt);
		if(PersistantPvP.debug) PersistantPvP.logger.info("  Adding score to '"+ player +"': " + amnt);
		
		Score score = obj.getScore(player);
		score.setScore(scores.get(player));
		
		PersistantPvP.group.performAction((Player p) -> p.setScoreboard(scoreboard));
	}
	/**Adds an amnt of score points to the player
   * Negatives can be used to "subtract" points if needed.
   * 
   * @param player The player to add points to
   * @param amnt The amount being added
   */
	public void addScore(Player player, int amnt){this.addScore(player.getName(),amnt);}
	
	/**Resets the players scores and overall removes them
	 * 
	 * @param player The player to remove.
	 */
	public void resetPlayer(String player)
	{
		if(scores.containsKey(player))scores.remove(player);
		scoreboard.resetScores(player);
		
		PersistantPvP.group.performAction((Player p) -> p.setScoreboard(scoreboard));
	}
  /**Resets the players scores and overall removes them
   * 
   * @param player The player to remove.
   */	
	public void resetPlayer(Player player){this.resetPlayer(player.getName());}

	/**Gets the score of the player, if no score is present it returns 0
	 * 
	 * @param player The player to get the score for
	 * @return The score
	 */
	public int getScore(String player)
	{
		if(!scores.containsKey(player)) return 0;
		else return scores.get(player);
	}
	/**Gets the score of the player, if no score is present it returns 0
   * 
   * @param player The player to get the score for
   * @return The score
   */
	public int getScore(Player player){return this.getScore(player.getName());}
	
	/**Returns the specified damage record for the player
	 * 
	 * @param player The player the damage record is for
	 * @return The damage record
	 */
	
	public DamageRecord getDamageRecord(String player)
	{
	  if(!this.damage_records.containsKey(player)) this.damage_records.put(player, new DamageRecord(Bukkit.getPlayer(player), this));
	  return this.damage_records.get(player);
	}
	/**Returns the specified damage record for the player
   * 
   * @param player The player the damage record is for
   * @return The damage record
   */
	public DamageRecord getDamageRecord(Player player){return this.getDamageRecord(player.getName());}
	
	/**Removes the player from all damage records.
	 * 
	 * @param player The player to remove
	 */
	public void removeFromDamageRecords(String player)
	{
	  for(String p : damage_records.keySet()){this.getDamageRecord(p).resetDamage(player);}
	}
	/**Removes the player from all damage records.
   * 
   * @param player The player to remove
   */
	public void removeFromDamageRecords(Player player){this.removeFromDamageRecords(player.getName());}
	
	public void removeDamageRecord(Player player){this.damage_records.remove(player);}
	
	public void setScoreboard(Player player){player.setScoreboard(scoreboard);}
}
