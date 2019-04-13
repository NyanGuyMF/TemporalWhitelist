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
package nyanguymf.whitelist.commons.commands;

import java.util.Objects;

import org.bukkit.command.CommandSender;

/** @author NyanGuyMF - Vasiliy Bely */
public abstract class SubCommand {
    private String name;

    private String permission;

    private String[] aliases;

    private String usage;

    public SubCommand(final String name, final String permission, final String usage) {
        this(name, permission, usage, new String[0]);
    }

    public SubCommand(
        final String name, final String permission,
        final String usage, final String...aliases
    ) {
        this.name = name;
        this.permission = permission;
        setUsage(usage);
        this.aliases = aliases;
    }

    /**
     * Executes sub command by given sender.
     * <p>
     * If returned value is <tt>false</tt> the {@link CommandManager}
     * will send {@link #usage} message to {@link CommandSender}.
     *
     * @param   sender  The person who executed command.
     * @param   alias   Used command alias.
     * @param   args    Arguments of command.
     * @return <tt>true</tt> if command executed successfully.
     */
    public abstract boolean execute(CommandSender sender, String alias, String[] args);

    protected final boolean hasPermission(final CommandSender sender) {
        return sender.hasPermission(getPermission());
    }

    @Override public final int hashCode() {
        return Objects.hash(name);
    }

    @Override public final boolean equals(final Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (!(obj instanceof SubCommand))
            return false;

        SubCommand other = (SubCommand) obj;
        return Objects.equals(name, other.name);
    }

    /** @return the name */
    public final String getName() {
        return name;
    }

    /** Sets name */
    protected final void setName(final String name) {
        this.name = name;
    }

    /** @return the permission */
    public final String getPermission() {
        return permission;
    }

    /** Sets permission */
    protected final void setPermission(final String permission) {
        this.permission = permission;
    }

    /** @return the aliases */
    public final String[] getAliases() {
        return aliases;
    }

    /** Sets aliases */
    protected final void setAliases(final String[] aliases) {
        this.aliases = aliases;
    }

    /** @return the usage */
    public String getUsage() {
        return usage;
    }

    /** Sets usage */
    protected void setUsage(final String usage) {
        this.usage = usage;
    }
}
