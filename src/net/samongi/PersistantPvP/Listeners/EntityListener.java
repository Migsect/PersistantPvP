package net.samongi.PersistantPvP.Listeners;

import net.samongi.PersistantPvP.PersistantPvP;
import net.samongi.PersistantPvP.GameManager.GameManager;
import net.samongi.PersistantPvP.Score.ScoreKeeper;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class EntityListener implements Listener
{
  @SuppressWarnings("unused")
  private final GameManager handler;
  private final ScoreKeeper keeper;
  @SuppressWarnings("unused")
  private final JavaPlugin plugin;
  
  public EntityListener(JavaPlugin plugin, GameManager handler, ScoreKeeper keeper)
  {
    this.handler = handler;
    this.keeper = keeper;
    this.plugin = plugin;
  }
  
  @EventHandler
  public void onEntityDamage(EntityDamageByEntityEvent event)
  {
    if(event.isCancelled()) return;
    
    if(!(event.getEntity() instanceof Player)) return;
    Player player = (Player) event.getEntity();
    Double damage =event.getDamage();
    Player damager = null;
    if(event.getDamager() instanceof Player)
    {
      damager = (Player) event.getDamager();
    }
    else if(event.getDamager() instanceof Arrow)
    {
      Arrow arrow = (Arrow) event.getDamager();
      if(!(arrow.getShooter() instanceof Player)) return;
      damager = (Player) arrow.getShooter();
    }
    PersistantPvP.debugLog("Adding " + damage + " from '" + damager.getName() + "' to '" + player.getName() + "'s DamageRecord");
    keeper.getDamageRecord(player).addDamage(damager.getName(), damage);
    
  }
}
