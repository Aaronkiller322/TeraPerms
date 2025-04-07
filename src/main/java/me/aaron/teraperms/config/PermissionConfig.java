package me.aaron.teraperms.config;

import me.aaron.teraperms.main.PermsMain;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class PermissionConfig {

    private static File file;
    private static FileConfiguration config;

    private static String datafolder = "plugins/" + PermsMain.getPlugin().getName();

    public static void loadConfig() {
        String filetype = "permission";
        File temp = new File(datafolder, filetype + ".yml");
        if (temp.exists()) {
            file = temp;
            config = (FileConfiguration) YamlConfiguration.loadConfiguration(file);
        } else {
            config = ConfigLoader.getConfig();
            file = temp;
            try {
                config.save(temp);
            } catch (Exception e) {
            }
        }
        PermsMain.getPlugin().config = config;
        PermsMain.getPlugin().file = file;

    }

}
