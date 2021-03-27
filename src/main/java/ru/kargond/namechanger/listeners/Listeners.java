package ru.kargond.namechanger.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.kargond.namechanger.main.MaskedPlayer;
import ru.kargond.namechanger.main.NameChanger;

public class Listeners implements Listener {

    private NameChanger nameChanger;

    public Listeners(NameChanger nameChanger) {
        this.nameChanger = nameChanger;
        Bukkit.getPluginManager().registerEvents(this, nameChanger.getMain());
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent e) {
        nameChanger.getMysql().loadMask(e.getUniqueId(), e.getName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogin(PlayerJoinEvent e) {
        MaskedPlayer maskedPlayer = nameChanger.getMaskManager().getMaskedPlayer(e.getPlayer());
        maskedPlayer.updateInGameData();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent e) {
        nameChanger.getMaskManager().removePlayer(e.getPlayer());
    }
}
