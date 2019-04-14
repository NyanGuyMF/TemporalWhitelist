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
package nyanguymf.whitelist.core.commands;

import static nyanguymf.whitelist.core.MessagesManager.formatTime;
import static nyanguymf.whitelist.core.db.WhitelistedPlayer.isPlayerExists;
import static nyanguymf.whitelist.core.db.WhitelistedPlayer.playerByName;

import org.bukkit.command.CommandSender;

import nyanguymf.whitelist.commons.commands.SubCommand;
import nyanguymf.whitelist.core.MessagesManager;
import nyanguymf.whitelist.core.db.WhitelistedPlayer;

/** @author NyanGuyMF - Vasiliy Bely */
final class InfoCommand extends SubCommand {
    private MessagesManager messages;

    public InfoCommand(final MessagesManager messages) {
        super("info", "twh.info", messages.usage("whitelist", "info"));

        this.messages = messages;
    }

    @Override public boolean execute(
        final CommandSender sender, final String alias, final String[] args
    ) {
        if (!super.hasPermission(sender))
            return false;

        if (args.length == 0)
            return false;

        if (!isPlayerExists(args[0])) {
            sender.sendMessage(messages.error("player-doesnt-exists", args[0]));
            return true;
        }

        WhitelistedPlayer player = playerByName(args[0]);

        String isWhitelisted = player.isWhitelisted()
                ? messages.info("true")
                : messages.info("false");

        String until = player.getUntil() != null
                ? formatTime(messages.info("until"), player.getUntil())
                : messages.info("null");

        messages.multiline("player-info", false).stream()
            .map(message -> {
                return message
                    .replace("&", "\u00a7")
                    .replace("{player-name}", player.getName())
                    .replace("{is-whitelisted}", isWhitelisted)
                    .replace("until", until);
            })
            .forEach(message -> sender.sendMessage(message));

        return true;
    }
}
