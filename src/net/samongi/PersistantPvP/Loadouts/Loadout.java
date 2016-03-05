package net.samongi.PersistantPvP.Loadouts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.samongi.PersistantPvP.PersistantPvP;
import net.samongi.SamongiLib.Configuration.ConfigAccessor;
import net.samongi.SamongiLib.Items.ItemUtil;
import net.samongi.SamongiLib.Utilities.TextUtil;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Loadout
{
  // Static variables:
  private static final Set<Loadout> loadouts = new HashSet<>();
  
  private List<String> sorting_tags = new ArrayList<>();
  
  private int weight = 0;
  private String display_name = "Default Loadout One";
  private String simple_name = null;
  private String subtitle = "";
  
  private List<String> info = new ArrayList<>();
  
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
  
  private int select_slot;
  
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
    // Getting the display name
    this.display_name = config.getConfig().getString(path + ".display-name", "DEFAULT");
    if(PersistantPvP.debug) PersistantPvP.logger.info("  Display Name: '" + display_name + "'");
    
    // Getting the simple name:
    this.simple_name = config.getConfig().getString(path + ".simple-name", null);
    if(PersistantPvP.debug) PersistantPvP.logger.info("  Simple Name: '" + display_name + "'");
    
    // Getting the title
    this.subtitle = config.getConfig().getString(path + ".subtitle", "");
    if(PersistantPvP.debug) PersistantPvP.logger.info("  Subtitle Name: '" + subtitle + "'");
    
    // Getting info
    List<String> got_info = TextUtil.formatString(config.getConfig().getStringList(path+".info"));
    if(got_info != null) this.info = got_info;
    
    // Getting the loadout's weight
    this.weight = config.getConfig().getInt(path + ".weight",0);
    if(PersistantPvP.debug) PersistantPvP.logger.info("  Weight: " + weight);
    
    // Getting the game mode of the loadout
    this.game_mode = GameMode.valueOf(config.getConfig().getString(path + ".gamemode"));
    if(this.game_mode == null) game_mode = GameMode.ADVENTURE;
    if(PersistantPvP.debug) PersistantPvP.logger.info("  Gamemode: " + game_mode);
    
    this.select_slot = config.getConfig().getInt(path + ".selected_slot", 0) % 9;
    if(PersistantPvP.debug) PersistantPvP.logger.info("  Selected Slot: " + select_slot);
    
    // Helmet Getting
    if(PersistantPvP.debug) PersistantPvP.logger.info("  Getting Helmet... ");
    if(config.getConfig().getConfigurationSection(path).getKeys(false).contains("helmet")) this.helmet = ItemUtil.getConfigItemStack(config, path + ".helmet");
    else if(PersistantPvP.debug) PersistantPvP.logger.info("    Found no helmet... ");
    
    // Chestplate Getting
    if(PersistantPvP.debug) PersistantPvP.logger.info("  Getting Chestplate... ");
    if(config.getConfig().getConfigurationSection(path).getKeys(false).contains("chestplate")) this.chestplate = ItemUtil.getConfigItemStack(config, path + ".chestplate");
    else if(PersistantPvP.debug) PersistantPvP.logger.info("    Found no chestplate... ");
    
    // Leggings Getting
    if(PersistantPvP.debug) PersistantPvP.logger.info("  Getting Leggings... ");
    if(config.getConfig().getConfigurationSection(path).getKeys(false).contains("leggings")) this.leggings = ItemUtil.getConfigItemStack(config, path + ".leggings");
    else if(PersistantPvP.debug) PersistantPvP.logger.info("    Found no leggings... ");
    
    // Boots Getting
    if(PersistantPvP.debug) PersistantPvP.logger.info("  Getting Boots... ");
    if(config.getConfig().getConfigurationSection(path).getKeys(false).contains("boots")) this.boots = ItemUtil.getConfigItemStack(config, path + ".boots");
    else if(PersistantPvP.debug) PersistantPvP.logger.info("    Found no boots... ");
    
    // Getting inventory items
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
      ItemStack item = ItemUtil.getConfigItemStack(config, path + ".inventory-slots." + n_str);
      items.put(n, item);
    }
    
    // Getting the effects that will be applied.
    if(config.getConfig().getConfigurationSection(path).getKeys(false).contains("effects")) //check to see if the config section exists.
    {
      List<String> effect_keys = new ArrayList<>(config.getConfig().getConfigurationSection(path + ".effects").getKeys(false));
      for(String e_str : effect_keys)
      {
        if(PersistantPvP.debug) PersistantPvP.logger.info(" Reading Potion Types: ");
        String type = config.getConfig().getString(path + ".effects." + e_str + ".type");
        if(PersistantPvP.debug) PersistantPvP.logger.info("    Got type: '" + type + "'");
        PotionEffectType potion_effect = PotionEffectType.getByName(type);
        if(potion_effect == null) continue;
        int strength = config.getConfig().getInt(path + ".effects." + e_str + ".strength");
        if(PersistantPvP.debug) PersistantPvP.logger.info("    Got strength: '" + strength + "'");
        int duration = config.getConfig().getInt(path + ".effects." + e_str + ".duration");
        if(PersistantPvP.debug) PersistantPvP.logger.info("    Got duration: '" + duration + "'");
        boolean ambient = config.getConfig().getBoolean(path + ".effects." + e_str + ".ambient");
        if(PersistantPvP.debug) PersistantPvP.logger.info("    Got ambient: '" + ambient + "'");
        boolean particles = config.getConfig().getBoolean(path + ".effects." + e_str + ".particles");
        if(PersistantPvP.debug) PersistantPvP.logger.info("    Got particles: '" + particles + "'");
        
        PotionEffect effect = new PotionEffect(potion_effect, duration, strength, ambient, particles);
        effects.add(effect);
      }
    }
    
    // Getting the sorting tags:
    List<String> sorting_tags = config.getConfig().getStringList(path+".tags");
    if(sorting_tags != null) this.sorting_tags = sorting_tags;
    
   Loadout.loadouts.add(this);
   
  }
  
  /**Gets the loadout's weight
   * 
   * @return returns the weight of the loadout.
   */
  public int getWeight(){return this.weight;}
  
  /**Returns the tags that the loadout has associated with it.
   * 
   * @return A list of tags
   */
  public List<String> getSortingTags(){return this.sorting_tags;}
  
  /**Gets the display name of the loadout.
   * 
   * @return
   */
  public String getDisplayName(){return this.display_name;}
  
  /**Gets the subtitle of the loadout for respawn
   * 
   * @return
   */
  public String getSubtitle(){return this.subtitle;}
  
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
    if(PersistantPvP.debug)PersistantPvP.logger.info("Equipping " + this.getDisplayName() + " to " + player.getName());
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
      if(PersistantPvP.debug)PersistantPvP.logger.info("  Equipping potion effect: " + e.getType().toString());
    	player.addPotionEffect(e);
    }
    
    player.getInventory().setHeldItemSlot(this.select_slot);
    
  }
  
  public List<String> getInfo(){return this.info;}
  
  public String getSimpleName()
  {
    if(this.simple_name != null) return this.simple_name;
    return ChatColor.stripColor(this.getDisplayName().toLowerCase().replace(" ", "_"));
  }
  
  public boolean equals(Object other)
  {
    return other.hashCode() == this.hashCode();
  }
  public int hashCode()
  {
    int hash_sum = 0;
    if( this.display_name != null) hash_sum += this.display_name.hashCode();
    if( this.info != null) hash_sum += this.info.hashCode();
    if( this.helmet != null) hash_sum += this.helmet.hashCode();
    if( this.chestplate != null) hash_sum += this.chestplate.hashCode();
    if( this.leggings != null) hash_sum += this.leggings.hashCode();
    if( this.boots != null) hash_sum += this.boots.hashCode();
    if( this.items != null) hash_sum += this.items.hashCode();
    if( this.effects != null) hash_sum += this.effects.hashCode();
    return hash_sum;
  }
  
  // STATIC METHODS
  public static Set<Loadout> getLoadouts(){return Loadout.loadouts;}
  public static Loadout getLoadout(String name)
  {
    for(Loadout l : Loadout.loadouts)
    {
      if(l.getSimpleName() == name) return l;
      if(l.getDisplayName() == name) return l;
    }
    return null;
  }
  
}
