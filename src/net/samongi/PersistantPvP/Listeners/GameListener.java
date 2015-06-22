package net.samongi.PersistantPvP.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.samongi.PersistantPvP.GameManager.GameManager;
import net.samongi.PersistantPvP.GameManager.Events.MapChangeEvent;

public class GameListener implements Listener
{
  private final GameManager game_manager;
  
  public GameListener(GameManager game_manager)
  {
    this.game_manager = game_manager;
  }
  
  @EventHandler
  public void onMapChange(MapChangeEvent event)
  {
    game_manager.onMapChange(event);
  }
}
