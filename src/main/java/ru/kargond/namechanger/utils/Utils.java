package ru.kargond.namechanger.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class Utils {

    public final static String PREFIX = "&b[NickChanger] &f";

    public static void sendPrefixedMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', PREFIX + message));
    }

    public static String trimUUID(UUID uuid) {
        return uuid.toString().replace("-", "");
    }
}
