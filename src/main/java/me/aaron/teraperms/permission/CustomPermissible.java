package me.aaron.teraperms.permission;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;

import java.util.HashMap;
import java.util.Map;

public class CustomPermissible extends PermissibleBase {

    private final Player player;
    public final Map<String, Boolean> customPermissions = new HashMap<>();

    public CustomPermissible(Player player) {
        super(player);
        this.player = player;
    }

    @Override
    public boolean hasPermission(String permission) {
        if (customPermissions.containsKey("*")) {
            return true;
        }
        return customPermissions.getOrDefault(permission, super.hasPermission(permission));
    }

    public void setPermission(String permission, boolean value) {
        customPermissions.put(permission, value);
    }

    public void removePermission(String permission) {
        customPermissions.remove(permission);
    }
}
