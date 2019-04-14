/**
 * This file is the part of TemporalWhitelist plug-in.
 *
 * Copyright (c) 2019 Vasiliy (NyanGuyMF)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package nyanguymf.whitelist.commons.commands;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

/** @author NyanGuyMF - Vasiliy Bely */
public abstract class CommandManager implements CommandExecutor, TabCompleter {
    private String name;
    private String usage;
    private Map<String, SubCommand> subCommands;

    public CommandManager(final String name, final String usage) {
        this.name = name;
        this.usage = usage;
        subCommands = new HashMap<>();
    }

    /**
     * Executes given command alias.
     *
     * @param   sender  The person who executed command.
     * @param   cmd     The command which was executed
     * @param   alias   Used command alias.
     * @param   args    Arguments of command.
     * @return Always <tt>true</tt>.
     */
    @Override public final boolean onCommand(
        final CommandSender sender, final Command cmd,
        final String alias, final String[] args
    ) {
        if (args.length == 0) {
            sender.sendMessage(usage);
            return true;
        }

        boolean isExecuted = false;

        final String subCommandName = args[0].toLowerCase();
        final String[] subArgs = Arrays.copyOfRange(args, 1, args.length);

        if (subCommands.containsKey(subCommandName)) {
            SubCommand subCommand = subCommands.get(subCommandName);

            if (!subCommand.execute(sender, subCommandName, subArgs)) {
                sender.sendMessage(subCommand.getUsage());
            }

            isExecuted = true;
        }

        if (isExecuted)
            return isExecuted;

        for (SubCommand subCommand : subCommands.values()) {
            for (String subCommandAlias : subCommand.getAliases()) {
                if (!subCommandAlias.equals(subCommandName)) {
                    continue;
                }

                if (!subCommand.execute(sender, subCommandName, subArgs)) {
                    sender.sendMessage(subCommand.getUsage());
                }

                isExecuted = true;
                break;
            }
        }

        if (!isExecuted) {
            sender.sendMessage(usage);
        }

        return true;
    }

    @Override public List<String> onTabComplete(
        final CommandSender sender, final Command command,
        final String alias, final String[] args
    ) {
        if (args.length == 1)
            return subCommands.keySet().parallelStream()
                .filter(subCommandName -> subCommandName.startsWith(args[0]))
                .collect(toList());
        else
            return null;
    }

    /**
     * Adds given sub command to this manager.
     *
     * @param   subCommand  Sub command to add.
     * @return previous sub command with this same name.
     */
    public final SubCommand addSub(final SubCommand subCommand) {
        return subCommands.put(subCommand.getName(), subCommand);
    }

    /** Registers command for plugin. */
    public final void register(final JavaPlugin plugin) {
        plugin.getCommand(name).setExecutor(this);
        plugin.getCommand(name).setTabCompleter(this);
    }
}
