package ru.kargond.namechanger;

import org.bukkit.plugin.java.JavaPlugin;
import ru.kargond.namechanger.main.NameChanger;

public class Main extends JavaPlugin {

    public void onEnable() {
        new NameChanger(this);
    }
}
