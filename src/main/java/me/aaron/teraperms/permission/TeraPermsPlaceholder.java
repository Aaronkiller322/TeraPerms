package me.aaron.teraperms.permission;

import me.aaron.teraperms.main.PermsMain;
import me.aaron.teraperms.permission.group.GroupManager;
import me.aaron.teraperms.permission.group.TeraGroup;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class TeraPermsPlaceholder extends PlaceholderExpansion {

    @Override
    public String getIdentifier() {
        return "teraperms";
    }

    @Override
    public String getAuthor() {
        return "Aaronkiller322";
    }

    @Override
    public String getVersion() {
        return "1.2";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, String identifier) {
        // Gruppenspezifische Platzhalter funktionieren auch ohne Spieler
        if (identifier.toLowerCase().startsWith("group_") && identifier.toLowerCase().endsWith("_permissions")) {
            String groupName = identifier.substring("group_".length(), identifier.length() - "_permissions".length());
            TeraGroup group = GroupManager.getGroup(groupName);
            if (group == null) return "unknown group";
            return String.join(", ", group.getPermission());
        }

        if (identifier.toLowerCase().startsWith("group_") && identifier.toLowerCase().endsWith("_permissions_with_inherited")) {
            String groupName = identifier.substring("group_".length(), identifier.length() - "_permissions_with_inherited".length());
            TeraGroup group = GroupManager.getGroup(groupName);
            if (group == null) return "unknown group";
            Set<String> allPermissions = new HashSet<>(group.getPermission());
            for (String inheritedGroup : group.getInheritance()) {
                TeraGroup inherit = GroupManager.getGroup(inheritedGroup);
                if (inherit != null) {
                    allPermissions.addAll(inherit.getPermission());
                }
            }
            return String.join(", ", allPermissions);
        }

        if (identifier.equalsIgnoreCase("group_list")) {
            return String.join(", ", GroupManager.getGroupsListString());
        }

        if (offlinePlayer == null || !offlinePlayer.isOnline()) return "";

        Player player = offlinePlayer.getPlayer();
        if (player == null) return "";

        UUID uuid = player.getUniqueId();
        UserPermission userPermission = new UserPermission(uuid);

        return switch (identifier.toLowerCase()) {
            case "player_prefix" -> {
                TeraGroup group = GroupManager.getGroup(userPermission.getPrimaryGroup());
                yield group != null ? group.getPrefix() : "";
            }
            case "player_primary_group_name" -> userPermission.getPrimaryGroup();
            case "player_permission" -> String.join(", ", userPermission.getPermission());
            case "player_all_permission" -> String.join(", ", userPermission.getAllPermission());
            case "player_groups" -> String.join(", ", userPermission.getGroups());
            default -> null;
        };
    }
}