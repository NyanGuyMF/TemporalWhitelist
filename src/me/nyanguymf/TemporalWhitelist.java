/**
 * 
 */
package me.nyanguymf;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.nyanguymf.commands.WhitelistCommand;
import me.nyanguymf.listeners.*;

/**
 * @author nyanguymf
 *
 */
public class TemporalWhitelist extends JavaPlugin {
    private static TemporalWhitelist instance;
    private static YamlConfiguration messagesYml;
    private Logger log = getLogger();

    public void onEnable() {
        File messagesFile = new File(getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }

        messagesYml = YamlConfiguration.loadConfiguration(messagesFile);

        setInstance(this);

        createPluginDirs();
        createConfig();

        regListeners();
        regCommands();

        log.info("Plugin enabled");
    }

    public void onDisable() {
        log.info("Plugin disabled");
    }

    public static TemporalWhitelist getInstance() {
        return instance;
    }

    public void setInstance(TemporalWhitelist inst) {
        instance = inst;
    }

    public static String getMessage(String message) {
        return messagesYml.getString(message);
    }

    public FileConfiguration getPlayerCfg(String playerName) {
        File playerFile = new File(getDataFolder() + File.separator + "players", playerName + ".yml");

        if (!playerFile.exists()) {
            createFile(playerFile);
        }

        YamlConfiguration playerYml = YamlConfiguration.loadConfiguration(playerFile);
        return playerYml;
    }
    
    private void createConfig() {
        File config = new File(getDataFolder(), "config.yml");

        if (!config.exists()) {
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
        }
    }

    private void createFile(File file) {
        try {
            file.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void createPluginDirs() {
        File pluginDir = new File(getDataFolder().getAbsolutePath());
        if (!pluginDir.exists()) {
            pluginDir.mkdir();
        }
        File playersDir = new File(getDataFolder().getAbsolutePath() + File.separator + "players");
        if (!playersDir.exists()) {
            playersDir.mkdir();
        }
    }

    private void regListeners() {
        getServer().getPluginManager().registerEvents(new LoginListener(), this);
    }

    private void regCommands() {
        getCommand("whitelist").setExecutor(new WhitelistCommand());
        getServer().getPluginCommand("whlist").setExecutor(new WhitelistCommand());
    }

    public static YamlConfiguration getMessagesYml() {
        return messagesYml;
    }

    public static void setMessagesYml(YamlConfiguration messagesYml) {
        TemporalWhitelist.messagesYml = messagesYml;
    }
}
