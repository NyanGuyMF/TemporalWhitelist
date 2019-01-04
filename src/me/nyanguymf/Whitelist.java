/**
 * 
 */
package me.nyanguymf;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.nyanguymf.util.DateUtils;

/**
 * @author nyanguymf
 *
 */
public class Whitelist {
    private TemporalWhitelist tw;

    /**
     * 
     */
    public Whitelist() {
        tw = TemporalWhitelist.getInstance();
    }

    public void off() {
        FileConfiguration cfg = tw.getConfig();
        cfg.set("whitelist", false);
        saveCfg(cfg);
    }

    public void on() {
        FileConfiguration cfg = tw.getConfig();
        cfg.set("whitelist", true);
        saveCfg(cfg);
    }

    public boolean isEnabled() {
        return tw.getConfig().getBoolean("whitelist");
    }

    public boolean isWhitelisted(String name) {
        if (!tw.getPlayerCfg(name).contains("whitelisted")) {
            return false;
        }

        if (!tw.getPlayerCfg(name).getBoolean("whitelisted")) {
            return false;
        }

        if (tw.getPlayerCfg(name).getLong("until") - System.currentTimeMillis() / 1000L <= 0) {
            return false;
        }

        return true;
    }

    public void add(CommandSender sender, String name, String timeDiff) {
        try {
            long until = DateUtils.parseDateDiff(timeDiff, true);

            FileConfiguration playerYml = tw.getPlayerCfg(name);
            playerYml.set("whitelisted", true);
            playerYml.set("until", until);
            playerYml.save(new File(tw.getDataFolder() + File.separator + "players", name + ".yml"));

            String  added   = TemporalWhitelist.getMessage("added-successfuly");
                    added   = added.replaceAll("%player%", name); 
                    added   = added.replaceAll("%until%", new Date(until * 1000).toString());

            sender.sendMessage(added);
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(e.getMessage());
        }
    }

    public void reload() {
        tw.reloadConfig();
        TemporalWhitelist.setMessagesYml(YamlConfiguration.loadConfiguration(new File(tw.getDataFolder(), "messages.yml")));
    }

    public void remove(CommandSender sender, String name) {
        FileConfiguration playerYml = tw.getPlayerCfg(name);
        playerYml.set("whitelisted", false);

        String  removed = TemporalWhitelist.getMessage("removed-successfuly");
                removed = removed.replaceAll("%player%", name);

        sender.sendMessage(removed);
    }

    private void saveCfg(FileConfiguration cfg) {
        try {
            cfg.save(new File(tw.getDataFolder(), "config.yml"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
