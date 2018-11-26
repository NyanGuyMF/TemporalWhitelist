/**
 * 
 */
package me.nyanguymf.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import me.nyanguymf.TemporalWhitelist;
import me.nyanguymf.Whitelist;

/**
 * @author nyanguymf
 *
 */
public class LoginListener implements Listener {
    private Whitelist wh = new Whitelist();

    /**
     * 
     */
    public LoginListener() {
        // TODO Auto-generated constructor stub
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
        if (!wh.isEnabled()) {
            return;
        }

        String playerName = event.getName();

        if (!wh.isWhitelisted(playerName)) {
            // TODO add config for messaging
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, TemporalWhitelist.getMessage("not-whitelisted"));
            return;
        }
    }
}
