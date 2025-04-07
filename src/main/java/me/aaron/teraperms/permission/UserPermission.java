package me.aaron.teraperms.permission;

import me.aaron.teraperms.main.PermsMain;
import me.aaron.teraperms.permission.group.GroupManager;
import me.aaron.teraperms.permission.group.TeraGroup;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserPermission {

    private final UUID uuid;

    public UserPermission(UUID uuid) {
        this.uuid = uuid;
    }

    public void addGroup(String group) {
        List<String> groups = getGroups();
        String lowerGroup = group.toLowerCase();
        if (!groups.contains(lowerGroup)) {
            groups.add(lowerGroup);
            saveGroupList(groups);
            updatePermission();
        }
    }

    public void removeGroup(String group) {
        List<String> groups = getGroups();
        if (groups.remove(group.toLowerCase())) {
            saveGroupList(groups);
            updatePermission();
        }
    }

    public List<String> getGroups() {
        List<String> list = new ArrayList<>();
        FileConfiguration config = PermsMain.getPlugin().config;
        String path = "users." + uuid + ".group";
        List<String> saved = config.getStringList(path);

        for (String group : saved) {
            if (GroupManager.getGroupsListString().contains(group)) {
                list.add(group);
            }
        }

        if (list.isEmpty()) {
            list.add(getStandardGroup());
        }

        return new ArrayList<>(list);
    }

    public String getPrimaryGroup() {
        List<String> userGroups = getGroups();
        String primary = null;
        int maxWeight = -1;

        for (TeraGroup group : GroupManager.getGroupsList()) {
            if (userGroups.contains(group.getName()) && group.getWeight() > maxWeight) {
                primary = group.getName();
                maxWeight = group.getWeight();
            }
        }

        return (primary != null) ? primary : getStandardGroup();
    }

    public List<String> getPermission() {
        FileConfiguration config = PermsMain.getPlugin().config;
        String path = "users." + uuid + ".permission";
        return new ArrayList<>(config.getStringList(path));
    }

    public void addPermission(String permission) {
        List<String> perms = getPermission();
        String lowerPerm = permission.toLowerCase();
        if (!perms.contains(lowerPerm)) {
            perms.add(lowerPerm);
            savePermissionList(perms);
            updatePermission();
        }
    }

    public void removePermission(String permission) {
        List<String> perms = getPermission();
        if (perms.remove(permission.toLowerCase())) {
            savePermissionList(perms);
            updatePermission();
        }
    }

    public List<String> getAllPermission() {
        List<String> allPerms = new ArrayList<>(getPermission());
        List<String> userGroups = getGroups();

        for (TeraGroup group : GroupManager.getGroupsList()) {
            if (userGroups.contains(group.getName())) {
                allPerms.addAll(group.getPermission());
            }
        }

        return new ArrayList<>(allPerms);
    }

    public void updatePermission() {
        PermsMain.getPlugin().getCustomPermissibles().remove(uuid);
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            PermissibleInjector.injectCustomPermissible(player);
        }
    }

    private void saveGroupList(List<String> groups) {
        String path = "users." + uuid + ".group";
        PermsMain.getPlugin().config.set(path, groups);
        PermsMain.getPlugin().save();
    }

    private void savePermissionList(List<String> permissions) {
        String path = "users." + uuid + ".permission";
        PermsMain.getPlugin().config.set(path, permissions);
        PermsMain.getPlugin().save();
    }

    private String getStandardGroup() {
        for (TeraGroup group : GroupManager.getGroupsList()) {
            if (group.isStandard()) {
                return group.getName();
            }
        }
        return "guest";
    }
}
