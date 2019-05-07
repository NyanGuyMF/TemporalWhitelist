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
package nyanguymf.whitelist.core;

import static nyanguymf.whitelist.core.db.DatabaseManagerFactory.loadDatabaseManager;
import static nyanguymf.whitelist.core.db.WhitelistedPlayer.allPlayers;
import static org.bukkit.Bukkit.getConsoleSender;
import static org.bukkit.Bukkit.getScheduler;
import static org.bukkit.ChatColor.GREEN;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import nyanguymf.whitelist.commons.db.DatabaseManager;
import nyanguymf.whitelist.commons.db.DatabaseManager.ConnectionStatus;
import nyanguymf.whitelist.core.commands.WhitelistCommand;
import nyanguymf.whitelist.core.db.WhitelistedPlayer;
import nyanguymf.whitelist.core.events.PlayerJoinHandler;

/** @author NyanGuyMF - Vasiliy Bely */
public final class TemporalWhitelistPlugin extends JavaPlugin implements WhitelistManager {
    private static TemporalWhitelistPlugin instance;
    private boolean isEnabled = false;
    private static DatabaseManager databaseManager;
    private MessagesManager messagesManager;

    public static boolean reconnect() {
        TemporalWhitelistPlugin.databaseManager = loadDatabaseManager(TemporalWhitelistPlugin.instance);

        if (TemporalWhitelistPlugin.databaseManager.isConnected()) {
            System.out.println("Reestablished connection with database");
        }

        return TemporalWhitelistPlugin.databaseManager.isConnected();
    }

    @Override public void onLoad() {
        TemporalWhitelistPlugin.instance = this;

        if (!super.getDataFolder().exists()) {
            super.getDataFolder().mkdir();
        }
        if (!new File(super.getDataFolder(), "config.yml").exists()) {
            super.saveDefaultConfig();
        }

        isEnabled = super.getConfig().getBoolean("is-enabled", false);

        TemporalWhitelistPlugin.databaseManager = loadDatabaseManager(this);

        if (TemporalWhitelistPlugin.databaseManager.getStatus() == ConnectionStatus.CONNECTED) {
            Bukkit.getConsoleSender().sendMessage(GREEN + "Connected to database.");
        }

        try {
            messagesManager = MessagesManager.getInstance(
                super.getDataFolder(),
                super.getConfig().getString("lang", "en")
            );
        } catch (IOException ex) {
            System.err.printf(
                "Unable to load messages file: %s\n",
                ex.getLocalizedMessage()
            );
        }
    }

    @Override public void onEnable() {
        if ((messagesManager == null) || (TemporalWhitelistPlugin.databaseManager == null)) {
            getConsoleSender().sendMessage(
                "\u00a73TemporalWhitelist \u00a78» \u00a7cPlugin wasn't enabled."
            );
            super.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        new WhitelistCommand(messagesManager, this).register(this);
        new PlayerJoinHandler(messagesManager).register(this);

        getScheduler().runTaskLaterAsynchronously(this, () -> {
            Date currentDate = new Date();

            for (WhitelistedPlayer player : allPlayers()) {
                if (player.isWhitelisted() && (player.getUntil() != null)) {
                    if (player.getUntil().after(currentDate)) {
                        player.setUntil(null);
                        player.setWhitelisted(false);
                        player.save();
                    }
                }
            }
        }, 20 * 1_800); // run every 30 minutes

        getConsoleSender().sendMessage(
            "\u00a73TemporalWhitelist \u00a78» \u00a7aPlugin enabled."
        );

        String isEnabled = this.isEnabled ? "\u00a7atrue" : "\u00a7cfalse";
        getConsoleSender().sendMessage(
            "\u00a73TemporalWhitelist \u00a78» \u00a7eWhitelist mode: "
            + isEnabled
        );
    }

    @Override public void onDisable() {
        try {
            TemporalWhitelistPlugin.databaseManager.close();
        } catch (IOException ignore) {}
        updateWhitelistConfig();
    }

    @Override public void enable() {
        if (!isEnabled) {
            isEnabled = true;
        }
        updateWhitelistConfig();
    }

    @Override public void disable() {
        if (isEnabled) {
            isEnabled = false;
        }
        updateWhitelistConfig();
    }

    @Override public boolean isWhitelistEnabled() {
        return isEnabled;
    }

    private void updateWhitelistConfig() {
        super.getConfig().set("is-enabled", isEnabled);
        try {
            super.getConfig().save(new File(super.getDataFolder(), "config.yml"));
        } catch (IOException ignore) {}
    }
}
