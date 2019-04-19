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
package nyanguymf.whitelist.commons.db;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

import org.apache.commons.codec.digest.DigestUtils;
import org.bukkit.plugin.Plugin;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

/** @author NyanGuyMF - Vasiliy Bely */
public final class DatabaseManager implements Closeable {
    private ReflectionClassLoader classLoader;
    private File driversFolder;
    private ConnectionSource conn;
    private DatabaseConfiguration config;
    private ConnectionStatus status;
    public enum ConnectionStatus {
        NOT_CONNECTED_YET, CONNECTED, DRIVER_NOT_FOUND,
        INVALID_HASH, DOWNLOAD_ERROR, CLOSED,
        CONNECTION_ERROR;
    }

    public DatabaseManager(final Plugin plugin, final DatabaseConfiguration config) {
        File driversFolder = new File(plugin.getDataFolder(), "libs");

        if (!driversFolder.exists()) {
            driversFolder.mkdir();
        } else if (driversFolder.isFile()) {
            System.err.println("Drivers file isn't directory.");
        }

        try {
            classLoader = new ReflectionClassLoader(plugin);
        } catch (IllegalStateException ex) {
            System.err.printf(
                "Unable to create ReflectionClassLoader instance: %s\n",
                ex.getLocalizedMessage()
            );
        }

        this.config = config;
        this.driversFolder = driversFolder;
        status = ConnectionStatus.NOT_CONNECTED_YET;
    }

    public boolean connect() {
        DatabaseDriver driver = findDriver(config.getDriverName());

        if (driver == null)
            return isConnected();

        try {
            Class.forName(driver.getClassName());
        } catch (ClassNotFoundException ex) {
            System.out.printf(
                "Driver not found: %s. Adding to classpath...\n", ex.getLocalizedMessage()
            );
            File driverJar = new File(driversFolder, driver.getFileName());

            if (!driverJar.exists()) {
                driverJar = downloadDriver(driver, driverJar);
            }

            if (driverJar == null) {
                status = ConnectionStatus.DOWNLOAD_ERROR;
                return isConnected();
            }

            if (!isDriverHashValid(driver, driverJar)) {
                driverJar.delete();
                status = ConnectionStatus.INVALID_HASH;
                return isConnected();
            }

            classLoader.loadJar(driverJar.toPath());
        }

        try {
            conn = new JdbcConnectionSource(
                driver.getConnectionUrl(config),
                config.getUsername(),
                config.getPassword()
            );
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.err.printf("Unable to connect to database: %s\n", ex.getMessage());
            status = ConnectionStatus.CONNECTION_ERROR;
            return isConnected();
        }

        status = ConnectionStatus.CONNECTED;

        return isConnected();
    }

    private File downloadDriver(final DatabaseDriver driver, final File destination) {
        URLConnection conn;

        try {
            conn = new URL(driver.getDownloadUrl()).openConnection();
        } catch (IOException ex) {
            System.err.println(ex.getLocalizedMessage());
            return null;
        }

        try (InputStream in = conn.getInputStream()) {
            Files.copy(in, destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            System.err.println(ex.getLocalizedMessage());
            return null;
        }

        return destination;
    }

    /**
     * Compares persistent driver MD5 hash checksum and
     * driver's file checksum.
     *
     * @param   driver      Persistent driver instance.
     * @param   driverFile  Existing driver file.
     * @return <tt>true</tt> if checksums are equal.
     */
    private boolean isDriverHashValid(final DatabaseDriver driver, final File driverFile) {
        try (InputStream in = new FileInputStream(driverFile)) {
            return driver.getMd5Hash().equals(DigestUtils.md5Hex(in));
        } catch (IOException ex) {
            System.err.println(ex.getLocalizedMessage());
            return false;
        }
    }

    private DatabaseDriver findDriver(final String driverName) {
        for (DatabaseDriver driver : DatabaseDriver.values())
            if (driver.toString().equalsIgnoreCase(driverName))
                return driver;

        return null;
    }

    public ConnectionSource getConnection() {
        return conn;
    }

    public ConnectionStatus getStatus() {
        return status;
    }

    public boolean isConnected() {
        return status == ConnectionStatus.CONNECTED;
    }

    @Override public void close() throws IOException {
        if (isConnected()) {
            conn.close();
            status = ConnectionStatus.CLOSED;
        }
    }
}
