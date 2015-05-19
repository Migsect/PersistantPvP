package net.samongi.PersistantPvP.Maps;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class SpawnEntity implements Spawn
{
  private Entity entity;
  public SpawnEntity(Entity entity)
  {
    this.entity = entity;
  }
  @Override
  public void spawn(Player player)
  {
    player.teleport(entity);
    
  }
  
}
