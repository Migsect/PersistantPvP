package net.samongi.PersistantPvP.Maps;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SpawnPoint implements Spawn
{
  private Location loc;
  
  public SpawnPoint(Location loc)
  {
    this.loc = loc;
  }
  
  @Override
  public void spawn(Player player)
  {
    player.teleport(loc);
  }

}

