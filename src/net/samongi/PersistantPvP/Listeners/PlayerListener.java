package net.samongi.PersistantPvP.Listeners;

import net.samongi.PersistantPvP.PersistantPvP;
import net.samongi.PersistantPvP.Maps.GameHandler;
import net.samongi.PersistantPvP.Players.Loadout;
import net.samongi.PersistantPvP.Score.ScoreKeeper;
import net.samongi.SamongiLib.Lambda.Action.PlayerAction;

import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerListener implements Listener
{
	GameHandler handler;
	ScoreKeeper keeper;
	JavaPlugin plugin;
	
	public PlayerListener(JavaPlugin plugin, GameHandler handler, ScoreKeeper keeper)
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
		int score = keeper.getScore(event.getPlayer());
		int num_players = PersistantPvP.group.getPlayers().size();
		handler.fetchLoadout().equipe(event.getPlayer());
		
		BukkitRunnable task = new BukkitRunnable(){
			@Override
			public void run()
			{
			  PlayerAction action = (Player player) -> (keeper.addScore(player, score / num_players));
        PersistantPvP.group.performAction(action);
			}
		};
		task.runTask(plugin);
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
			}
		};
		task.runTask(plugin);
		
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		event.setKeepInventory(true);
		// Check to see if the player was killed by an entity.
		if(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent)
		{
			EntityDamageByEntityEvent last_dam = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();
			if(PersistantPvP.debug)PersistantPvP.logger.info("A player was indeed killed by another entity");
			Entity damager = last_dam.getDamager();
			if(last_dam.getDamager() instanceof Player)
			{
				if(PersistantPvP.debug)PersistantPvP.logger.info("  A player was killed by another player.");
				Player killer = (Player)damager;
				double score_to_take = Math.floor(keeper.getScore(event.getEntity())/2.0);
				if(score_to_take == 0) keeper.addScore(killer, 2);
				else
				{
					keeper.addScore(killer, (int)score_to_take);
					keeper.addScore(event.getEntity(), (int)(0-score_to_take));
				}
			}
			if(last_dam.getDamager() instanceof Arrow)
			{
				if(PersistantPvP.debug)PersistantPvP.logger.info("  A player was killed by an arrow");
				Arrow arrow = (Arrow)damager;
				ProjectileSource shooter = arrow.getShooter();
				if(!(shooter instanceof Player)) return;
				
				Player killer = (Player) shooter;
				double score_to_take = Math.floor(keeper.getScore(event.getEntity())/2.0);
				if(score_to_take == 0) keeper.addScore(killer, 2);
				else
				{
					keeper.addScore(killer, (int)score_to_take);
					keeper.addScore(event.getEntity(), (int)(0-score_to_take));
				}
			}
		}
	}
	private void sendTitle(Player player, Loadout lo)
	{
	  String command0 = "title " + player.getName() + " subtitle {text:\"" + lo.getSubtitle() +"\", color:gray, italic:true}";
    String command1 = "title " + player.getName() + " title {text:\"You are now a " + lo.getDisplayName() + "\"}";
    plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), command0);
    plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), command1);
	}
}
