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

import static org.bukkit.Bukkit.getConsoleSender;
import static org.bukkit.ChatColor.RED;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import de.exlll.configlib.annotation.Comment;
import de.exlll.configlib.configs.yaml.BukkitYamlConfiguration;

/** @author NyanGuyMF - Vasiliy Bely */
final class DatabaseConfiguration extends BukkitYamlConfiguration {
    private static DatabaseConfiguration ignoreInstance;

    @Comment({
        "Set true if you want ot connect to MySQL database.",
        "Otherwise it will use local H2 database."
    })
    private boolean enabled = false;

    @Comment({
        "Replace placeholders in 'jdbc:mysql://{host}:{port}/{database}'",
        " with your host (localhost usually), port (3306 usually) and database.",
        "Example: jbdc:mysql://localhost:3306/whitelist",
        "Or leave this field unaffected if you disabled database."
    })
    private String connectionUrl = "jdbc:mysql://{host}:{port}/{database}";

    @Comment({
        "",
        "Highly recommend to create new user and give strong password, don't user root!",
        "Your database user."
    })
    private String user = "whlist";

    @Comment("Password of your user in database.")
    private String password = "XQZDH8+Uj#ScLp$!nx3z";

    private DatabaseConfiguration(final Path path) {
        super(
            path,
            BukkitYamlProperties.builder()
                .setFormatter(fn -> {
                    StringBuilder builder = new StringBuilder(fn.length());
                    for (char c : fn.toCharArray()) {
                        if (Character.isLowerCase(c)) {
                            builder.append(c);
                        } else if (Character.isUpperCase(c)) {
                            c = Character.toLowerCase(c);
                            builder.append('-').append(c);
                        }
                    }
                    return builder.toString();
                })
                .addFilter(field -> !field.getName().startsWith("ignore"))
                .build()
        );
    }

    protected static DatabaseConfiguration getInstance(final File configFile) {
        if (DatabaseConfiguration.ignoreInstance == null) {
            boolean isFileExists = configFile.exists();

            if (!isFileExists) {
                try {
                    configFile.createNewFile();
                } catch (IOException ex) {
                    getConsoleSender().sendMessage(
                        RED + "Unable to create database config file: "
                        + ex.getLocalizedMessage()
                    );
                    // cannot create config without file
                    DatabaseConfiguration.ignoreInstance = null;
                    return DatabaseConfiguration.ignoreInstance;
                }
            }

            if (!isFileExists) {
                DatabaseConfiguration.ignoreInstance = new DatabaseConfiguration(configFile.toPath());
                DatabaseConfiguration.ignoreInstance.save();
            } else {
                DatabaseConfiguration.ignoreInstance = new DatabaseConfiguration(configFile.toPath());
                DatabaseConfiguration.ignoreInstance.loadAndSave();
            }

            return DatabaseConfiguration.ignoreInstance;
        }

        return DatabaseConfiguration.ignoreInstance;
    }

    /** @return the enabled */
    public boolean isEnabled() {
        return enabled;
    }

    /** Sets enabled */
    protected void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    /** @return the connectionUrl */
    public String getConnectionUrl() {
        return connectionUrl;
    }

    /** Sets connectionUrl */
    public void setConnectionUrl(final String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    /** @return the user */
    public String getUser() {
        return user;
    }

    /** Sets user */
    public void setUser(final String user) {
        this.user = user;
    }

    /** @return the password */
    public String getPassword() {
        return password;
    }

    /** Sets password */
    public void setPassword(final String password) {
        this.password = password;
    }
}
