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
package nyanguymf.whitelist.core.events;

import static nyanguymf.whitelist.core.db.WhitelistedPlayer.isPlayerExists;
import static nyanguymf.whitelist.core.db.WhitelistedPlayer.playerByName;

import java.util.Date;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.plugin.java.JavaPlugin;

import nyanguymf.whitelist.core.MessagesManager;
import nyanguymf.whitelist.core.db.WhitelistedPlayer;

/** @author NyanGuyMF - Vasiliy Bely */
public final class PlayerJoinHandler implements Listener {
    private MessagesManager messages;

    public PlayerJoinHandler(final MessagesManager messages) {
        this.messages = messages;
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onJoin(final PlayerLoginEvent event) {
        if (event.getResult() != Result.ALLOWED)
            return;

        if (!isPlayerExists(event.getPlayer().getName())) {
            disallow(event);
            new WhitelistedPlayer(event.getPlayer().getName()).create();
            return;
        }

        WhitelistedPlayer player = playerByName(event.getPlayer().getName());

        if (!player.isWhitelisted()) {
            disallow(event);
            return;
        }

        if (player.getUntil() != null) {
            if (player.getUntil().before(new Date())) {
                disallow(event);
                player.setWhitelisted(false);
                player.setUntil(null);
                player.save();
            }
        }
    }

    private void disallow(final PlayerLoginEvent event) {
        event.disallow(
            Result.KICK_WHITELIST,
            messages.info("not-whitelisted").replace("\\n", "\n")
        );
    }

    public void register(final JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
