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

/** @author NyanGuyMF - Vasiliy Bely */
enum DatabaseDriver {
    MySQL(
        "http://central.maven.org/maven2/mysql/mysql-connector-java/8.0.15/mysql-connector-java-8.0.15.jar"
        , "27e3ced4adad4feec926b56dfeb94031"
        , "com.mysql.cj.jdbc.Driver"
        , "jdbc:mysql://{host}:{port}/{database}"
    ),

    H2(
        "http://central.maven.org/maven2/com/h2database/h2/1.4.199/h2-1.4.199.jar"
        , "f805f57d838de4b42ce01c7f85e46e1c"
        , "org.h2.Driver"
        , "jdbc:h2:{host}/{database}"
    );

    private String downloadUrl;

    private String md5Hash;

    private String className;

    private String connectionUrlFormat;

    private DatabaseDriver(
        final String downloadUrl, final String md5Hash,
        final String className, final String connectionUrlFormat
    ) {
        this.downloadUrl = downloadUrl;
        this.md5Hash = md5Hash;
        this.className = className;
        this.connectionUrlFormat = connectionUrlFormat;
    }

    public String getFileName() {
        return String.format("%s-driver.jar", super.toString().toLowerCase());
    }

    /** @return the downloadUrl */
    public String getDownloadUrl() {
        return downloadUrl;
    }

    /** @return the md5Hash */
    public String getMd5Hash() {
        return md5Hash;
    }

    /** @return the className */
    public String getClassName() {
        return className;
    }

    public String getConnectionUrl(final DatabaseConfiguration config) {
        return connectionUrlFormat.replace("{host}", config.getHost())
                .replace("{port}", String.valueOf(config.getPort()))
                .replace("{database}", config.getDatabaseName());
    }
}
