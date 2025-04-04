package me.aaron.teraperms.Component;

import me.aaron.teraperms.main.PermsMain;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class PrefixManager {

	private static File file_prefix;
	private static FileConfiguration config_prefix;

	public static String getPrefix() {
		return PermsMain.getPlugin().prefix;
	}
	public static String getName() {
		return PermsMain.getPlugin().name;
	}
	
	public static void loadPrefix() {
		
		
	    if (file_prefix == null) {
	        file_prefix = new File("plugins/" + PermsMain.getPlugin().getName(), "prefix.yml");
	        config_prefix = YamlConfiguration.loadConfiguration(file_prefix);
	    }
		if(config_prefix.get("default-prefix") == null) {
			config_prefix.set("default-prefix", PermsMain.getPlugin().getName());
			try {
				config_prefix.save(file_prefix);
			} catch (IOException e) {
			}
		}
		if(config_prefix.get("split-prefix") == null) {
			config_prefix.set("split-prefix", "§8 ►§7");
			try {
				config_prefix.save(file_prefix);
			} catch (IOException e) {
			}
		}
		if(config_prefix.get("HexColorTextGenerator-Prefix.enabled") == null) {
			config_prefix.set("HexColorTextGenerator-Prefix.enabled", true);
			try {
				config_prefix.save(file_prefix);
			} catch (IOException e) {
			}
		}
		
		if(config_prefix.get("HexColorTextGenerator-Prefix.start") == null) {
			config_prefix.set("HexColorTextGenerator-Prefix.start", "#FB8900");
			try {
				config_prefix.save(file_prefix);
			} catch (IOException e) {
			}
		}
		
		if(config_prefix.get("HexColorTextGenerator-Prefix.end") == null) {
			config_prefix.set("HexColorTextGenerator-Prefix.end", "#FBCB00");
			try {
				config_prefix.save(file_prefix);
			} catch (IOException e) {
			}
		}
		
		boolean hexcolor = config_prefix.getBoolean("HexColorTextGenerator-Prefix.enabled");
		String prefix = config_prefix.getString("default-prefix");
		
		//HexColorTextGenerator 
		PermsMain.getPlugin().name = prefix;
		if(hexcolor) {
			String start = config_prefix.getString("HexColorTextGenerator-Prefix.start");
			String end = config_prefix.getString("HexColorTextGenerator-Prefix.end");
			
			prefix = HexColorTextGenerator.converHexcolorText(prefix, start, end);
		}

		PermsMain.getPlugin().name = HexColorTextGenerator.convertHexColorToChatColor(prefix);

		PermsMain.getPlugin().prefix = PermsMain.getPlugin().name + config_prefix.getString("split-prefix");
	}

	
	
	
}
