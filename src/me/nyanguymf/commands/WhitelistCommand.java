/**
 * 
 */
package me.nyanguymf.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.nyanguymf.TemporalWhitelist;
import me.nyanguymf.Whitelist;

/**
 * @author nyanguymf
 *
 */
public class WhitelistCommand implements CommandExecutor {
    private Whitelist wh = new Whitelist();
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("whitelist.use")) {
            // TODO add config for messaging
            sender.sendMessage(TemporalWhitelist.getMessage("no-permission"));
            return true;
        }

        if (args.length == 0) {
            wh.reload();
            sender.sendMessage(TemporalWhitelist.getMessage("reloaded"));
            return true;
        }

        if (args[0].equalsIgnoreCase("add")) {
            if (args.length < 2) {
                sender.sendMessage(TemporalWhitelist.getMessage("add-usage"));
                return true;
            }

            if (args.length == 2)
                wh.add(sender, args[1]);
            else if (args.length >= 3)
                wh.add(sender, args[1], args[2]);
            return true;
        }

        if (args[0].equalsIgnoreCase("remove")) {
            if (args.length < 2) {
                // TODO add config for messaging
                sender.sendMessage(TemporalWhitelist.getMessage("remove-usage"));
                return true;
            }

            wh.remove(sender, args[1]);
            return true;
        }

        if (args[0].equalsIgnoreCase("on")) {
            wh.on();
            sender.sendMessage(TemporalWhitelist.getMessage("whitelist-on"));
            return true;
        }

        if (args[0].equalsIgnoreCase("off")) {
            wh.off();
            sender.sendMessage(TemporalWhitelist.getMessage("whitelist-off"));
            return true;
        }

        if (args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(TemporalWhitelist.getMessage("whitelist-usage"));
            return true;
        }

        sender.sendMessage(TemporalWhitelist.getMessage("whitelist-usage"));
        return true;
    }

}
