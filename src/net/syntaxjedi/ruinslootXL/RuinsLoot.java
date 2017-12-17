package net.syntaxjedi.ruinslootXL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.block.Chest;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import co.bronet.machinations.api.IContainer;
import co.bronet.machinations.api.IMachinationsPlugin;

public class RuinsLoot extends JavaPlugin{
	
	private static final Logger log = Logger.getLogger("Minecraft");
	//private IMachinationsPlugin townAPI;
	
	public static Inventory inv;
	public static Block bl;
	public int chestNumber;
	public int time;
	public Long timel;
	public int spawnTime;
	public Long spawnTimeL;
	public Boolean doRand;
	@Override
	public void onEnable(){

		this.getServer().getPluginManager().registerEvents(new BlockEvent(), this);
		this.getServer().getPluginManager().registerEvents(new InventoryGUI(), this);
		
		//register commands file
		log.info("[RuinsLoot] Checking Plugin Files");
		this.saveDefaultConfig();
		FileHandler.createFile();
		log.info("[RuinsLoot] File Check Successful, Registering Commands");
		Commands commands = new Commands(this);
		this.getCommand("lootchest").setExecutor(commands);
		log.info("[RuinsLoot] File Check Successful");
		
		/*
		if(Bukkit.getServer().getPluginManager().getPlugin("TownManager") != null){
			townAPI = (IMachinationsPlugin)Bukkit.getServer().getPluginManager().getPlugin("TownManager");
			log.info("[RuinsLootXL] ####################");
			log.info("[RuinsLootXL] " + townAPI);
			log.info("[RuinsLootXL] ####################");
		}else{
			log.info("[RuinsLootXL] ####################");
			log.info("[RuinsLootXL] " + "Missing Dependency Machinations");
			log.info("[RuinsLootXL] ####################");
		}
		*/
		
		time = getConfig().getInt("time.ticks");
		timel = (long)time;
		
		spawnTime = getConfig().getInt("random.ticks");
		spawnTimeL = (long)spawnTime;
		
		doRand = getConfig().getBoolean("random.enabled");
		
		 BukkitScheduler fillScheduler = getServer().getScheduler();
		 BukkitScheduler randomScheduler = getServer().getScheduler();
	        fillScheduler.scheduleSyncRepeatingTask(this, new Runnable() {
	            @Override
	            public void run() {
	                findChest("all");
	            }
	        }, 0L, timel);
	        
	        randomScheduler.scheduleSyncRepeatingTask(this, new Runnable() {
	            @Override
	            public void run() {
	            	if(doRand){
	            		findChest("empty");
	            	}
	            }
	        }, 0L, spawnTimeL);
	}
	
	@Override
	public void onDisable(){
	}
	
	public void findChest(String fillType){
		log.info("[RuinsLootXL] Filling Chests");
		//World world = Bukkit.getWorld("world");
		for(World w : getServer().getWorlds()){
			Location loc = new Location(w, 0, 0, 0);
			
			Map<Integer, ArrayList<Object>> cLoc = FileHandler.getLoc();
			
			
			for (int i = 0; i < cLoc.size(); i++){
				loc.setX((double) cLoc.get(i).get(3));
				loc.setY((double) cLoc.get(i).get(4));
				loc.setZ((double) cLoc.get(i).get(5));
				
				Block lChest = loc.getBlock();
				Chunk chunk = w.getChunkAt(loc);
				if(!chunk.isLoaded()){
					w.loadChunk(chunk);
				}
				if(lChest.getType().CHEST != null && cLoc.get(i).get(0).equals(w.getName())){
					if(fillType.equals("all")){
						Chest chest = (Chest) lChest.getState();
						inv = chest.getBlockInventory();
						ItemStack[] stacks = FileHandler.getLoot(cLoc.get(i).get(2).toString());
						inv.setContents(stacks);
					}else if (fillType.equals("random") && cLoc.get(i).get(1).equals("random") && cLoc.get(i).get(0).equals(w.getName())){
						Chest rFill = (Chest) lChest.getState();
						inv = rFill.getBlockInventory();
						ItemStack[] stacks = FileHandler.getLoot(cLoc.get(i).get(2).toString());
						inv.setContents(stacks);
					}else if(fillType.equals("empty") && cLoc.get(i).get(1).equals("random") && cLoc.get(i).get(0).equals(w.getName())){
						Chest rChest = (Chest) lChest.getState();
						inv = rChest.getBlockInventory();
						inv.clear();
						lChest.setType(Material.AIR);
						try{
							FileHandler.removeLoc(lChest.getX(), lChest.getY(), lChest.getZ(), lChest.getWorld().getName());
						}catch (IOException e){
							e.printStackTrace();
						}
					}
				}
			}
		}
		
	}
	
	public void setChests(int randAmount){
		//IContainer container = townAPI.getContainer();
		for(World w : getServer().getWorlds()){
			log.info("Worlds: " + this.getConfig().getString("worlds." + w.getName()));
			if(this.getConfig().isSet("worlds." + w.getName())){
				for (int i = 0; i < randAmount; i++){
					int percent = 0;
					Location randLoc = new Location(w, 0, 0, 0);
					String type = null;
					double xMin = this.getConfig().getDouble("worlds." + w.getName() + ".minX");
					double xMax = this.getConfig().getDouble("worlds." + w.getName() + ".maxX");
					double zMin = this.getConfig().getDouble("worlds." + w.getName() + ".minZ");
					double zMax = this.getConfig().getDouble("worlds." + w.getName() + ".maxZ");
					
					Random r = new Random();
					Random rChance = new Random();
					double finalX = Math.round(xMin + (xMax - xMin) * r.nextDouble());
					double finalZ = Math.round(zMin + (zMax - zMin) * r.nextDouble());
					
					randLoc.setX(finalX);
					randLoc.setZ(finalZ);
					randLoc.setY(w.getHighestBlockYAt(randLoc));
					
					/*
					if(container.getTownByLocation(randLoc) != null){
						while(container.getTownByLocation(randLoc) != null){
							finalX = Math.round(xMin + (xMax - xMin) * r.nextDouble());
							finalZ = Math.round(zMin + (zMax - zMin) * r.nextDouble());
							
							randLoc.setX(finalX);
							randLoc.setZ(finalZ);
							randLoc.setY(w.getHighestBlockYAt(randLoc));
						}
					}
					*/
					
					if(this.getConfig().getBoolean("random.randomChest")){
						int comRand = this.getConfig().getInt("chestChance.common");
						int ucomRand = this.getConfig().getInt("chestChance.uncommon");
						int legRand = this.getConfig().getInt("chestChance.legendary");
						
						int lowRandChance = 0;
						int highRandChance = comRand + ucomRand + legRand;
						int randOut = rChance.nextInt(highRandChance - lowRandChance + 1) + lowRandChance;
						
						percent = ((randOut*100)/highRandChance);
						
						if(percent > ucomRand){
							type = "common";
						}else if(percent <= ucomRand && percent > legRand){
							type = "uncommon";
						}else if(percent <= legRand){
							type = "legendary";
						}
					}else{
						type = this.getConfig().getString("random.defaultType");
					}
					
					Block randChest = w.getBlockAt(randLoc);
					randChest.setType(Material.CHEST);
					Chest chestState = (Chest) randChest.getState();
					chestState.setCustomName(type);
					FileHandler.addLoc(randLoc.getBlockX(), randLoc.getBlockY(), randLoc.getBlockZ(), type, "random", randLoc.getWorld().getName());
				}
			}else{
					continue;
			}
		}
		findChest("random");
	}
	
	public static ItemStack createItem(Material material, int amount, String name, String type){
		ItemStack item = new ItemStack(material, amount);
		ItemMeta meta = item.getItemMeta();
		if(name != null) meta.setDisplayName(name);
		if(type != null){
			List<String> chestType = new ArrayList<String>();
			chestType.add(type);
			meta.setLore(chestType);
		}
		item.setItemMeta(meta);
		return item;
	}
	
}
