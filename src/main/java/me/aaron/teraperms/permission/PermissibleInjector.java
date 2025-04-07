package me.aaron.teraperms.permission;

import me.aaron.teraperms.main.PermsMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import java.lang.reflect.Field;
import java.util.List;

public class PermissibleInjector {

    public static void injectCustomPermissible(Player player) {
        try {
            Field permField = findPermissibleField(player.getClass());
            if (permField == null) {
                Bukkit.getServer().getPluginManager().disablePlugin(PermsMain.getPlugin());
                return;
            }

            permField.setAccessible(true);
            CustomPermissible customPerm = PermsMain.getPlugin().getCustomPermissibles().get(player.getUniqueId());

            if (customPerm == null) {
                customPerm = new CustomPermissible(player);
                UserPermission userPermission = new UserPermission(player.getUniqueId());
                List<String> permissions = userPermission.getAllPermission();
                for (String perm : permissions) {
                    customPerm.customPermissions.putIfAbsent(perm, true);
                }
                PermsMain.getPlugin().getCustomPermissibles().put(player.getUniqueId(), customPerm);
            }

            permField.set(player, customPerm);
            player.updateCommands();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Field findPermissibleField(Class<?> clazz) {
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                if (Permissible.class.isAssignableFrom(field.getType())) {
                    return field;
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }
}