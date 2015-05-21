package net.samongi.PersistantPvP.Players;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.samongi.PersistantPvP.PersistantPvP;
import net.samongi.SamongiLib.Configuration.ConfigAccessor;
import net.samongi.SamongiLib.Utilities.ItemUtilities;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Loadout
{
  private int weight = 0;
  String display_name = "Defautl Loadout One";
  
	private GameMode game_mode = GameMode.ADVENTURE;
  // Armor
  private ItemStack helmet;
  private ItemStack chestplate;
  private ItemStack leggings;
  private ItemStack boots;
  // Items (36)
  private Map<Integer, ItemStack> items = new HashMap<>();
  // Potion Effects
  private List<PotionEffect> effects = new ArrayList<PotionEffect>();
  
  /**Creates an empty loadout to be defined by code.
   * 
   * @param display_name The loadout's displayname.
   * @param weight The loadout's weight.
   */
  public Loadout(String display_name, int weight)
  {
    this.weight = weight;
    this.display_name = display_name;
  }
  /**Generates the loadout based of a configuration file.
   * 
   * @param config The configuration accessor
   * @param path The path to start at in the configuration.
   */
  public Loadout(ConfigAccessor config, String path)
  {
    if(PersistantPvP.debug) PersistantPvP.logger.info("Creating new Loadout using path: '" + path + "'");
    this.display_name = config.getConfig().getString(path + ".display-name", "DEFAULT");
    if(PersistantPvP.debug) PersistantPvP.logger.info("  Display Name: " + display_name);
    this.weight = config.getConfig().getInt(path + ".weight",0);
    if(PersistantPvP.debug) PersistantPvP.logger.info("  Weight: " + weight);
    this.game_mode = GameMode.valueOf(config.getConfig().getString(path + ".gamemode"));
    if(this.game_mode == null) game_mode = GameMode.ADVENTURE;
    if(PersistantPvP.debug) PersistantPvP.logger.info("  Gamemode: " + game_mode);
    
    if(PersistantPvP.debug) PersistantPvP.logger.info("  Getting Helmet... ");
    this.helmet = ItemUtilities.getConfigItemStack(config, path + ".helmet");
    if(PersistantPvP.debug) PersistantPvP.logger.info("  Getting Chestplate... ");
    this.chestplate = ItemUtilities.getConfigItemStack(config, path + ".chestplate");
    if(PersistantPvP.debug) PersistantPvP.logger.info("  Getting Leggings... ");
    this.leggings = ItemUtilities.getConfigItemStack(config, path + ".leggings");
    if(PersistantPvP.debug) PersistantPvP.logger.info("  Getting Boots... ");
    this.boots = ItemUtilities.getConfigItemStack(config, path + ".boots");
    
    List<String> item_keys = new ArrayList<>(config.getConfig().getConfigurationSection(path + ".inventory-slots").getKeys(false));
    for(String n_str : item_keys)
    {
      int n = -1;
      try
      {
        n = Integer.parseInt(n_str);
      } catch(Exception e) 
      {
        continue; // it didn't work.
      }
      if(PersistantPvP.debug) PersistantPvP.logger.info("  Getting ItemSlot + '" + n + "'");
      ItemStack item = ItemUtilities.getConfigItemStack(config, path + ".inventory-slots." + n_str);
      items.put(n, item);
    }
    
    List<String> effect_keys = new ArrayList<>(config.getConfig().getConfigurationSection(path + ".effects").getKeys(false));
    for(String e_str : effect_keys)
    {
      String type = config.getConfig().getString(path + ".effects." + e_str + ".type");
      PotionEffectType potion_effect = PotionEffectType.getByName(type);
      if(potion_effect == null) continue;
      int strength = config.getConfig().getInt(path + ".effects." + e_str + ".strength");
      int duration = config.getConfig().getInt(path + ".effects." + e_str + ".duration");
      boolean ambient = config.getConfig().getBoolean(path + ".effects." + e_str + ".ambient");
      boolean particles = config.getConfig().getBoolean(path + ".effects." + e_str + ".particles");
      
      PotionEffect effect = new PotionEffect(potion_effect, strength, duration, ambient, particles);
      effects.add(effect);
    }
  }
  
  /**Gets the loadout's weight
   * 
   * @return returns the weight of the loadout.
   */
  public int getWeight(){return this.weight;}
  
  /**Gets the display name of the loadout.
   * 
   * @return
   */
  public String getDisplayName(){return this.display_name;}
  
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
    for(int i = 0; i < 36; i++) if(!this.items.containsKey(i)) this.items.put(i, item);
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
    
    for(int i : items.keySet())
    {
      player.getInventory().setItem(i, items.get(i));
    }
    // Applying all the potion effects.
    for(PotionEffect e : effects)
    {
    	player.addPotionEffect(e);
    }
    
  }
}
