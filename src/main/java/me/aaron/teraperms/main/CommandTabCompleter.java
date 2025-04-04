package me.aaron.teraperms.main;

import me.aaron.teraperms.permission.UserPermission;
import me.aaron.teraperms.permission.group.GroupManager;
import me.aaron.teraperms.permission.group.TeraGroup;
import me.aaron.teraperms.util.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class CommandTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> tab = new ArrayList<>();
        if (!(sender instanceof Player || sender instanceof ConsoleCommandSender)) return tab;
        if (!command.getName().equalsIgnoreCase("perm")) return tab;

        try {
            if (sender instanceof Player player) {
                UserPermission userPermission = new UserPermission(player.getUniqueId());
                if (!userPermission.getAllPermission().contains("*")) return tab;
            }

            switch (args.length) {
                case 1 -> {
                    tab.addAll(List.of("user", "group"));
                    return filter(tab, args[0]);
                }
                case 2 -> {
                    if (args[0].equalsIgnoreCase("user")) {
                        Bukkit.getOnlinePlayers().forEach(p -> tab.add(p.getName()));
                    } else if (args[0].equalsIgnoreCase("group")) {
                        tab.add("list");
                        GroupManager.getGroupsListString().forEach(group -> tab.add(group.toLowerCase()));
                    }
                    return filter(tab, args[1]);
                }
                case 3 -> {
                    if (args[0].equalsIgnoreCase("user")) {
                        tab.addAll(List.of("add", "remove", "info", "group"));
                    } else if (args[0].equalsIgnoreCase("group")) {
                        tab.addAll(List.of("create", "delete", "info", "weight", "prefix", "add", "remove", "inheritance"));
                    }
                    return filter(tab, args[2]);
                }
                case 4 -> {
                    if (args[0].equalsIgnoreCase("user")) {
                        handleUserFourthArg(tab, args);
                    } else if (args[0].equalsIgnoreCase("group")) {
                        handleGroupFourthArg(tab, args);
                    }
                    return filter(tab, args[3]);
                }
                case 5 -> {
                    if (args[0].equalsIgnoreCase("user")) {
                        handleUserFifthArg(tab, args);
                    } else if (args[0].equalsIgnoreCase("group")) {
                        handleGroupFifthArg(tab, args);
                    }
                    return filter(tab, args[4]);
                }
            }
        } catch (Exception ignored) {}

        return tab;
    }

    private void handleUserFourthArg(List<String> tab, String[] args) throws ExecutionException, InterruptedException {
        switch (args[2].toLowerCase()) {
            case "add" -> Bukkit.getPluginManager().getPermissions().forEach(p -> tab.add(p.getName()));
            case "remove" -> {
                UUID uuid = fetchUUID(args[1]);
                tab.addAll(new UserPermission(uuid).getPermission());
            }
            case "group" -> tab.addAll(List.of("add", "remove"));
        }
    }

    private void handleGroupFourthArg(List<String> tab, String[] args) {
        String group = args[1].toLowerCase();
        if (!GroupManager.getGroupsListString().contains(group)) {
            tab.add(PermCommand.getMessage("no_group_tabcompleter"));
            return;
        }

        TeraGroup teraGroup = GroupManager.getGroup(group);
        switch (args[2].toLowerCase()) {
            case "weight" -> tab.add(String.valueOf(teraGroup.getWeight()));
            case "prefix" -> tab.add(teraGroup.getPrefix());
            case "add" -> Bukkit.getPluginManager().getPermissions().forEach(p -> tab.add(p.getName()));
            case "remove" -> tab.addAll(teraGroup.getPermission());
            case "inheritance" -> tab.addAll(List.of("add", "remove"));
        }
    }

    private void handleUserFifthArg(List<String> tab, String[] args) throws ExecutionException, InterruptedException {
        UUID uuid = fetchUUID(args[1]);
        UserPermission userPermission = new UserPermission(uuid);

        if (args[2].equalsIgnoreCase("group")) {
            if (args[3].equalsIgnoreCase("add")) {
                GroupManager.getGroupsListString().stream()
                        .filter(group -> !userPermission.getGroups().contains(group))
                        .forEach(tab::add);
            } else if (args[3].equalsIgnoreCase("remove")) {
                GroupManager.getGroupsListString().stream()
                        .filter(userPermission.getGroups()::contains)
                        .forEach(tab::add);
            }
        }
    }

    private void handleGroupFifthArg(List<String> tab, String[] args) {
        String group = args[1].toLowerCase();
        if (!GroupManager.getGroupsListString().contains(group)) {
            tab.add(PermCommand.getMessage("no_group_tabcompleter"));
            return;
        }
        TeraGroup teraGroup = GroupManager.getGroup(group);

        if (args[2].equalsIgnoreCase("inheritance")) {
            if (args[3].equalsIgnoreCase("add")) {
                GroupManager.getGroupsListString().stream()
                        .filter(g -> !g.equalsIgnoreCase(group) && !teraGroup.getInheritance().contains(g))
                        .forEach(tab::add);
            } else if (args[3].equalsIgnoreCase("remove")) {
                GroupManager.getGroupsListString().stream()
                        .filter(g -> !g.equalsIgnoreCase(group) && teraGroup.getInheritance().contains(g))
                        .forEach(tab::add);
            }
        }
    }

    private UUID fetchUUID(String name) throws ExecutionException, InterruptedException {
        Player player = Bukkit.getPlayer(name);
        return player != null ? player.getUniqueId() : UUIDFetcher.getUUIDAsync(name).get();
    }

    private List<String> filter(List<String> source, String start) {
        List<String> result = new ArrayList<>();
        for (String s : source) {
            if (start == null || s.toLowerCase().startsWith(start.toLowerCase())) {
                result.add(s);
            }
        }
        return result;
    }
}
