package net.samongi.PersistantPvP.Listeners;

import net.samongi.PersistantPvP.PersistantPvP;
import net.samongi.PersistantPvP.GameManager.GameManager;
import net.samongi.PersistantPvP.Players.Loadout;
import net.samongi.PersistantPvP.Score.ScoreKeeper;
import net.samongi.SamongiLib.Player.PlayerUtilities;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerListener implements Listener
{
	private final GameManager handler;
	private final ScoreKeeper keeper;
	private final JavaPlugin plugin;
	
	public PlayerListener(JavaPlugin plugin, GameManager handler, ScoreKeeper keeper)
	{
		this.handler = handler;
		this.keeper = keeper;
		this.plugin = plugin;
	}
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
	  BukkitRunnable task = new BukkitRunnable(){
      private final Player player = event.getPlayer();
      @Override
      public void run()
      {
        if(PersistantPvP.debug)PersistantPvP.logger.info("Spawning plater " + player.getName());
        handler.getCurrentMap().spawnPlayer(player);
        Loadout lo = handler.fetchLoadout();
        lo.equipe(player);
        sendTitle(player, lo);
      }
    };
    task.runTask(plugin);
		keeper.setScoreboard(event.getPlayer());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
    keeper.getDamageRecord(event.getPlayer()).awardPoints();
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		BukkitRunnable task = new BukkitRunnable(){
			private final Player player = event.getPlayer();
			@Override
			public void run()
			{
			  if(PersistantPvP.debug)PersistantPvP.logger.info("Spawning plater " + player.getName());
				handler.getCurrentMap().spawnPlayer(player);
				Loadout lo = handler.fetchLoadout();
				lo.equipe(player);
				sendTitle(player, lo);
				PlayerUtilities.removeArrows(player);
			}
		};
		task.runTask(plugin);
		
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		event.setKeepInventory(true);
		// Check to see if the player was killed by an entity.
		keeper.getDamageRecord(event.getEntity()).awardPoints();
		
	}
	private void sendTitle(Player player, Loadout lo)
	{
	  String command0 = "title " + player.getName() + " subtitle {text:\"" + lo.getSubtitle() +"\", color:gray, italic:true}";
    String command1 = "title " + player.getName() + " title {text:\"You are now a " + lo.getDisplayName() + "\"}";
    plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), command0);
    plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), command1);
	}
}
