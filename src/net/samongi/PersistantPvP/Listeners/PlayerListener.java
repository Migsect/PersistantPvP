package net.samongi.PersistantPvP.Listeners;

import net.samongi.PersistantPvP.GameManager.GameManager;
import net.samongi.PersistantPvP.Score.StatKeeper;

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
	private final GameManager manager;
	private final StatKeeper stat_keeper;
	
	public PlayerListener(JavaPlugin plugin, GameManager manager, StatKeeper stat_keeper)
	{
		this.manager = manager;
		this.stat_keeper = stat_keeper;
	}
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
    stat_keeper.onPlayerJoin(event);
    manager.onPlayerJoin(event);
    
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
    stat_keeper.onPlayerQuit(event);
    manager.onPlayerQuit(event);
    
    
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
	  manager.spawnPlayer(event.getPlayer());
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event)
	{
	  manager.onPlayerDeath(event);
   
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event)
	{
	  this.manager.onPlayerDropItem(event);
	}
}
