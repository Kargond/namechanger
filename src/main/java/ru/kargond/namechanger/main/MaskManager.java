package ru.kargond.namechanger.main;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MaskManager {

    private NameChanger nameChanger;

    private Map<UUID, MaskedPlayer> masks = new HashMap<>();

    public MaskManager(NameChanger nameChanger) {
        this.nameChanger = nameChanger;
    }

    public MaskedPlayer getMaskedPlayer(UUID uuid) {
        return this.masks.get(uuid);
    }

    public MaskedPlayer getMaskedPlayer(Player player) {
        return getMaskedPlayer(player.getUniqueId());
    }

    public void createMaskedPlayer(UUID uuid, String displayName, String defaultName) {
        masks.put(uuid, new MaskedPlayer(uuid, displayName, defaultName, nameChanger));
    }

    public void removePlayer(UUID uuid) {
        MaskedPlayer maskedPlayer = masks.remove(uuid);
        maskedPlayer.resetInternalName();
    }

    public void removePlayer(Player player) {
        removePlayer(player.getUniqueId());
    }
}
