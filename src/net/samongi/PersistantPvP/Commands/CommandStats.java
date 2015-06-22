package net.samongi.PersistantPvP.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.samongi.PersistantPvP.GameManager.GameManager;
import net.samongi.PersistantPvP.Loadouts.Loadout;
import net.samongi.PersistantPvP.Score.StatKeeper;
import net.samongi.PersistantPvP.Score.StatRecord;
import net.samongi.SamongiLib.CommandHandling.ArgumentType;
import net.samongi.SamongiLib.CommandHandling.BaseCommand;
import net.samongi.SamongiLib.CommandHandling.SenderType;

public class CommandStats extends BaseCommand
{
  private final StatKeeper stat_keeper;
  private final GameManager game_manager;
  
  public CommandStats(String command_path,  StatKeeper stat_keeper, GameManager game_manager)
  {
    super(command_path);
    this.stat_keeper = stat_keeper;
    this.game_manager = game_manager;
    
    this.permission = "persistantpvp.stats";
    
    this.allowed_senders.add(SenderType.PLAYER);
    this.allowed_senders.add(SenderType.CONSOLE);
    
    ArgumentType[] types0 = new ArgumentType[0];
    this.allowed_arguments.add(types0);
    ArgumentType[] types1 = {ArgumentType.STRING};
    this.allowed_arguments.add(types1);
  }
  @Override
  public boolean run(CommandSender sender, String[] args)
  {
    if(args.length < 1 && SenderType.getSenderType(sender).equals(SenderType.PLAYER))
    {
      Player player = (Player) sender;
      Loadout loadout = game_manager.getLoadoutManager().getLoadout(player);
      this.sendStatReport(player, loadout, sender);
      return true;
    }
    else
    {
      if(SenderType.getSenderType(sender).equals(SenderType.PLAYER) && sender.hasPermission("persistantpvp.stats.others"))
      {
        sender.sendMessage(ChatColor.RED + "You do not have permission to use this functionality.");
        return true;
      }
      String player_name = args[0];
      Player player = Bukkit.getPlayer(player_name);
      if(player == null)
      {
        sender.sendMessage(ChatColor.RED + "That player is currently not connected");
      }
      Loadout loadout = game_manager.getLoadoutManager().getLoadout(player);
      this.sendStatReport(player, loadout, sender);
      return true;
    }
  }
  private void sendStatReport(Player player, Loadout loadout, CommandSender sender)
  {
    StatRecord record = stat_keeper.getRecord(player);
    sender.sendMessage(ChatColor.AQUA + "Lifetime Report:");
    sender.sendMessage(ChatColor.WHITE + "- " + ChatColor.YELLOW + "Kills - Deaths: " + ChatColor.BOLD + ChatColor.GREEN + record.getTotalKills() + " - " + record.getTotalDeaths());
    double total_kd = record.getTotalKillDeathRatio();
    String total_kd_str = "";
    if(total_kd < 0) total_kd_str = "N/A";
    else total_kd_str = String.format("%.2f", total_kd);
    sender.sendMessage(ChatColor.WHITE + "- " + ChatColor.YELLOW + "K/D Ratio: " + ChatColor.BOLD + ChatColor.GREEN + total_kd_str);
    sender.sendMessage(ChatColor.WHITE + "- " + ChatColor.YELLOW + "Last Killstreak / Killstreak High: " + ChatColor.BOLD + ChatColor.GREEN + record.getPriorStreak() + " / " + record.getLargestStreak());
    if(loadout != null)
    {
      sender.sendMessage(ChatColor.AQUA + "Loadout '" + ChatColor.BLUE + loadout.getDisplayName() + ChatColor.AQUA + "' Report:");
      sender.sendMessage(ChatColor.WHITE + "- " + ChatColor.YELLOW + "Kill - Deaths: " + ChatColor.BOLD + ChatColor.GREEN + record.getLoadoutKills(loadout) + " - " + record.getLoadoutDeaths(loadout));
      double loadout_kd = record.getLoadoutKillDeathRatio(loadout);
      String loadout_kd_str = "";
      if(loadout_kd < 0) loadout_kd_str = "N/A";
      else loadout_kd_str = String.format("%.2f", loadout_kd);
      sender.sendMessage(ChatColor.WHITE + "- " + ChatColor.YELLOW + "K/D Ration: " + ChatColor.BOLD + ChatColor.GREEN + loadout_kd_str);
      sender.sendMessage(ChatColor.WHITE + "- " + ChatColor.YELLOW + "Last Killstreak / Killstreak High: " + ChatColor.BOLD + ChatColor.GREEN + record.getPriorStreak() + " / " + record.getLargestStreak(loadout));
    }
  }
}
