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

import static org.bukkit.Bukkit.getConsoleSender;

import java.io.File;
import java.io.IOException;

import org.bukkit.plugin.java.JavaPlugin;

import nyanguymf.whitelist.core.db.DatabaseManager;

/** @author NyanGuyMF - Vasiliy Bely */
public final class TemporalWhitelistPlugin extends JavaPlugin {
    private DatabaseManager databaseManager;
    private MessagesManager messagesManager;

    @Override public void onLoad() {
        if (!super.getDataFolder().exists()) {
            super.getDataFolder().mkdir();
        }
        if (!new File(super.getDataFolder(), "config.yml").exists()) {
            super.saveDefaultConfig();
        }

        databaseManager = new DatabaseManager(super.getDataFolder()).connect();
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
        getConsoleSender().sendMessage(
            "\u00a73TemporalWhitelist \u00a78» \u00a7aPlugin enabled."
        );
    }

    @Override public void onDisable() {
        databaseManager.close();
    }
}
