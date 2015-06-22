package net.samongi.PersistantPvP.GameManager.Events;

import net.samongi.SamongiLib.Maps.MapData;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class MapChangeEvent extends Event
{
  private static final HandlerList handlers = new HandlerList();
  private final MapData map_to;
  private final MapData map_from;
  
  public MapChangeEvent(MapData map_to, MapData map_from)
  {
    this.map_from = map_from;
    this.map_to = map_to;
  }
  
  @Override
  public HandlerList getHandlers(){return handlers;}
  public static HandlerList getHandlerList(){return handlers;}
  
  public MapData getMapTo(){return this.map_to;}
  public MapData getMapFrom(){return this.map_from;}

}
