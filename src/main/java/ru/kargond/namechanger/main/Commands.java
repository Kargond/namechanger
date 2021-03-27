package ru.kargond.namechanger.main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.kargond.namechanger.utils.Utils;

public class Commands implements CommandExecutor {

    private NameChanger nameChanger;

    public Commands(NameChanger nameChanger) {
        this.nameChanger = nameChanger;
        nameChanger.getMain().getCommand("name").setExecutor(this);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("name")) {
            if (!(sender instanceof Player)) {
                Utils.sendPrefixedMessage(sender, "&cOnly players can use this command");
                return true;
            }

            if(!sender.hasPermission("name.change")) {
                Utils.sendPrefixedMessage(sender, "You do not have permissions to use this command");
                return true;
            }

            if(args.length == 0) {
                Utils.sendPrefixedMessage(sender, "&cUsage: /name <nickname> OR /name off");
                return true;
            }

            Player p = (Player) sender;

            if (args[0].equalsIgnoreCase("off")) {
                Utils.sendPrefixedMessage(sender, "You have changed your nickname back to default");
                nameChanger.getMaskManager().getMaskedPlayer(p).resetDisplayName();
                return true;
            }

            if (args[0].length() >= 16 || !args[0].matches("[a-zA-ZА-Я0-9а-я]+")) {
                Utils.sendPrefixedMessage(sender, "You cannot use this name");
                return true;
            }

            Utils.sendPrefixedMessage(sender, "Your new nickname is " + args[0]);
            nameChanger.getMaskManager().getMaskedPlayer(p).setDisplayName(args[0]);
            return true;
        }
        return true;
    }

}
