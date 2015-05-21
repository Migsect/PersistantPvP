package net.samongi.PersistantPvP.Listeners;

import net.samongi.PersistantPvP.PersistantPvP;
import net.samongi.PersistantPvP.Maps.GameHandler;
import net.samongi.PersistantPvP.Score.ScoreKeeper;

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
		handler.fetchLoadout().equipe(event.getPlayer());
		handler.getCurrentMap().spawnPlayer(event.getPlayer());
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
				PersistantPvP.group.performAction((Player player) -> keeper.addScore(player, score / num_players));
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
				handler.getCurrentMap().spawnPlayer(player);
				handler.fetchLoadout().equipe(event.getPlayer());
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
}
