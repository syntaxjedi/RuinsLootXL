package net.syntaxjedi.ruinslootXL;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class FileHandler {
	private static final Logger log = Logger.getLogger("Minecraft");
	static Plugin plugin = RuinsLoot.getPlugin(RuinsLoot.class);
	//FileConfiguration config = plugin.getConfig();
	
	public static void createFile(){
		File cPath = new File("./plugins/ruinsLootXL/");
		File loc = new File(cPath, "locations.loc");
		if(!cPath.exists()){
			try{
				cPath.mkdirs();
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		if(!loc.exists()){
			try {
				loc.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void resetLoc(){
		File loc = new File("./plugins/ruinsLootXL/locations.loc");
		if(!loc.exists()){
			try{
				loc.createNewFile();
			}catch(IOException e){
				e.printStackTrace();
			}
		}else if(loc.exists()){
			try{
				loc.delete();
				loc.createNewFile();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		
	}
	
	public static void addLoc(int x, int y, int z, String type, String placed,String world){
		try{
			File locDir = new File("./plugins/ruinsLootXL/");
			if(!locDir.exists()){
				locDir.mkdir();
			}
			
			File locFile = new File(locDir, "locations.loc");
			if(!locFile.exists()){
				locFile.createNewFile();
			}
			
			FileWriter fw = new FileWriter(locFile, true);
			PrintWriter pw = new PrintWriter(fw);
			
			//pw.println(x + ":" + y + ":" + z + ":" + world);
			pw.println(x + ":" + y + ":" + z + ":" + type + ":" + placed + ":"+ world);
			pw.flush();
			pw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void removeLoc(int x, int y, int z, String world) throws IOException{
		File locFile = new File("./plugins/ruinsLootXL/locations.loc");
		File temp = new File("./plugins/ruinsLootXL/temp.loc");
		
		BufferedReader br = new BufferedReader(new FileReader(locFile));
		BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
		
		String delLoc = (x + ":" + y + ":" + z + ":" + world);
		String currentLine;
		while((currentLine = br.readLine()) != null){
			String trimmedLine = currentLine.trim();
			String[] locParse = trimmedLine.split(":");
			int fx = Integer.parseInt(locParse[0]);
			int fy = Integer.parseInt(locParse[1]);
			int fz = Integer.parseInt(locParse[2]);
			String type = locParse[3];
			String placed = locParse[4];
			String fWorld = locParse[5];
			String parsedLine = (fx + ":" + fy + ":" + fz + ":" + fWorld);
			if(parsedLine.equals(delLoc)){continue;}
			bw.write(currentLine + System.getProperty("line.separator"));
		}
		bw.close();
		br.close();
		System.gc();
		locFile.delete();
		boolean successful = temp.renameTo(new File("./plugins/ruinsLootXL/locations.loc"));
	}
	
	public static Map getLoc(){
		int pos = 0;
		Map<Object, Object> cLoc = new HashMap();
		try{
			File loc = new File("./plugins/ruinsLootXL/locations.loc");
			Scanner sc = new Scanner(loc);
			
			String s;
			while(sc.hasNextLine()){
				ArrayList<Object> locList = new ArrayList();
				
				String locLine = sc.nextLine();
				String[] locParse = locLine.split(":");
				Double fx = Double.parseDouble(locParse[0]);
				Double fy = Double.parseDouble(locParse[1]);
				Double fz = Double.parseDouble(locParse[2]);
				String type = locParse[3];
				String placed = locParse[4];
				String fWorld = locParse[5];
				locList.add(fWorld);
				locList.add(placed);
				locList.add(type);
				locList.add(fx);
				locList.add(fy);
				locList.add(fz);
				cLoc.put(pos, locList);
				pos++;
				//locList.clear();
				/*if(fx == x && fy == y && fz == z && fWorld.equals(world)){
					return type;
				}*/
			}
			sc.close();
		}catch (Exception e){
			e.printStackTrace();
		}
		return cLoc;
	}
	
	public static ItemStack[] getLoot(String chest){
		plugin.reloadConfig();
		ArrayList<?> stack = (ArrayList<?>) plugin.getConfig().getList("chests." + chest);
		ItemStack[] stacks = stack.toArray(new ItemStack[stack.size()]);
		return stacks;
	}
	
	public static void setConfig(String name, ItemStack[] items){
		plugin.getConfig().set("chests." + name, items);
		plugin.saveConfig();
	}
	
}
	