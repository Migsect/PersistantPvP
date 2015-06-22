package net.samongi.PersistantPvP.Commands;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import net.samongi.PersistantPvP.Maps.MapManager;
import net.samongi.SamongiLib.CommandHandling.ArgumentType;
import net.samongi.SamongiLib.CommandHandling.BaseCommand;
import net.samongi.SamongiLib.CommandHandling.SenderType;

public class CommandMap extends BaseCommand
{
  private final MapManager map_manager;
  public CommandMap(String command_path, MapManager map_manager)
  {
    super(command_path);
    this.map_manager =  map_manager;
    
    this.permission = "persistantpvp.switchmap";
    
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
    if(args.length == 0)
    {
      Set<String> map_keys = map_manager.getMapKeys();
      sender.sendMessage(ChatColor.AQUA + "Current Loaded Maps: " + ChatColor.WHITE + "[" + ChatColor.YELLOW + map_keys.size() + ChatColor.YELLOW + "]");
      for(String k : map_keys)
      {
        sender.sendMessage(ChatColor.WHITE + " - " + ChatColor.YELLOW + k + ChatColor.WHITE + " : " + ChatColor.GOLD + this.map_manager.getMap(k).getDisplayName());
      }
    }
    else // there is at least one argument
    {
      String map_tag = args[0];
      if(!this.map_manager.mapExists(map_tag))
      {
        sender.sendMessage(ChatColor.RED + "Map tag: '" + ChatColor.YELLOW + map_tag + ChatColor.RED + "' does not exist.");
        return true;
      }
      this.map_manager.setCurrentMap(map_tag);
    }
    return true;
  }

}
