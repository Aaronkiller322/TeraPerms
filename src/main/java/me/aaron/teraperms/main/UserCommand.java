package me.aaron.teraperms.main;

import me.aaron.teraperms.Component.HexColorTextGenerator;
import me.aaron.teraperms.permission.UserPermission;
import me.aaron.teraperms.permission.group.GroupManager;
import me.aaron.teraperms.util.UUIDFetcher;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;

public class UserCommand {

    public static boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2 || !args[0].equalsIgnoreCase("user")) return false;

        String user = args[1];
        UUIDFetcher.getUUIDAsync(user).thenAccept(uuid -> {
            if (uuid == null) {
                sender.sendMessage(PermCommand.getMessage("no_player"));
                return;
            }

            UserPermission userPermission = new UserPermission(uuid);

            switch (args.length) {
                case 2 -> sendPlayerInfo(sender, uuid, userPermission);
                case 3 -> {
                    if (args[2].equalsIgnoreCase("info")) {
                        sendPlayerInfo(sender, uuid, userPermission);
                    }
                }
                case 4 -> {
                    String permission = args[3];
                    switch (args[2].toLowerCase()) {
                        case "add" -> {
                            userPermission.addPermission(permission);
                            sender.sendMessage(PermCommand.getMessage("commands.user.add.successful"));
                        }
                        case "remove" -> {
                            userPermission.removePermission(permission);
                            sender.sendMessage(PermCommand.getMessage("commands.user.remove.successful"));
                        }
                    }
                }
                case 5 -> {
                    if (!args[2].equalsIgnoreCase("group")) return;
                    String group = args[4].toLowerCase();
                    if (!GroupManager.getGroupsListString().contains(group)) {
                        sender.sendMessage(PermCommand.getMessage("no_group"));
                        return;
                    }

                    switch (args[3].toLowerCase()) {
                        case "add" -> {
                            if (userPermission.getGroups().contains(group)) {
                                sender.sendMessage(PermCommand.getMessage("already_set"));
                                return;
                            }
                            UUIDFetcher.getNameAsync(uuid).thenAccept(username -> {
                                userPermission.addGroup(group);
                                sender.sendMessage(PermCommand.getMessage("commands.user.group.add.successful")
                                        .replace("%player%", username)
                                        .replace("%group%", group));
                            });
                        }
                        case "remove" -> {
                            if (!userPermission.getGroups().contains(group)) {
                                sender.sendMessage(PermCommand.getMessage("no_group"));
                                return;
                            }
                            UUIDFetcher.getNameAsync(uuid).thenAccept(username -> {
                                userPermission.removeGroup(group);
                                sender.sendMessage(PermCommand.getMessage("commands.user.group.remove.successful")
                                        .replace("%player%", username)
                                        .replace("%group%", group));
                            });
                        }
                    }
                }
            }
        });

        return true;
    }

    private static void sendPlayerInfo(CommandSender sender, UUID uuid, UserPermission userPermission) {
        UUIDFetcher.getNameAsync(uuid).thenAccept(username -> {
            List<String> list = PermsMain.getPlugin().getConfig().getStringList("commands.user.info.successful");
            for (String line : list) {
                line = line.replace("%player%", username)
                        .replace("%primarygroup%", userPermission.getPrimaryGroup())
                        .replace("%inheritance%", userPermission.getGroups().toString())
                        .replace("%permission%", userPermission.getPermission().toString());
                sender.sendMessage(HexColorTextGenerator.convertAllInfo(line));
            }
        });
    }
}
