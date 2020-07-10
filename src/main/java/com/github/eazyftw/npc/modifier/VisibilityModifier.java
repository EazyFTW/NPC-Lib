package com.github.eazyftw.npc.modifier;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.github.eazyftw.npc.NPC;
import com.github.eazyftw.npc.wrappers.WrapperPlayServerScoreboardTeam;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class VisibilityModifier extends NPCModifier {

    public VisibilityModifier(@NotNull NPC npc) {
        super(npc);
    }

    public VisibilityModifier queuePlayerListChange(EnumWrappers.PlayerInfoAction action) {
        PacketContainer packetContainer = super.newContainer(PacketType.Play.Server.PLAYER_INFO, false);

        packetContainer.getPlayerInfoAction().write(0, action);

        PlayerInfoData playerInfoData = new PlayerInfoData(
                super.npc.getGameProfile(),
                20,
                EnumWrappers.NativeGameMode.NOT_SET,
                (action == EnumWrappers.PlayerInfoAction.REMOVE_PLAYER ? WrappedChatComponent.fromJson("{\"text\":\"[NPC] " + super.npc.getGameProfile().getName() + "\",\"color\":\"dark_gray\"}") : WrappedChatComponent.fromText(""))
        );

        packetContainer.getPlayerInfoDataLists().write(0, Collections.singletonList(playerInfoData));

        return this;
    }

    public VisibilityModifier queuePlayerTeamChange() {
        WrapperPlayServerScoreboardTeam wrapperSBT = new WrapperPlayServerScoreboardTeam(super.newContainer(PacketType.Play.Server.SCOREBOARD_TEAM));

        wrapperSBT.setName(this.npc.getGameProfile().getName());
        wrapperSBT.setDisplayName(WrappedChatComponent.fromText(this.npc.getGameProfile().getName()));
        wrapperSBT.setCollisionRule("never");
        wrapperSBT.setNameTagVisibility("never");
        wrapperSBT.setMode(WrapperPlayServerScoreboardTeam.Mode.TEAM_CREATED);
        wrapperSBT.setPlayers(Collections.singletonList(this.npc.getGameProfile().getName()));

        return this;
    }

    public VisibilityModifier queuePlayerTeamDelete() {
        WrapperPlayServerScoreboardTeam wrapperSBT = new WrapperPlayServerScoreboardTeam(super.newContainer(PacketType.Play.Server.SCOREBOARD_TEAM));

        wrapperSBT.setName(this.npc.getGameProfile().getName());
        wrapperSBT.setMode(WrapperPlayServerScoreboardTeam.Mode.TEAM_REMOVED);

        return this;
    }

    public VisibilityModifier queueSpawn() {
        PacketContainer packetContainer = super.newContainer(PacketType.Play.Server.NAMED_ENTITY_SPAWN);

        packetContainer.getUUIDs().write(0, super.npc.getGameProfile().getUUID());

        double x = super.npc.getLocation().getX();
        double y = super.npc.getLocation().getY();
        double z = super.npc.getLocation().getZ();

        if (MINECRAFT_VERSION < 9) {
            packetContainer.getIntegers()
                    .write(1, (int) Math.floor(x * 32.0D))
                    .write(2, (int) Math.floor(y * 32.0D))
                    .write(3, (int) Math.floor(z * 32.0D));
        } else {
            packetContainer.getDoubles()
                    .write(0, x)
                    .write(1, y)
                    .write(2, z);
        }

        packetContainer.getBytes()
                .write(0, (byte) (super.npc.getLocation().getYaw() * 256F / 360F))
                .write(1, (byte) (super.npc.getLocation().getPitch() * 256F / 360F));

        if (MINECRAFT_VERSION < 15) packetContainer.getDataWatcherModifier().write(0, new WrappedDataWatcher());

        return this;
    }

    public VisibilityModifier queueDestroy() {
        PacketContainer packetContainer = super.newContainer(PacketType.Play.Server.ENTITY_DESTROY, false);
        packetContainer.getIntegerArrays().write(0, new int[]{super.npc.getEntityId()});
        return this;
    }
}
