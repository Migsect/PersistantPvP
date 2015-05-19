package net.samongi.PersistantPvP.Players;

import java.util.ArrayList;
import java.util.List;

import net.samongi.SamongiLib.Configuration.ConfigAccessor;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class Loadout
{
	private GameMode game_mode = GameMode.ADVENTURE;
  // Armor
  private ItemStack helmet;
  private ItemStack chestplate;
  private ItemStack leggings;
  private ItemStack boots;
  // Items (36)
  private List<ItemStack> items = new ArrayList<>(36);
  // Potion Effects
  private List<PotionEffect> effects = new ArrayList<PotionEffect>();
  
  /**Creates an empty loadout to be defined by code.
   * 
   */
  public Loadout(){}
  /**Generates the loadout based of a configuration file.
   * 
   * @param config The configuration accessor
   * @param path The path to start at in the configuration.
   */
  public Loadout(ConfigAccessor config, String path)
  {
    // TODO
  }
  
  /**Sets the helmet
   * 
   * @param item
   */
  public void setHelmet(ItemStack item){this.helmet = item;}
  /**Sets the chestplate
   * 
   * @param item
   */
  public void setChestplate(ItemStack item){this.chestplate = item;}
  /**Sets the leggings
   * 
   * @param item
   */
  public void setLeggings(ItemStack item){this.leggings = item;}
  /**Sets the boots
   * 
   * @param item
   */
  public void setBoots(ItemStack item){this.boots = item;}
  
  /**Adds an inventory item to the item list, placing it in the first one it finds.
   * 
   * @param item
   */
  public void addInventoryItem(ItemStack item)
  {
    items.add(item);
  }
  
  public void addPotionEffect(PotionEffect effect)
  {
  	this.effects.add(effect);
  }
  
  public void equipe(Player player)
  {
  	player.setGameMode(this.game_mode);
    // Cleaning up.
    player.getInventory().clear();
    for(PotionEffect pe : player.getActivePotionEffects())
    {
      player.removePotionEffect(pe.getType());
    }
    
    // Setting the items
    player.getInventory().setBoots(boots);
    player.getInventory().setLeggings(leggings);
    player.getInventory().setChestplate(chestplate);
    player.getInventory().setHelmet(helmet);
    
    for(ItemStack i : items)
    {
      player.getInventory().addItem(i);
    }
    // Applying all the potion effects.
    for(PotionEffect e : effects)
    {
    	player.addPotionEffect(e);
    }
    
  }
}
