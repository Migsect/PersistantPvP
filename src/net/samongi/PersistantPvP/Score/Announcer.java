package net.samongi.PersistantPvP.Score;

import net.samongi.PersistantPvP.PersistantPvP;
import net.samongi.PersistantPvP.GameManager.GameManager;
import net.samongi.SamongiLib.Player.Group;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Announcer
{
  private final StatKeeper stat_keeper;
  
  public Announcer(StatKeeper stat_keeper, ScoreKeeper score_keeper, GameManager manager)
  {
    this.stat_keeper = stat_keeper;
  }
  
  public void onKill(Player player)
  {
    StatRecord record = stat_keeper.getRecord(player);
    int chain_kills = record.getCurrentChainKill();
    int killstreak = record.getCurrentStreak();
    if(chain_kills >= 2) this.chainKillAnnounce(player, chain_kills);
    if(killstreak >= 4) this.killstreakAnnounce(player, killstreak);
  }
  
  @SuppressWarnings("deprecation")
  private void chainKillAnnounce(Player player, int amount)
  {
    String command0 = "title " + player.getName() + " clear";
    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command0);
    String command1 = "title " + player.getName() + " times 0 40 40";
    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command1);
    switch(amount)
    {
      case 2: 
        String command2 = "title " + player.getName() + " title {text:\"DOUBLE KILL\", color:red}";
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command2);
        player.playSound(player.getLocation(), "double_kill", 1, 1);
        break;
      case 3:
        String command3 = "title " + player.getName() + " title {text:\"TRIPLE KILL\", color:red}";
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command3);
        player.playSound(player.getLocation(), "triple_kill", 1, 1);
        break;
      case 4:
        String command4 = "title " + player.getName() + " title {text:\"MEGA KILL\", color:red}";
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command4);
        player.playSound(player.getLocation(), "mega_kill", 1, 1);
        break;
      case 5:
        String command5 = "title " + player.getName() + " title {text:\"ULTRA KILL\", color:red}";
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command5);
        player.playSound(player.getLocation(), "ultra_kill", 1, 1);
        break;
      case 6:
        String command6 = "title " + player.getName() + " title {text:\"MONSTER KILL\", color:red}";
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command6);
        player.playSound(player.getLocation(), "monster_kill", 1, 1);
        break;
    }
  }
  @SuppressWarnings("deprecation")
  private void killstreakAnnounce(Player player, int amount)
  {
    Group group = PersistantPvP.group;
    String command0 = "title @a clear";
    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command0);
    String command1 = "title @a times 0 40 40";
    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command1);
    switch(amount)
    {
      case 4:
        String command4b = "title @a title {text:\"" + player.getName() + " is on a KILLING SPREE!\", color:dark_red}";
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command4b);
        group.performAction((Player p)->{
          p.playSound(p.getLocation(), "killing_spree", 1, 1);
        });
        break;
      case 6:
        String command6b = "title @a title {text:\"" + player.getName() + " is on a RAMPAGE\", color:dark_red}";
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command6b);
        group.performAction((Player p)->{
          p.playSound(p.getLocation(), "rampage", 1, 1);
        });
        break;
      case 8:
        String command8b = "title @a title {text:\"" + player.getName() + " is DOMINATING\", color:dark_red}";
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command8b);
        group.performAction((Player p)->{
          p.playSound(p.getLocation(), "dominating", 1, 1);
        });
        break;
      case 10:
        String command10b = "title @a title {text:\"" + player.getName() + " is UNSTOPPABLE\", color:dark_red}";
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command10b);
        group.performAction((Player p)->{
          p.playSound(p.getLocation(), "unstoppable", 1, 1);
        });
        break;
      case 12:
        String command12b = "title @a title {text:\"" + player.getName() + " is GOD LIKE\", color:dark_red}";
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command12b);
        group.performAction((Player p)->{
          p.playSound(p.getLocation(), "god_like", 1, 1);
        });
        break;
    }
  }
}
