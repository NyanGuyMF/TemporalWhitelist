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
package nyanguymf.whitelist.core.db;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.plugin.Plugin;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import nyanguymf.whitelist.commons.db.DatabaseConfiguration;
import nyanguymf.whitelist.commons.db.DatabaseManager;
import nyanguymf.whitelist.core.yaml.YamlDatabaseConfiguration;

/** @author NyanGuyMF - Vasiliy Bely */
public final class DatabaseManagerFactory {
    public static DatabaseManager loadDatabaseManager(final Plugin plugin) {
        DatabaseConfiguration databaseConfig = loadConfig(plugin);

        if (databaseConfig == null)
            return null;

        DatabaseManager databaseManager = new DatabaseManager(plugin, databaseConfig);

        if (!databaseManager.connect()) {
            switch (databaseManager.getStatus()) {
            default:
                System.err.printf("Connection error status: %s\n", databaseManager.getStatus());
                break;
            }
            return databaseManager;
        }

        try {
            Dao<WhitelistedPlayer, String> playersDao = DaoManager.createDao(
                databaseManager.getConnection(), WhitelistedPlayer.class
            );

            WhitelistedPlayer.initDao(playersDao);
        } catch (SQLException ex) {
            System.err.printf("Unable to create dao: %s\n", ex.getLocalizedMessage());
        }

        return databaseManager;
    }

    private static DatabaseConfiguration loadConfig(final Plugin plugin) {
        File configFile = new File(plugin.getDataFolder(), "database.yml");
        boolean isFileExists = configFile.exists();

        if (!isFileExists) {
            try {
                configFile.createNewFile();
            } catch (IOException ex) {
                System.err.println(ex.getLocalizedMessage());
                return null;
            }
        }

        YamlDatabaseConfiguration databaseConfig
                = new YamlDatabaseConfiguration(configFile.toPath());

        if (!isFileExists) {
            databaseConfig.save();
        } else {
            databaseConfig.loadAndSave();
        }

        if (databaseConfig.getDriverName().equalsIgnoreCase("h2")) {
            databaseConfig.setHost(plugin.getDataFolder().getAbsolutePath());
            databaseConfig.save();
        }

        return databaseConfig;
    }
}
