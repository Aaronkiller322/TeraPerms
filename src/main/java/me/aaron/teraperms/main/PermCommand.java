package me.aaron.teraperms.main;

import me.aaron.teraperms.Component.HexColorTextGenerator;
import me.aaron.teraperms.config.PermissionConfig;
import me.aaron.teraperms.permission.UserPermission;
import me.aaron.teraperms.permission.group.GroupManager;
import net.kyori.adventure.text.ComponentBuilder;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.awt.*;

public class PermCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("perm")) return false;

        if (!(sender instanceof Player || sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(getMessage("no_player_or_console"));
            return true;
        }

        if (sender instanceof Player player) {
            UserPermission userPermission = new UserPermission(player.getUniqueId());
            if (!userPermission.getAllPermission().contains("*")) {
                sender.sendMessage(getMessage("no_permission"));
                return true;
            }
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            PermissionConfig.loadConfig();
            GroupManager.reloadGroup();
            PermsMain.getPlugin().loadAllPermissions();
            sender.sendMessage(getMessage("commands.reload.successful"));
            return true;
        }

        if (GroupCommand.execute(sender, args)) return true;
        if (UserCommand.execute(sender, args)) return true;

        sendHelpMessages(sender);
        return true;
    }

    public static String getMessage(String path) {
        String text = PermsMain.getPlugin().getConfig().getString(path, "&4Error");
        return HexColorTextGenerator.convertAllInfo(text);
    }

    private void sendHelpMessages(CommandSender sender) {
        String[] paths = {
                "commands.reload.help",
                "commands.user.info.help",
                "commands.user.add.help",
                "commands.user.remove.help",
                "commands.user.group.add.help",
                "commands.user.group.remove.help",
                "commands.group.list.help",
                "commands.group.create.help",
                "commands.group.delete.help",
                "commands.group.info.help",
                "commands.group.weight.help",
                "commands.group.prefix.help",
                "commands.group.add.help",
                "commands.group.remove.help",
                "commands.group.inheritance.add.help",
                "commands.group.inheritance.remove.help"
        };

        for (String path : paths) {
            sender.sendMessage(getMessage(path));
        }
    }
}
