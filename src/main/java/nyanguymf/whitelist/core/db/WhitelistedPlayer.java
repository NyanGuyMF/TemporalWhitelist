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

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/** @author NyanGuyMF - Vasiliy Bely */
@DatabaseTable(tableName="players")
public final class WhitelistedPlayer {
    private static Dao<WhitelistedPlayer, String> dao;

    @DatabaseField(id=true)
    private String name;

    @DatabaseField(columnName="is_whitelisted", canBeNull=false)
    private boolean isWhitelisted = false;

    @DatabaseField(dataType=DataType.DATE_STRING)
    private Date until;

    public WhitelistedPlayer() {}

    public WhitelistedPlayer(final String name) {
        this(name, null);
    }

    public WhitelistedPlayer(final String name, final Date until) {
        this.name = name;
        this.until = until;
    }

    protected static void initDao(final Dao<WhitelistedPlayer, String> dao) {
        if (WhitelistedPlayer.dao == null) {
            try {
                if (!dao.isTableExists()) {
                    createTable(dao);
                }
            } catch (SQLException ex) {
                System.err.printf("Unable to create table: %s\n",ex.getLocalizedMessage());
            }
            WhitelistedPlayer.dao = dao;
        }
    }

    public static List<WhitelistedPlayer> allPlayers() {
        try {
            return WhitelistedPlayer.dao.queryForAll();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Gets player by name.
     * <p>
     * If player not found it will return <tt>null</tt>.
     *
     * @param   playerName  Player name for query.
     * @return {@link WhitelistedPlayer} instance for given name or
     * <tt>null</tt> value if not found.
     */
    public static WhitelistedPlayer playerByName(final String playerName) {
        try {
            return WhitelistedPlayer.dao.queryForId(playerName);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static boolean isPlayerExists(final String player) {
        try {
            return WhitelistedPlayer.dao.idExists(player);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean create() {
        try {
            WhitelistedPlayer.dao.create(this);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean save() {
        try {
            WhitelistedPlayer.dao.update(this);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean reload() {
        try {
            WhitelistedPlayer.dao.refresh(this);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean delete() {
        try {
            WhitelistedPlayer.dao.delete(this);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    /** @return the name */
    public String getName() {
        return name;
    }

    /** Sets name */
    public void setName(final String name) {
        this.name = name;
    }

    /** @return the isWhitelisted */
    public boolean isWhitelisted() {
        return isWhitelisted;
    }

    /** Sets isWhitelisted */
    public void setWhitelisted(final boolean isWhitelisted) {
        this.isWhitelisted = isWhitelisted;
    }

    /** @return the until */
    public Date getUntil() {
        return until;
    }

    /** Sets until */
    public void setUntil(final Date until) {
        this.until = until;
    }

    /** @return the dao */
    protected static Dao<WhitelistedPlayer, String> getDao() {
        return WhitelistedPlayer.dao;
    }
}
