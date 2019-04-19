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
package nyanguymf.whitelist.core.yaml;

import java.nio.file.Path;

import de.exlll.configlib.configs.yaml.BukkitYamlConfiguration;
import nyanguymf.whitelist.commons.db.DatabaseConfiguration;

/** @author NyanGuyMF - Vasiliy Bely */
public final class YamlDatabaseConfiguration
    extends BukkitYamlConfiguration
    implements DatabaseConfiguration
{
    private String driverName = "h2";
    private String username = "";
    private String password = "strongPassword228";
    private String databaseName = "whitelist";
    private String host = "localhost";
    private int port = 3306;

    public YamlDatabaseConfiguration(final Path path) {
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
                .build()
        );
    }

    @Override public String getDriverName() {
        return driverName;
    }

    public void setDriverName(final String driverName) {
        this.driverName = driverName;
    }

    @Override public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    @Override public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(final String databaseName) {
        this.databaseName = databaseName;
    }

    @Override public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    @Override public int getPort() {
        return port;
    }

    public void setPort(final int port) {
        this.port = port;
    }

    @Override public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }
}
