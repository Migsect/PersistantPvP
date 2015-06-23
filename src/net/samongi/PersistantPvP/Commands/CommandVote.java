package net.samongi.PersistantPvP.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.samongi.PersistantPvP.Maps.VoteManager;
import net.samongi.SamongiLib.CommandHandling.ArgumentType;
import net.samongi.SamongiLib.CommandHandling.BaseCommand;
import net.samongi.SamongiLib.CommandHandling.SenderType;
import net.samongi.SamongiLib.Maps.MapData;
import net.samongi.SamongiLib.Menu.InventoryMenu;
import net.samongi.SamongiLib.Menu.ButtomAction.ButtonAction;

public class CommandVote extends BaseCommand
{
  private final VoteManager vote_manager;
  
  public CommandVote(String command_path, VoteManager vote_manager)
  {
    super(command_path);
    this.vote_manager = vote_manager;
    
    this.permission = "persistantpvp.vote";
    
    this.allowed_senders.add(SenderType.PLAYER);
    
    ArgumentType[] types0 = new ArgumentType[0];
    this.allowed_arguments.add(types0);
  }
  @Override
  public boolean run(CommandSender sender, String[] args)
  {
    Player player = (Player)sender;
    InventoryMenu menu = new InventoryMenu(player, 2, ChatColor.BOLD + "" + ChatColor.UNDERLINE + ChatColor.BLUE + "Vote For The Next Map!");
    
    List<MapData> candidates = vote_manager.getCandidates();
    int index = 0;
    for(MapData m : candidates)
    {
      ItemStack menu_item = m.getDisplayItem();
      
      ItemMeta menu_im = menu_item.getItemMeta();
      menu_im.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + menu_im.getDisplayName());
      List<String> menu_item_lore = new ArrayList<>();
      int current_votes = vote_manager.getVotes(m);
      double multiplier = vote_manager.getVoteMultiplier(m);
      
      String current_vote = vote_manager.getVote(player);
      boolean is_vote = false;
      if(current_vote != null) is_vote = current_vote.equals(m.getTag());
      
      String vote_power = String.format("%.2f", current_votes * multiplier);
      String vote_multiplier = String.format("%.0f", multiplier * 100) + "%";
      if(multiplier > 1.99) vote_multiplier = ChatColor.AQUA + vote_multiplier;
      else if(multiplier > 1.4) vote_multiplier = ChatColor.BLUE + vote_multiplier;
      else if(multiplier >= 0.99) vote_multiplier = ChatColor.GREEN + vote_multiplier;
      else if(multiplier >= 0.74) vote_multiplier = ChatColor.YELLOW + vote_multiplier;
      else if(multiplier >= 0.49) vote_multiplier = ChatColor.GOLD + vote_multiplier;
      else if(multiplier >= 0.24) vote_multiplier = ChatColor.RED + vote_multiplier;
      else vote_multiplier = ChatColor.DARK_RED + vote_multiplier;
      
      menu_item_lore.add(ChatColor.WHITE + "Current Votes: " + ChatColor.YELLOW + current_votes);
      menu_item_lore.add(ChatColor.WHITE + "Vote Multiplier: " + vote_multiplier);
      menu_item_lore.add(ChatColor.WHITE + "Current Vote Strength: " + ChatColor.YELLOW + vote_power);
      if(is_vote) menu_item_lore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "[You are voting for this.]");
      
      menu_im.setLore(menu_item_lore);
      menu_item.setItemMeta(menu_im);
      
      menu.setItem(index, menu_item);
      ButtonAction button_action = new VoteForMap(this.vote_manager, player.getUniqueId(), m, menu);
      menu.addClickAction(index, button_action);
      
      index++;
    }
    menu.openMenu();
    
    return true;
  }
  class VoteForMap implements ButtonAction
  {
    private final VoteManager vote_manager;
    private final MapData map;
    private final UUID player;
    private final InventoryMenu menu;
    public VoteForMap(VoteManager vote_manager, UUID player, MapData map, InventoryMenu menu)
    {
      this.vote_manager = vote_manager;
      this.map = map;
      this.player = player;
      this.menu = menu;
    }
    @Override
    public void onButtonPress()
    {
      this.vote_manager.setVote(player, map.getTag());
      Bukkit.getPlayer(player).sendMessage(ChatColor.BLUE + "You have voted for: " + ChatColor.BOLD + ChatColor.GREEN + map.getDisplayName());
      
      List<MapData> candidates = vote_manager.getCandidates();
      int index = 0;
      for(MapData m : candidates)
      {
        ItemStack menu_item = m.getDisplayItem();
        
        ItemMeta menu_im = menu_item.getItemMeta();
        menu_im.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + menu_im.getDisplayName());
        List<String> menu_item_lore = new ArrayList<>();
        int current_votes = vote_manager.getVotes(m);
        double multiplier = vote_manager.getVoteMultiplier(m);
        
        boolean is_vote = vote_manager.getVote(player).equals(m.getTag());
        
        String vote_power = String.format("%.2f", current_votes * multiplier);
        String vote_multiplier = String.format("%.0f", multiplier * 100) + "%";
        if(multiplier > 1.99) vote_multiplier = ChatColor.AQUA + vote_multiplier;
        else if(multiplier > 1.4) vote_multiplier = ChatColor.BLUE + vote_multiplier;
        else if(multiplier >= 0.99) vote_multiplier = ChatColor.GREEN + vote_multiplier;
        else if(multiplier >= 0.74) vote_multiplier = ChatColor.YELLOW + vote_multiplier;
        else if(multiplier >= 0.49) vote_multiplier = ChatColor.GOLD + vote_multiplier;
        else if(multiplier >= 0.24) vote_multiplier = ChatColor.RED + vote_multiplier;
        else vote_multiplier = ChatColor.DARK_RED + vote_multiplier;
        
        menu_item_lore.add(ChatColor.WHITE + "Current Votes: " + ChatColor.YELLOW + current_votes);
        menu_item_lore.add(ChatColor.WHITE + "Vote Multiplier: " + vote_multiplier);
        menu_item_lore.add(ChatColor.WHITE + "Current Vote Strength: " + ChatColor.YELLOW + vote_power);
        if(is_vote) menu_item_lore.add(ChatColor.GREEN + "" + ChatColor.BOLD + "[You are voting for this.]");
        
        menu_im.setLore(menu_item_lore);
        menu_item.setItemMeta(menu_im);
        
        menu.setItem(index, menu_item);
        index++;
      }
      
      Bukkit.getPlayer(player).updateInventory();
      
    }
    
  }

}
