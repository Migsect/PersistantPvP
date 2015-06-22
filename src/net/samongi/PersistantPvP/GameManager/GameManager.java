package net.samongi.PersistantPvP.GameManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import net.samongi.PersistantPvP.PersistantPvP;
import net.samongi.PersistantPvP.GameManager.Events.MapChangeEvent;
import net.samongi.PersistantPvP.GameManager.GameType.GameType;
import net.samongi.PersistantPvP.GameManager.GameType.FreeForAll.FreeForAllGameType;
import net.samongi.PersistantPvP.Loadouts.LoadoutManager;
import net.samongi.PersistantPvP.Maps.MapManager;
import net.samongi.PersistantPvP.Score.StatKeeper;

public class GameManager
{
  private final PersistantPvP plugin;
	
  private final MapManager map_manager;
  private final LoadoutManager loadout_manager;
  private StatKeeper stat_keeper;
  
	private final Map<String, GameType> gametypes = new HashMap<>();
	private GameType current_gametype = null;
	
	
	public GameManager(PersistantPvP plugin, StatKeeper stat_keeper)
	{
		this.plugin = plugin;
		this.stat_keeper = stat_keeper;
		this.map_manager = new MapManager();
		this.loadout_manager = new LoadoutManager(plugin);
		
		this.parseGameTypes(plugin.getConfig().getConfigurationSection("gametypes"));
		
		String default_gametype = plugin.getConfig().getString("default-gametype");
		if(default_gametype == null || !gametypes.containsKey(default_gametype))
		{
		  Random rand = new Random();
		  List<String> gametypes_list = new ArrayList<>(this.gametypes.keySet());
		  this.setCurrentGameType(gametypes_list.get(rand.nextInt(gametypes.size())));
		}
		else this.setCurrentGameType(default_gametype);
	}
	private void parseGameTypes(ConfigurationSection section)
	{
	  if(section == null) return; // Should display a message here.
    PersistantPvP.debugLog("Parsing GameTypes");
	  Set<String> keys = section.getKeys(false);
	  for(String k : keys)
	  {
	    GameType new_game = null;
	    String type_name = section.getString(k+".gametype");
	    ConfigurationSection type_section = section.getConfigurationSection(k);
	    if(type_name == null) continue;
	    switch(type_name.toUpperCase())
	    {
	      case "FREEFORALL":    new_game = new FreeForAllGameType(plugin, this, stat_keeper, type_section); break;
	      default: continue;
	    }
	    this.gametypes.put(k, new_game);
	    PersistantPvP.debugLog("Parsing Gamemode: '" + k + "' with type: '" + type_name + "'");
	  }
    PersistantPvP.debugLog("Total number of parsed GameTypes: " + this.gametypes.size());
	}
	
	public boolean hasCurrentGameType(){return this.current_gametype != null;}
	public GameType getCurrentGameType(){return this.current_gametype;}
	public boolean hasGameTypeTag(String tag){return this.gametypes.containsKey(tag);}
	public Set<String> getGameTypeTags(){return this.gametypes.keySet();}
	public boolean setCurrentGameType(String tag)
	{
	  if(!this.hasGameTypeTag(tag)) return false;

	  if(this.current_gametype != null) PersistantPvP.log("Disabling GameType: '" + this.current_gametype.getDisplayName() + "'");
	  if(this.current_gametype != null) this.current_gametype.onTypeDisable();
	  
	  this.current_gametype = this.gametypes.get(tag);
	  
	  this.current_gametype.onTypeEnable();
    PersistantPvP.log("Enabling GameType: '" + this.current_gametype.getDisplayName() + "'");
	  return true;
	}
	public void reloadGameType()
	{
	  if(this.current_gametype == null) return;
    PersistantPvP.log("Disabling GameType: '" + this.current_gametype.getDisplayName() + "'");
    this.current_gametype.onTypeDisable();
    PersistantPvP.log("Enabling GameType: '" + this.current_gametype.getDisplayName() + "'");
    this.current_gametype.onTypeEnable();
	}
	
	public void onPlayerJoin(PlayerJoinEvent event)
	{
	  if(!this.hasCurrentGameType()) return;
	  this.current_gametype.onPlayerJoin(event);
	}
	public void onPlayerQuit(PlayerQuitEvent event)
	{
    if(!this.hasCurrentGameType()) return;
    this.current_gametype.onPlayerQuit(event);
	}
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
    if(!this.hasCurrentGameType()) return;
	  this.current_gametype.onPlayerRespawn(event);
	}
	public void onPlayerDeath(PlayerDeathEvent event)
	{
    if(!this.hasCurrentGameType()) return;
	  this.current_gametype.onPlayerDeath(event);
	}
	public void onPlayerDropItem(PlayerDropItemEvent event)
	{
    if(!this.hasCurrentGameType()) return;
	  this.current_gametype.onPlayerDropItem(event);
	}
	public void onPlayerPickupItem(PlayerPickupItemEvent event)
	{
    if(!this.hasCurrentGameType()) return;
	  this.current_gametype.onPlayerPickupItem(event);
	}
	public void spawnPlayer(Player player)
	{
    if(!this.hasCurrentGameType()) return;
	  this.current_gametype.spawnPlayer(player);
	}
	public void onMapChange(MapChangeEvent event)
	{
	  if(!this.hasCurrentGameType()) return;
    this.current_gametype.onMapChange(event);
	}
	
	public MapManager getMapManager(){return this.map_manager;}
	public LoadoutManager getLoadoutManager(){return this.loadout_manager;}
	
}
