package me.aaron.teraperms.permission.group;

import me.aaron.teraperms.config.PermissionConfig;
import me.aaron.teraperms.main.PermsMain;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class GroupManager {

    private static final Map<String, TeraGroup> groups = new HashMap<>();

    public static Map<String, TeraGroup> getGroupsMap() {
        return groups;
    }

    public static List<TeraGroup> getGroupsList() {
        return new ArrayList<>(groups.values());
    }

    public static List<String> getGroupsListString() {
        List<String> list = new ArrayList<>();
        for (TeraGroup group : groups.values()) {
            list.add(group.getName());
        }
        return list;
    }

    public static TeraGroup getGroup(String group) {
        return groups.get(group);
    }

    public static void reloadGroup() {
        FileConfiguration config = PermsMain.getPlugin().config;
        groups.clear();

        if (config.isConfigurationSection("groups")) {
            for (String group : Objects.requireNonNull(config.getConfigurationSection("groups")).getKeys(false)) {
                boolean standard = config.getBoolean("groups." + group + ".default");
                String prefix = config.getString("groups." + group + ".prefix", "");
                int weight = config.getInt("groups." + group + ".weight", 0);
                List<String> inheritance = config.getStringList("groups." + group + ".inheritance");
                List<String> permission = config.getStringList("groups." + group + ".permission");

                TeraGroup teraGroup = new TeraGroup(group, prefix, weight, standard,
                        new ArrayList<>(permission), new ArrayList<>(inheritance));
                groups.put(group, teraGroup);
            }
        }
    }

    public static void saveTeraGroup(TeraGroup teraGroup) {
        FileConfiguration config = PermsMain.getPlugin().config;
        String base = "groups." + teraGroup.getName();

        config.set(base + ".default", teraGroup.isStandard());
        config.set(base + ".prefix", teraGroup.getPrefix());
        config.set(base + ".weight", teraGroup.getWeight());
        config.set(base + ".inheritance", teraGroup.getInheritance());
        config.set(base + ".permission", teraGroup.getPermission());

        PermsMain.getPlugin().save();
    }

    public static void deleteTeraGroup(String group) {
        FileConfiguration config = PermsMain.getPlugin().config;
        config.set("groups." + group, null);
        PermsMain.getPlugin().save();

        PermissionConfig.loadConfig();
        reloadGroup();
        PermsMain.getPlugin().loadAllPermissions();
    }
}
