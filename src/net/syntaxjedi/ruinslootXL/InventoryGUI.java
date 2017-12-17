package net.syntaxjedi.ruinslootXL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryGUI implements Listener{
	private static final Logger log = Logger.getLogger("Minecraft");
	static RuinsLoot plugin;
	
	public static Inventory inv;
	
	public static void confGUI(Player p){
		inv = Bukkit.createInventory(p, 9, "Lootchest Config");
		inv.setItem(2, plugin.createItem(Material.CHEST, 1, "Common", null));
		inv.setItem(4, plugin.createItem(Material.CHEST, 1, "Uncommon", null));
		inv.setItem(6, plugin.createItem(Material.CHEST, 1, "Legendary", null));
		p.openInventory(inv);
	}
	
	public void openInv(Player p, String type){
		int s = 1;
		ItemStack[] loot = FileHandler.getLoot(type);
		inv = Bukkit.createInventory(p, 36, type);
		inv.setItem(2, plugin.createItem(Material.ARROW, 1, "Back", null));
		inv.setItem(4, plugin.createItem(Material.TNT, 1, "Clear", null));
		inv.setItem(6, plugin.createItem(Material.EMERALD, 1, "Save", null));
		for(int i = 0; i < inv.getSize(); i++){
			if(i >= 9){
				inv.setItem(i, loot[(i-9)]);
			}
		}
		//inv.setContents(loot);
		p.openInventory(inv);
	}
	
	public void saveInv(Player p, Inventory openInv, String name){
		ItemStack[] items = new ItemStack[27];
		inv = openInv;
		for(int i = 9; i < inv.getSize(); i++){
			items[(i-9)] = inv.getItem(i);
			if(inv.getItem(i) == null){
				items[(i-9)] = null;
			}
		}
		FileHandler.setConfig(name, items);
	}
	
	public void clearInv(Player p, Inventory openInv, String name){
		inv = openInv;
		for(int i = 9; i < inv.getSize(); i++){
			inv.setItem(i, null);
		}
		saveInv(p, openInv, name);
	}
	
	@EventHandler
	public void invClicked(InventoryClickEvent e){
		Player p = (Player) e.getWhoClicked();
		if(e.getCurrentItem() == null || e.getCurrentItem().equals(Material.AIR)){
			return;
		}
		if(e.getInventory().getName().contains("Lootchest Config")){
			e.setCancelled(true);
			
			if(e.getCurrentItem().equals(Material.AIR)){
				return;
			}
			if(e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().getDisplayName().equals("Common")){
				openInv(p, "common");
			}else if(e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().getDisplayName().equals("Uncommon")){
				openInv(p, "uncommon");
			}else if(e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().getDisplayName().equals("Legendary")){
				openInv(p, "legendary");
			}
		}else{
			if(e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().getDisplayName().equals("Back")){
				e.setCancelled(true);
				saveInv(p, e.getInventory(), e.getInventory().getName());
				confGUI(p);
			}else if(e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().getDisplayName().equals("Clear")){
				e.setCancelled(true);
				clearInv(p, e.getInventory(), e.getInventory().getName());
				openInv(p, e.getInventory().getName().toString());
			}
			else if(e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().getDisplayName().equals("Save")){
				e.setCancelled(true);
				saveInv(p, e.getInventory(), e.getInventory().getName());
			}
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e){
		Player p = (Player) e.getPlayer();
		inv = e.getInventory();
		Location loc = new Location(p.getWorld(), 0, 0, 0);
		
		if(inv.getType().equals(InventoryType.CHEST)){
			Map<Integer, ArrayList<Object>> cLoc = FileHandler.getLoc();
			for(int i = 0; i < cLoc.size(); i++){
				loc.setX((double)cLoc.get(i).get(3));
				loc.setY((double)cLoc.get(i).get(4));
				loc.setZ((double)cLoc.get(i).get(5));
				
				if(inv.getLocation().equals(loc) && cLoc.get(i).get(1).equals("random")){
					Location chestLoc = inv.getLocation();
					Block selChest = chestLoc.getWorld().getBlockAt(chestLoc);
					inv.clear();
					selChest.setType(Material.AIR);
					try {
						FileHandler.removeLoc(chestLoc.getBlockX(), chestLoc.getBlockY(), chestLoc.getBlockZ(), chestLoc.getWorld().getName());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
				}
			}
		}
	}
}
