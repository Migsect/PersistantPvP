package net.samongi.PersistantPvP.Listeners;

import net.samongi.PersistantPvP.PersistantPvP;
import net.samongi.PersistantPvP.GameManager.GameManager;
import net.samongi.PersistantPvP.Players.Loadout;
import net.samongi.PersistantPvP.Score.DamageRecord;
import net.samongi.PersistantPvP.Score.ScoreKeeper;
import net.samongi.PersistantPvP.Score.StatKeeper;
import net.samongi.PersistantPvP.Score.StatRecord;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerListener implements Listener
{
	private final GameManager handler;
	private final ScoreKeeper keeper;
	private final StatKeeper stat_keeper;
	
	public PlayerListener(JavaPlugin plugin, GameManager handler, ScoreKeeper keeper, StatKeeper stat_keeper)
	{
		this.handler = handler;
		this.keeper = keeper;
		this.stat_keeper = stat_keeper;
	}
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		keeper.setScoreboard(event.getPlayer());
    stat_keeper.loadRecord(event.getPlayer());
    
    handler.spawnPlayer(event.getPlayer());
    
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
    keeper.getDamageRecord(event.getPlayer()).awardPoints();
    stat_keeper.saveRecord(event.getPlayer());
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
	  handler.spawnPlayer(event.getPlayer());
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		event.setKeepInventory(true);
		// Check to see if the player was killed by an entity.
		Player death_player = event.getEntity();
		DamageRecord dmg_record = keeper.getDamageRecord(death_player);
		String high_dmg_player_name = dmg_record.getHighestDamage();
		
	  PersistantPvP.debugLog("Found Highest Damage Player to be: '" + high_dmg_player_name + "' for '" + death_player.getName() + "'");
	  Player high_dmg_player = Bukkit.getPlayer(high_dmg_player_name);
	  
	  if(high_dmg_player != null) high_dmg_player.getInventory().addItem(handler.fetchReward());
		dmg_record.awardPoints();
		
		
		Player kill_player = dmg_record.getLastDamager();
		if(kill_player != null)
		{
  		Loadout death_loadout = handler.getCurrentLoadout(death_player);
      Loadout kill_loadout = handler.getCurrentLoadout(kill_player);
      StatRecord death_stat_record = stat_keeper.getRecord(death_player);
  		StatRecord kill_stat_record = stat_keeper.getRecord(kill_player);
  		death_stat_record.incrementDeath(kill_player, death_loadout); // killed by kill_player while using death_loadout
  		kill_stat_record.incrementKill(death_player, kill_loadout); // killed death_player using kill_loadout
		}
		
   
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event)
	{
	  event.setCancelled(true); // we're just going to cancel it because we don't want people dropping items.
	  // We could also use this for abilities if we wanted to.
	}
}
