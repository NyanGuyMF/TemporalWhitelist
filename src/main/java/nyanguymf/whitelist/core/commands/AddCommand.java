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

import static java.lang.System.currentTimeMillis;
import static nyanguymf.whitelist.core.MessagesManager.formatTime;
import static nyanguymf.whitelist.core.db.WhitelistedPlayer.isPlayerExists;
import static nyanguymf.whitelist.core.db.WhitelistedPlayer.playerByName;

import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import nyanguymf.whitelist.commons.commands.SubCommand;
import nyanguymf.whitelist.core.MessagesManager;
import nyanguymf.whitelist.core.db.WhitelistedPlayer;
import sh.okx.timeapi.api.TimeAPI;

/** @author NyanGuyMF - Vasiliy Bely */
final class AddCommand extends SubCommand {
    private MessagesManager messages;

    public AddCommand(final MessagesManager messages) {
        super("add", "twh.add", messages.usage("whitelist", "add"));
        this.messages = messages;
    }

    @Override public boolean execute(
        final CommandSender sender, final String alias, final String[] args
    ) {
        if (args.length == 0)
            return false;

        // I thought about stream with map(player -> name), then sort & search
        // or maybe just stream API usage, but it's too complicated method for this goal
        // TODO: find other way if this will be time-consuming
        boolean isPlayerFound = false;
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            if (player.getName().equals(args[0])) {
                isPlayerFound = true;
                break;
            }
        }

        if (!isPlayerFound) {
            sender.sendMessage(messages.info("player-not-found-warn", args[0]));
        }

        Date untilDate = null;

        if (args.length > 1) {
            try {
                TimeAPI until = new TimeAPI(args[1]);
                untilDate = new Date(currentTimeMillis() + until.getMilliseconds());
            } catch (IllegalArgumentException ex) {
                sender.sendMessage(messages.error("invalid-time-format", args[1]));
                return true;
            }
        }

        WhitelistedPlayer player;
        if (isPlayerExists(args[0])) {
            player = playerByName(args[0]);
            player.setWhitelisted(true);
            player.setUntil(untilDate);
            player.save();
        } else {
            player = new WhitelistedPlayer(args[0], untilDate);
            player.setWhitelisted(true);
            player.create();
        }

        if (untilDate != null) {
            sender.sendMessage(formatTime(messages.info(
                "whitelisted-until", player.getName()
            ), untilDate));
        } else {
            sender.sendMessage(messages.info("whitelisted", player.getName()));
        }

        return true;
    }
}
