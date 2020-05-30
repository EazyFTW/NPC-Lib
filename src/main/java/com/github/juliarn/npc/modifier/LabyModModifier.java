package com.github.juliarn.npc.modifier;


import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.util.ProtocolBuffer;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LabyModModifier extends NPCModifier {

    private static final Gson GSON = new Gson();

    private static final String LABYMOD_CHANNEL_NAME = "LMC";

    private final List<byte[]> channelMessages = new CopyOnWriteArrayList<>();

    public LabyModModifier(@NotNull NPC npc) {
        super(npc);
    }

    public LabyModModifier queueAction(LabyModAction actionType, int id) {
        JsonArray array = new JsonArray();

        JsonObject data = new JsonObject();
        data.addProperty("uuid", super.npc.getGameProfile().getUUID().toString());
        data.addProperty(actionType.idPropertyName, id);

        array.add(data);

        this.channelMessages.add(this.createChannelMessageData(actionType.actionIdentifier, array));
        return this;
    }

    private byte[] createChannelMessageData(String messageKey, JsonElement jsonElement) {
        ProtocolBuffer buffer = new ProtocolBuffer();

        buffer.writeString(messageKey);
        buffer.writeString(GSON.toJson(jsonElement));

        byte[] bytes = new byte[buffer.getByteBuf().readableBytes()];
        buffer.getByteBuf().readBytes(bytes);

        return bytes;
    }

    @Override
    public void send(@NotNull Player... targetPlayers) {
        for (Player targetPlayer : targetPlayers) {
            for (byte[] channelMessage : this.channelMessages) {
                ProtocolBuffer packet = new ProtocolBuffer(MINECRAFT_VERSION >= 13 ? 25 : 255);

                packet.writeString(LABYMOD_CHANNEL_NAME);
                packet.writeBytes(channelMessage);

                packet.sendToPlayer(targetPlayer);
            }
        }
    }

    public enum LabyModAction {
        EMOTE("emote_api", "emote_id"),
        STICKER("sticker_api", "sticker_id");

        private final String actionIdentifier;

        private final String idPropertyName;

        LabyModAction(String actionIdentifier, String idPropertyName) {
            this.actionIdentifier = actionIdentifier;
            this.idPropertyName = idPropertyName;
        }

        public String getActionIdentifier() {
            return actionIdentifier;
        }

        public String getIdPropertyName() {
            return idPropertyName;
        }

    }

}
