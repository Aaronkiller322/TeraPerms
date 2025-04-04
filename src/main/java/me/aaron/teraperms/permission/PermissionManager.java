package me.aaron.teraperms.permission;

import me.aaron.teraperms.main.PermsMain;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PermissionManager {

    private final UUID uuid;

    public PermissionManager(UUID uuid) {
        this.uuid = uuid;
    }

    public void addPermission(String permission) {
        String perm = permission.toLowerCase();
        List<String> perms = getPermissionsList();
        if (!perms.contains(perm)) {
            perms.add(perm);
            savePermissions(perms);
            updateLivePermissions(perm, true);
        }
    }

    public void removePermission(String permission) {
        String perm = permission.toLowerCase();
        List<String> perms = getPermissionsList();
        if (perms.remove(perm)) {
            savePermissions(perms);
            updateLivePermissions(perm, false);
        }
    }

    public List<String> getPermissionsList() {
        List<String> list = new ArrayList<>();
        CustomPermissible customPerm = PermsMain.getPlugin().getCustomPermissibles().get(uuid);

        if (customPerm != null) {
            for (Map.Entry<String, Boolean> entry : customPerm.customPermissions.entrySet()) {
                if (entry.getValue()) {
                    list.add(entry.getKey());
                }
            }
        }

        return list;
    }

    private void savePermissions(List<String> perms) {
        String path = "player." + uuid + ".perm";
        getConfig().set(path, perms);
        saveConfig();
    }

    private void updateLivePermissions(String permission, boolean add) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            CustomPermissible customPerm = PermsMain.getPlugin().getCustomPermissibles().get(uuid);
            if (customPerm != null) {
                if (add) {
                    customPerm.setPermission(permission, true);
                } else {
                    customPerm.removePermission(permission);
                }
                PermissibleInjector.injectCustomPermissible(player);
            }
        }
    }

    private FileConfiguration getConfig() {
        return PermsMain.getPlugin().getConfig();
    }

    private void saveConfig() {
        PermsMain.getPlugin().saveConfig();
    }
}