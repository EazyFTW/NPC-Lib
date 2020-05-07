package com.github.juliarn.npc.laby;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.github.juliarn.npc.NPC;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public final class LabyModEmoteSenderUtils {

    private LabyModEmoteSenderUtils() {
        throw new UnsupportedOperationException();
    }

    public static void playEmote(int emote, @NotNull Collection<Player> players, @NotNull NPC... npcs) {
        JsonArray array = new JsonArray();

        for (NPC npc : npcs) {
            JsonObject data = new JsonObject();

            data.addProperty("uuid", npc.getGameProfile().getUUID().toString());
            data.addProperty("emote_id", emote);

            array.add(data);
        }

        for (Player player : players) {
            sendChannelMessage(player, "emote_api", array);
        }
    }

    private static void sendChannelMessage(@NotNull Player target, @NotNull String channel, @NotNull JsonElement element) {
        ByteBuf packet;
        // Since 1.13 -> 25; Before 1.13 -> 255
        if (ProtocolLibrary.getProtocolManager().getProtocolVersion(target) > 340) {
            packet = ByteBufUtils.createPacketBuffer(25);
        } else {
            packet = ByteBufUtils.createPacketBuffer(255);
        }

        ByteBufUtils.createChannelMessage(packet, channel, element);
        PlayerConnectionUtils.sendCustomByteBuf(target, packet);
    }
}
