package me.aaron.teraperms.main;

import me.aaron.teraperms.Component.HexColorTextGenerator;
import me.aaron.teraperms.permission.group.GroupManager;
import me.aaron.teraperms.permission.group.TeraGroup;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class GroupCommand {

    public static boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2 || !args[0].equalsIgnoreCase("group")) return false;

        String group = args[1].toLowerCase();

        switch (args.length) {
            case 2 -> {
                if (args[1].equalsIgnoreCase("list")) {
                    String list = PermCommand.getMessage("commands.group.list.successful")
                            .replace("%group%", GroupManager.getGroupsListString().toString());
                    sender.sendMessage(list);
                } else {
                    sendInfo(sender, group);
                }
                return true;
            }
            case 3 -> {
                if (args[2].equalsIgnoreCase("create")) {
                    if (GroupManager.getGroupsListString().contains(group)) {
                        sender.sendMessage(PermCommand.getMessage("exist_group"));
                        return true;
                    }
                    TeraGroup teraGroup = new TeraGroup(group, "&7" + group, 1, false, new ArrayList<>(), new ArrayList<>());
                    GroupManager.saveTeraGroup(teraGroup);
                    sender.sendMessage(PermCommand.getMessage("commands.group.create.successful").replace("%group%", group));
                    return true;
                }
                if (!GroupManager.getGroupsListString().contains(group)) {
                    sender.sendMessage(PermCommand.getMessage("no_group"));
                    return true;
                }
                if (args[2].equalsIgnoreCase("delete")) {
                    GroupManager.deleteTeraGroup(group);
                    sender.sendMessage(PermCommand.getMessage("commands.group.delete.successful").replace("%group%", group));
                    return true;
                }
                if (args[2].equalsIgnoreCase("info")) {
                    sendInfo(sender, group);
                    return true;
                }
            }
            case 4 -> {
                if (!GroupManager.getGroupsListString().contains(group)) {
                    sender.sendMessage(PermCommand.getMessage("no_group"));
                    return true;
                }
                TeraGroup teraGroup = GroupManager.getGroup(group);
                switch (args[2].toLowerCase()) {
                    case "weight" -> {
                        try {
                            int weight = Integer.parseInt(args[3]);
                            teraGroup.setWeight(weight);
                            GroupManager.saveTeraGroup(teraGroup);
                            GroupManager.reloadGroup();
                            sender.sendMessage(PermCommand.getMessage("commands.group.weight.successful").replace("%group%", group));
                            return true;
                        } catch (NumberFormatException ignored) {}
                        return false;
                    }
                    case "prefix" -> {
                        teraGroup.setPrefix(args[3]);
                        GroupManager.saveTeraGroup(teraGroup);
                        GroupManager.reloadGroup();
                        sender.sendMessage(PermCommand.getMessage("commands.group.prefix.successful").replace("%group%", group));
                        return true;
                    }
                    case "add" -> {
                        teraGroup.addPermission(args[3]);
                        updatePermissions();
                        sender.sendMessage(PermCommand.getMessage("commands.group.add.successful").replace("%group%", group));
                        return true;
                    }
                    case "remove" -> {
                        teraGroup.removePermission(args[3]);
                        updatePermissions();
                        sender.sendMessage(PermCommand.getMessage("commands.group.remove.successful").replace("%group%", group));
                        return true;
                    }
                }
            }
            case 5 -> {
                if (!GroupManager.getGroupsListString().contains(group)) {
                    sender.sendMessage(PermCommand.getMessage("no_group"));
                    return true;
                }
                if (args[2].equalsIgnoreCase("inheritance")) {
                    TeraGroup teraGroup = GroupManager.getGroup(group);
                    switch (args[3].toLowerCase()) {
                        case "add" -> teraGroup.addInheritance(args[4]);
                        case "remove" -> teraGroup.removeInheritance(args[4]);
                        default -> {
                            return false;
                        }
                    }
                    updatePermissions();
                    sender.sendMessage(PermCommand.getMessage("commands.group.inheritance." + args[3].toLowerCase() + ".successful")
                            .replace("%group%", group));
                    return true;
                }
            }
        }
        return false;
    }

    private static void sendInfo(CommandSender sender, String group) {
        List<String> infoList = PermsMain.getPlugin().getConfig().getStringList("commands.group.info.successful");
        TeraGroup tgroup = GroupManager.getGroup(group);
        for (String line : infoList) {
            line = line.replace("%group%", group)
                    .replace("%prefix%", tgroup.getPrefix())
                    .replace("%default%", String.valueOf(tgroup.isStandard()))
                    .replace("%weight%", String.valueOf(tgroup.getWeight()))
                    .replace("%inheritance%", tgroup.getInheritance().toString())
                    .replace("%permission%", tgroup.getPermission().toString());
            sender.sendMessage(HexColorTextGenerator.convertAllInfo(line));
        }
    }

    private static void updatePermissions() {
        GroupManager.reloadGroup();
        PermsMain.getPlugin().loadAllPermissions();
    }
}
