package ru.kargond.namechanger.main;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.kargond.namechanger.listeners.Listeners;
import ru.kargond.namechanger.listeners.PacketListeners;
import ru.kargond.namechanger.mysql.MySQL;

public class NameChanger {

    @Getter
    private JavaPlugin main;

    @Getter
    private MySQL mysql;

    @Getter
    private MaskManager maskManager;

    public NameChanger(JavaPlugin main) {
        this.main = main;

        main.saveDefaultConfig();
        FileConfiguration config = main.getConfig();
        this.mysql = new MySQL(config.getString("mysql.host"),
                config.getString("mysql.name"),
                config.getString("mysql.password"),
                config.getString("mysql.database"), this);

        this.maskManager = new MaskManager(this);

        new Listeners(this);
        new Commands(this);
        new PacketListeners(this);
    }

    public boolean areInternalChangedEnabled() {
        return main.getConfig().getBoolean("settings.changeInternalPlayerName");
    }
}
