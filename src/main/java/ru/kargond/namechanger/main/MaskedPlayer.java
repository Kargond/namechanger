package ru.kargond.namechanger.main;

import com.mojang.authlib.GameProfile;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.UUID;

public class MaskedPlayer {

    @Setter
    private UUID uuid;
    @Getter
    private String displayName;
    @Setter
    private String defaultName;

    private NameChanger nameChanger;
    private Field nameField;

    public MaskedPlayer(UUID uuid, String displayName, String defaultName,
                        NameChanger nameChanger) {
        this.nameChanger = nameChanger;

        this.uuid = uuid;
        this.displayName = displayName;
        this.defaultName = defaultName;

        try {
            nameField = GameProfile.class.getDeclaredField("name");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        this.defaultName = getPlayer().getName();
        this.nameChanger.getMysql().updateMask(uuid, displayName);
        updateInGameData();
    }

    public void resetDisplayName() {
        this.displayName = null;
        this.nameChanger.getMysql().removeMask(uuid);
        updateInGameData();
    }

    public void updateInGameData() {
        if (displayName == null) {
            getPlayer().setDisplayName(defaultName);
            if (nameChanger.areInternalChangedEnabled()) {
                resetInternalName();
            }
        } else {
            getPlayer().setDisplayName(displayName);
            if (nameChanger.areInternalChangedEnabled()) {
                changeInternalName(displayName);
            }
        }
    }

    private void changeInternalName(String name) {
        EntityPlayer cp = ((CraftPlayer) getPlayer()).getHandle();
        GameProfile profile = cp.getProfile();

        boolean accessible = nameField.isAccessible();
        try {
            nameField.setAccessible(true);
            nameField.set(profile, name);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            nameField.setAccessible(accessible);
        }
    }

    public void resetInternalName() {
        changeInternalName(defaultName);
    }

    private Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
}
