package ru.kargond.namechanger.listeners;

import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import com.comphenix.packetwrapper.WrapperPlayServerScoreboardScore;
import com.comphenix.packetwrapper.WrapperPlayServerScoreboardTeam;
import com.comphenix.packetwrapper.WrapperPlayServerTabComplete;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.kargond.namechanger.main.MaskedPlayer;
import ru.kargond.namechanger.main.NameChanger;

import java.util.List;

public class PacketListeners {

    private NameChanger nameChanger;

    public PacketListeners(NameChanger nameChanger) {
        this.nameChanger = nameChanger;
        init();
    }

    public void init() {
        registerTabCompleteListener();
        registerPlayerInfoListener();
        registerScoreboardScoreListener();
        registerScoreboardTeamListener();
    }

    public void registerTabCompleteListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(nameChanger.getMain(),
                ListenerPriority.HIGHEST, PacketType.Play.Server.TAB_COMPLETE) {
            public void onPacketSending(PacketEvent event) {
                WrapperPlayServerTabComplete wrapper = new WrapperPlayServerTabComplete(event.getPacket());

                for (int i = 0; i < wrapper.getMatches().length; i++) {
                    String playerName = wrapper.getMatches()[i];
                    Player player = Bukkit.getPlayerExact(playerName);

                    String displayName = nameChanger.getMaskManager().getMaskedPlayer(player).getDisplayName();
                    if (displayName != null) wrapper.getMatches()[i] = displayName;
                }
            }
        });
    }

    public void registerScoreboardTeamListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(nameChanger.getMain(),
                ListenerPriority.HIGHEST, PacketType.Play.Server.SCOREBOARD_TEAM) {
            public void onPacketSending(PacketEvent event) {
                WrapperPlayServerScoreboardTeam wrapper = new WrapperPlayServerScoreboardTeam(event.getPacket());

                for (int i = 0; i < wrapper.getPlayers().size(); ++i) {
                    String playerName = wrapper.getPlayers().get(i);
                    Player player = Bukkit.getPlayerExact(playerName);

                    String displayName = nameChanger.getMaskManager().getMaskedPlayer(player).getDisplayName();

                    if (displayName != null) {
                        wrapper.getPlayers().set(i, displayName);
                    }
                }
            }
        });
    }

    public void registerPlayerInfoListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(nameChanger.getMain(),
                ListenerPriority.HIGHEST, PacketType.Play.Server.PLAYER_INFO) {
            public void onPacketSending(PacketEvent event) {
                if (event.isCancelled()) {
                    return;
                }

                WrapperPlayServerPlayerInfo wrapper = new WrapperPlayServerPlayerInfo(event.getPacket());
                List<PlayerInfoData> datalist = wrapper.getData();

                for (int i = 0; i < datalist.size(); i++) {
                    PlayerInfoData data = datalist.get(i);
                    WrappedGameProfile current = data.getProfile();

                    MaskedPlayer maskedPlayer = nameChanger.getMaskManager().getMaskedPlayer(current.getUUID());

                    if (maskedPlayer == null) {
                        continue;
                    }

                    String displayName = maskedPlayer.getDisplayName();

                    if (displayName == null) {
                        continue;
                    }

                    WrappedGameProfile profile = new WrappedGameProfile(current.getUUID(), displayName);
                    datalist.set(i, new PlayerInfoData(profile, data.getLatency(), data.getGameMode(),
                            null));
                }
                wrapper.setData(datalist);
            }
        });
    }

    public void registerScoreboardScoreListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(nameChanger.getMain(),
                ListenerPriority.HIGHEST, PacketType.Play.Server.SCOREBOARD_SCORE) {
            public void onPacketSending(PacketEvent event) {
                WrapperPlayServerScoreboardScore wrapper = new WrapperPlayServerScoreboardScore(event.getPacket());
                Player player = Bukkit.getPlayerExact(wrapper.getScoreName());

                String displayName = nameChanger.getMaskManager().getMaskedPlayer(player).getDisplayName();
                if (displayName != null) {
                    wrapper.setScoreName(displayName);
                }
            }
        });
    }
}
