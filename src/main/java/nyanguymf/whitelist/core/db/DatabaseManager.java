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

import static com.j256.ormlite.table.TableUtils.createTable;
import static org.bukkit.Bukkit.getConsoleSender;
import static org.bukkit.ChatColor.RED;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

/** @author NyanGuyMF - Vasiliy Bely */
public final class DatabaseManager {
    private String pluginFolderPath;
    private ConnectionSource conn;
    private DatabaseConfiguration config;

    public DatabaseManager(final File pluginFolder) {
        config = DatabaseConfiguration.getInstance(new File(pluginFolder, "database.yml"));
        pluginFolderPath = pluginFolder.getAbsolutePath();
    }

    public DatabaseManager connect() {
        if (config == null)
            return this;

        try {
            if (config.isEnabled()) {
                conn = new JdbcConnectionSource(
                    config.getConnectionUrl(), config.getUser(), config.getPassword()
                );
            } else {
                conn = new JdbcConnectionSource(
                    "jdbc:h2:"
                    + pluginFolderPath
                    + File.separatorChar
                    + "whitelist"
                );
            }
        } catch (SQLException ex) {
            getConsoleSender().sendMessage(
                RED + "Unable to connect to database server: "
                + ex.getLocalizedMessage()
            );
        }
        return this;
    }

    public DatabaseManager initDaos() {
        try {
            WhitelistedPlayer.initDao(DaoManager.createDao(conn, WhitelistedPlayer.class));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return this;
    }

    public DatabaseManager createTables() {
        try {
            if (!WhitelistedPlayer.getDao().isTableExists()) {
                createTable(WhitelistedPlayer.getDao());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (NullPointerException ignore) {}

        return this;
    }

    public void close() {
        if (conn != null) {
            try {
                conn.close();
            } catch (IOException ex) {
                getConsoleSender().sendMessage(
                    RED + "Unable to close database connection: "
                    + ex.getLocalizedMessage()
                );
            }
        }
    }
}
