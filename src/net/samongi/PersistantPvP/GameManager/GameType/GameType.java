package net.samongi.PersistantPvP.GameManager.GameType;

import net.samongi.PersistantPvP.GameManager.Events.MapChangeEvent;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public interface GameType
{
  public String getDisplayName();
  
  public void onTypeEnable();
  public void onTypeDisable();
  public void onMapChange(MapChangeEvent event);
  
  public void onPlayerDeath(PlayerDeathEvent event);
  public void onPlayerRespawn(PlayerRespawnEvent event);
  
  public void onPlayerJoin(PlayerJoinEvent event);
  public void onPlayerQuit(PlayerQuitEvent event);
  
  public void onPlayerDropItem(PlayerDropItemEvent event);
  public void onPlayerPickupItem(PlayerPickupItemEvent event);
  
  public void displayScoreboard(Player player);
  
  public void spawnPlayer(Player player);
}
