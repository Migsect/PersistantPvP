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
	
	public void addScore(Player player, int amnt)
	{
		if(!scores.containsKey(player.getName())) scores.put(player.getName(), 0);
		scores.put(player.getName(), scores.get(player.getName()) + amnt);
		if(PersistantPvP.debug) PersistantPvP.logger.info("  Adding score to '"+ player.getName() +"': " + amnt);
		
		Score score = obj.getScore(player.getName());
		score.setScore(scores.get(player.getName()));
		
		PersistantPvP.group.performAction((Player p) -> p.setScoreboard(scoreboard));
	}
	
	public void resetPlayer(Player player)
	{
		if(scores.containsKey(player.getName()))scores.remove(player.getName());
		scoreboard.resetScores(player.getName());
	}
	
	public int getScore(Player player)
	{
		if(!scores.containsKey(player.getName())) return 0;
		else return scores.get(player.getName());
	}
	public void setScoreboard(Player player)
	{
		player.setScoreboard(scoreboard);
	}
}
