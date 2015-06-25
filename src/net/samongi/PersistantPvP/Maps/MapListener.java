package net.samongi.PersistantPvP.Maps;

import net.samongi.SamongiLib.Maps.MapData;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class MapListener implements Listener
{
  MapManager map_manager;
  
  public MapListener(MapManager map_manager)
  {
    this.map_manager = map_manager;
  }
  
  @EventHandler
  public void onWeatherChange(WeatherChangeEvent event)
  {
    if(!map_manager.hasCurrentMap()) return;
    MapData current_map = map_manager.getCurrentMap();
    if(!current_map.getWorld().equals(event.getWorld())) return;
    boolean will_rain = event.toWeatherState();
    String weather = current_map.getWeather();
    // If the map's weather is not rain or storm and if it is going to rain.  
    // Basically if its sun, then it will cancel the event if its going to rain
    if((!weather.toLowerCase().equals("rain") || !weather.toLowerCase().equals("storm")) && will_rain) event.setCancelled(true);
    if((weather.toLowerCase().equals("rain") || weather.toLowerCase().equals("storm")) && !will_rain) event.setCancelled(true);
    World world = event.getWorld();
    if(!world.isThundering() && weather.toLowerCase().equals("storm")) world.setThundering(true);
  }
  
  @EventHandler
  public void onPlayerMovement(PlayerMoveEvent event)
  {
    
  }
}
