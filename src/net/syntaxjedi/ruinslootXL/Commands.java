package net.syntaxjedi.ruinslootXL;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Commands implements CommandExecutor{
	
	RuinsLoot plugin;
	
	public Commands(RuinsLoot instance){
		this.plugin = instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]){
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "This plugin does not have console support.");
			return true;
		}else{
			Player p = (Player) sender;
			if(command.getName().equalsIgnoreCase("lootchest")){
				Boolean rand = plugin.getConfig().getBoolean("random.enabled");
				Boolean randChest = plugin.getConfig().getBoolean("random.randomChest");
				int emptySlot = p.getInventory().firstEmpty();
				if(args.length == 0){
					
					p.sendMessage(ChatColor.RED + "Use /lootchest help to see all the commands");
					return true;
					
				}else if(args.length == 1){
					
					switch(args[0].toLowerCase()){
					
					case "help":
						p.sendMessage(ChatColor.BLUE + "============================================" + 
								ChatColor.GOLD + "\nCurrent Version: " + ChatColor.LIGHT_PURPLE + plugin.getDescription().getVersion() + ChatColor.GOLD + "\nAuthor: " 
								+ ChatColor.GREEN + plugin.getDescription().getAuthors() + 
								ChatColor.GOLD + "\nCommands:" + "\n- /lootchest help :" + ChatColor.WHITE + " Displays this screen" + 
								ChatColor.GOLD + "\n- /lootchest config: " + ChatColor.WHITE + " Opens a gui to configure each chest's contents." +
								ChatColor.GOLD + "\n- /lootchest reload :" + ChatColor.WHITE + " Reloads the config file." +
								ChatColor.GOLD + "\n- /lootchest <common | uncommon | legendary> :" + ChatColor.WHITE + " Gives a lootchest of the type common, uncommon, or legendary" +
								ChatColor.GOLD + "\n- /lootchest <common | uncommon | legendary> [name] :" + ChatColor.WHITE
								+ " Gives a lootchest of the type common, uncommon, or legendary, with a custom name. Limit 2 appending names." +
								ChatColor.GOLD + "\n- /lootchest random :" + ChatColor.WHITE + " Toggles random lootchest spawning across the world." +
								ChatColor.GOLD + "\n- /lootchest random chest :" + ChatColor.WHITE + " Toggles between spawning random chest type or the default chest type. (" 
								+ ChatColor.BLUE + plugin.getConfig().getString("random.defaultType") + ChatColor.WHITE + ")" +
								ChatColor.GOLD + "\n- /lootchest clear :" + ChatColor.WHITE + " Clears the current chest locations list"  +
								ChatColor.GOLD + "\n- /lootchest random next :" + ChatColor.WHITE + " Clears the current random spawned chests and spawns new ones." +
								ChatColor.GOLD + "\n- /lootchest fill :" + ChatColor.WHITE + " Fills all lootchests with their respective items from the config.");
						return true;
					case "config":
						InventoryGUI.confGUI(p);
						return true;
					case "random":
						if(rand){
							rand = false;
							p.sendMessage(ChatColor.GOLD + "Random Chests Turned Off");
							plugin.getConfig().set("random.enabled", rand);
							plugin.saveConfig();
							return true;
						}else if(!rand){
							rand = true;
							p.sendMessage(ChatColor.GOLD + "Random Chests Turned On");
							plugin.getConfig().set("random.enabled", rand);
							plugin.saveConfig();
							return true;
						}
						
					case "reload":
						p.sendMessage(ChatColor.GOLD + "Reloading config.");
						plugin.reloadConfig();
						return true;
					case "common":
						p.getInventory().setItem(emptySlot, RuinsLoot.createItem(Material.CHEST, 1, "Common LootChest", "common"));
						p.sendMessage(ChatColor.GOLD + "Gave " + ChatColor.WHITE + "Common LootChest " + ChatColor.GOLD + "to " + p.getDisplayName());
						return true;
					
					case "uncommon":
						p.getInventory().setItem(emptySlot, RuinsLoot.createItem(Material.CHEST, 1, "Uncommon LootChest", "uncommon"));
						p.sendMessage(ChatColor.GOLD + "Gave " + ChatColor.WHITE + "Uncommon LootChest " + ChatColor.GOLD + "to " + p.getDisplayName());
						return true;
					
					case "legendary":
						p.getInventory().setItem(emptySlot, RuinsLoot.createItem(Material.CHEST, 1, "Legendary LootChest", "legendary"));
						p.sendMessage(ChatColor.GOLD + "Gave " + ChatColor.WHITE + "L LootChest " + ChatColor.GOLD + "to " + p.getDisplayName());
						return true;
						
					case "fill":
						p.sendMessage(ChatColor.GOLD + "Filling Chests");
						plugin.findChest("all");
						return true;
						
					case "clear":
						p.sendMessage(ChatColor.GOLD + "Clearing locations.");
						rand = false;
						FileHandler.resetLoc();
						return true;
						
					default:
						p.sendMessage(ChatColor.RED + "Unknown type " + "\"" + args[0] + "\"");
					}
				}else if(args.length == 2 && args[0].toLowerCase().equals("random")){
					switch(args[1].toLowerCase()){
					case "next":
						p.sendMessage(ChatColor.GOLD + "Resetting chests.");
						plugin.findChest("empty");
						plugin.setChests(plugin.getConfig().getInt("random.amount"));
						return true;
					case "chest":
						if(!randChest){
							randChest = true;
							p.sendMessage(ChatColor.GOLD + "Switching random chest spawn type to random");
							plugin.getConfig().set("random.randomChest", randChest);
							plugin.saveConfig();
							return true;
						}else{
							randChest = false;
							p.sendMessage(ChatColor.GOLD + "Switching random chest spawn type to default. (" + ChatColor.BLUE + plugin.getConfig().getString("random.defaultType") + ChatColor.GOLD + ")");
							plugin.getConfig().set("random.randomChest", randChest);
							plugin.saveConfig();
							return true;
						}
					default:
						p.sendMessage("Unknown argument: " + "\"" + args[1] + "\"");
					}
				}else if(args.length >= 2 && args.length < 4 && args[0].toLowerCase().equals("lootchest")){
					String name = "";
					for(int i = 1; i < args.length; i++){
						name += args[i] + " ";
						
					}
					
					switch(args[0].toLowerCase()){
					
					case "common":
						p.getInventory().setItem(emptySlot, RuinsLoot.createItem(Material.CHEST, 1, name, "common"));
						p.sendMessage(ChatColor.GOLD + "Gave " + ChatColor.WHITE + "Common LootChest " + ChatColor.GOLD + "to " + p.getDisplayName());
						return true;
					
					case "uncommon":
						p.getInventory().setItem(emptySlot, RuinsLoot.createItem(Material.CHEST, 1, name, "uncommon"));
						p.sendMessage(ChatColor.GOLD + "Gave " + ChatColor.WHITE + "Uncommon LootChest " + ChatColor.GOLD + "to " + p.getDisplayName());
						return true;
						
					case "legendary":
						p.getInventory().setItem(emptySlot, RuinsLoot.createItem(Material.CHEST, 1, name, "legendary"));
						p.sendMessage(ChatColor.GOLD + "Gave " + ChatColor.WHITE + "L LootChest " + ChatColor.GOLD + "to " + p.getDisplayName());
						return true;
						
					default:
						p.sendMessage(ChatColor.RED + "Unknown type " + "\"" + args[0] + "\"");
						
					}
				}else{
					p.sendMessage(ChatColor.RED + "Unknown command, use /lootchest help to see a list of commands.");
					return true;
				}
			}
		}
		return false;
	}
}
