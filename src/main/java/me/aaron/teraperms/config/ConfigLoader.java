package me.aaron.teraperms.config;

import me.aaron.teraperms.main.PermsMain;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.InputStream;
import java.io.InputStreamReader;

public class ConfigLoader {
    private static FileConfiguration config;

    private static void initConfig() {
        // Datei aus dem JAR laden

        try (InputStream in = PermsMain.getPlugin().getResource("permission.yml")) {
            if (in != null) {
                config = YamlConfiguration.loadConfiguration(new InputStreamReader(in));
            } else {
                throw new IllegalArgumentException("Datei permission.yml nicht gefunden im Ressourcenpfad.");
            }
        } catch (Exception e) {
        }
    }

    public static FileConfiguration getConfig() {
        initConfig();
        return config;
    }
}
