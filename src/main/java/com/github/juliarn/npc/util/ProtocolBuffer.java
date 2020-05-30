package com.github.juliarn.npc.util;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

public class ProtocolBuffer {

    private static Method GET_HANDLE_METHOD;
    private static Field PLAYER_CONNECTION_FIELD;
    private static Method GET_NETWORK_MANAGER_METHOD;
    private static Field CHANNEL_FIELD;

    private final ByteBuf byteBuf = Unpooled.buffer();

    public ProtocolBuffer() {
    }

    public ProtocolBuffer(int id) {
        this.writeVarInt(id);
    }

    public void writeString(@NotNull String string) {
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        this.writeVarInt(bytes.length);
        this.byteBuf.writeBytes(bytes);
    }

    public void writeVarInt(int value) {
        do {
            byte temp = (byte) (value & 0b01111111);
            value >>>= 7;
            if (value != 0) {
                temp |= 0b10000000;
            }
            this.byteBuf.writeByte(temp);
        } while (value != 0);
    }

    public void writeBytes(byte[] bytes) {
        this.byteBuf.writeBytes(bytes);
    }

    public void sendToPlayer(Player player) {
        try {
            if (GET_HANDLE_METHOD == null) {
                GET_HANDLE_METHOD = player.getClass().getMethod("getHandle");

                Object handle = GET_HANDLE_METHOD.invoke(player);
                PLAYER_CONNECTION_FIELD = handle.getClass().getField("playerConnection");

                Object connection = PLAYER_CONNECTION_FIELD.get(handle);
                GET_NETWORK_MANAGER_METHOD = connection.getClass().getMethod("a");

                Object networkManager = GET_NETWORK_MANAGER_METHOD.invoke(connection);
                CHANNEL_FIELD = networkManager.getClass().getField("channel");
            }

            Object handle = GET_HANDLE_METHOD.invoke(player);
            Object connection = PLAYER_CONNECTION_FIELD.get(handle);
            Object networkManager = GET_NETWORK_MANAGER_METHOD.invoke(connection);

            Channel channel = (Channel) CHANNEL_FIELD.get(networkManager);
            channel.writeAndFlush(this.byteBuf);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchFieldException | NoSuchMethodException exception) {
            exception.printStackTrace();
        }
    }

    public ByteBuf getByteBuf() {
        return byteBuf;
    }

}
