package net.samongi.PersistantPvP.Maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.md_5.bungee.api.ChatColor;
import net.samongi.PersistantPvP.PersistantPvP;
import net.samongi.PersistantPvP.Players.Loadout;
import net.samongi.SamongiLib.Configuration.ConfigAccessor;

public class GameHandler
{
	Loadout general_loadout;
	GameMap current_map;
	
	HashMap<String, GameMap> maps = new HashMap<String, GameMap>();
	
	public GameHandler()
	{
		this.createGeneralLoadout();
	}
	
	public void parseMapConfig(ConfigAccessor map_config)
	{
		// Get keys and generate maps based off them
    List<String> keys = new ArrayList<>(map_config.getConfig().getConfigurationSection("maps").getKeys(false));
    for(String k : keys)
    {
      if(PersistantPvP.debug) PersistantPvP.logger.info("MAP-MANAFER - Parsing map with key: '"+k+"'");
      GameMap map = new GameMap(map_config, k);
      this.maps.put(map.getTag(), map);
      if(PersistantPvP.debug) PersistantPvP.logger.info("MAP-MANAFER - Adding '"+map.getTag()+"' to the maps list.");
    }
    /*
    String lobby_world_name = map_config.getConfig().getString("lobby.world");
    String lobby_loc_raw = map_config.getConfig().getString("lobby.spawn");
    List<Double> lobby_coords = StringUtilities.extractNumbers(lobby_loc_raw);
    
    this.lobby_world = Bukkit.getWorld(lobby_world_name);
    if(lobby_world == null) PersistancePvP.logger.info("MAP-MANAGER - '"+ lobby_world_name +"' appears to not be an actual world...");
    this.lobby_spawn = new Location(null, lobby_coords.get(0),lobby_coords.get(1),lobby_coords.get(2));
    */
	}
	
	public void switch_maps(String map)
	{
		if(!this.maps.containsKey(map)) return; // Tell them this.
		this.current_map = this.maps.get(map);
		// Teleport the players to the new map.
		PersistantPvP.group.performAction((Player player) -> general_loadout.equipe(player));
		PersistantPvP.group.performAction((Player player) -> current_map.spawnPlayer(player));
	}
	
	public GameMap getCurrentMap(){return this.current_map;}
	public Loadout getCurrentLoadout(){return this.general_loadout;}
	
	private void createGeneralLoadout()
	{
		// make the loadout first
		this.general_loadout = new Loadout();
		
		// Weapon
		ItemStack sword = new ItemStack(Material.IRON_SWORD);
		ItemMeta sword_im = sword.getItemMeta();
		sword_im.setDisplayName(ChatColor.YELLOW + "Slab of Pain");
		sword_im.addEnchant(Enchantment.DURABILITY, 10, true);
		sword.setItemMeta(sword_im);
		general_loadout.addInventoryItem(sword);
		
		// Bow
		ItemStack bow = new ItemStack(Material.BOW);
		ItemMeta bow_im = bow.getItemMeta();
		bow_im.setDisplayName(ChatColor.YELLOW + "Slinger");
		bow_im.addEnchant(Enchantment.ARROW_INFINITE, 10, true);
		bow_im.addEnchant(Enchantment.ARROW_DAMAGE, 2, true);
		bow_im.addEnchant(Enchantment.DURABILITY, 10, true);
		bow.setItemMeta(bow_im);
		general_loadout.addInventoryItem(bow);
		
		// Arrow
		ItemStack arrow = new ItemStack(Material.ARROW);
		ItemMeta arrow_im = arrow.getItemMeta();
		arrow_im.setDisplayName(ChatColor.RED + "Whack-a-Arrow");
		arrow_im.addEnchant(Enchantment.KNOCKBACK, 10, true);
		arrow.setItemMeta(arrow_im);
		general_loadout.addInventoryItem(arrow);
		
		// Armor
		ItemStack helmet = new ItemStack(Material.CHAINMAIL_HELMET);
		ItemMeta helmet_im = helmet.getItemMeta();
		helmet_im.setDisplayName(ChatColor.YELLOW + "Skull Cage");
		helmet_im.addEnchant(Enchantment.DURABILITY, 10, true);
		helmet.setItemMeta(helmet_im);
		general_loadout.setHelmet(helmet);
		
		ItemStack chestplate = new ItemStack(Material.IRON_CHESTPLATE);
		ItemMeta chestplater_im = helmet.getItemMeta();
		chestplater_im.setDisplayName(ChatColor.YELLOW + "Chest Shield");
		chestplater_im.addEnchant(Enchantment.DURABILITY, 10, true);
		chestplate.setItemMeta(chestplater_im);
		general_loadout.setChestplate(chestplate);
		
		ItemStack leggings = new ItemStack(Material.CHAINMAIL_LEGGINGS);
		ItemMeta leggings_im = helmet.getItemMeta();
		leggings_im.setDisplayName(ChatColor.YELLOW + "Walkers");
		leggings_im.addEnchant(Enchantment.DURABILITY, 10, true);
		leggings.setItemMeta(leggings_im);
		general_loadout.setLeggings(leggings);
		
		ItemStack boots = new ItemStack(Material.IRON_BOOTS);
		ItemMeta boots_im = helmet.getItemMeta();
		boots_im.setDisplayName(ChatColor.YELLOW + "Stompers");
		boots_im.addEnchant(Enchantment.DURABILITY, 10, true);
		boots.setItemMeta(boots_im);
		general_loadout.setBoots(boots);
		
		// Potions
		PotionEffect effect1 = new PotionEffect(PotionEffectType.SATURATION, 360000, 3, false, false);
		general_loadout.addPotionEffect(effect1);
	}
}
