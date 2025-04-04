package me.aaron.teraperms.main;

import me.aaron.teraperms.Component.PrefixManager;
import me.aaron.teraperms.config.PermissionConfig;
import me.aaron.teraperms.permission.CustomPermissible;
import me.aaron.teraperms.permission.PermissibleInjector;
import me.aaron.teraperms.permission.TeraPermsPlaceholder;
import me.aaron.teraperms.permission.group.GroupManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public final class PermsMain extends JavaPlugin {

    private static PermsMain instance;

    public String prefix;
    public String name;

    public File file;
    public FileConfiguration config;

    private final HashMap<UUID, CustomPermissible> customPermissibles = new HashMap<>();

    public static PermsMain getPlugin() {
        return instance;
    }

    public HashMap<UUID, CustomPermissible> getCustomPermissibles() {
        return customPermissibles;
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        PrefixManager.loadPrefix();
        PermissionConfig.loadConfig();
        GroupManager.reloadGroup();

        getCommand("perm").setExecutor(new PermCommand());
        getCommand("perm").setTabCompleter(new CommandTabCompleter());

        PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        pluginManager.registerEvents(new JoinEvent(), this);

        loadAllPermissions();
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new TeraPermsPlaceholder().register();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void loadAllPermissions() {
        customPermissibles.clear();
        for (Player player : Bukkit.getOnlinePlayers()) {
            PermissibleInjector.injectCustomPermissible(player);
        }
    }

    public void save() {
        try {
            config.save(file);
        } catch (Exception ignored) {
        }
    }
}
