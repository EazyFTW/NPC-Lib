package com.github.juliarn.npc.laby;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

public final class ByteBufUtils {

    private ByteBufUtils() {
        throw new UnsupportedOperationException();
    }

    private static final Gson GSON = new Gson();

    @NotNull
    public static ByteBuf createPacketBuffer(int packetID) {
        ByteBuf byteBuf = Unpooled.buffer();
        writeVarInt(byteBuf, packetID);
        return byteBuf;
    }

    public static void createChannelMessage(@NotNull ByteBuf byteBuf, @NotNull String messageDataKey, @NotNull JsonElement data) {
        writeString(byteBuf, messageDataKey);
        writeString(byteBuf, GSON.toJson(data));
    }

    public static void writeString(@NotNull ByteBuf byteBuf, @NotNull String string) {
        byte[] values = string.getBytes(StandardCharsets.UTF_8);
        writeVarInt(byteBuf, values.length);
        byteBuf.writeBytes(values);
    }

    @NotNull
    public static String readString(@NotNull ByteBuf byteBuf) {
        int integer = readVarInt(byteBuf);
        byte[] buffer = new byte[integer];
        byteBuf.readBytes(buffer, 0, integer);

        return new String(buffer, StandardCharsets.UTF_8);
    }

    public static void writeVarInt(@NotNull ByteBuf byteBuf, int value) {
        do {
            byte temp = (byte) (value & 0b01111111);
            value >>>= 7;
            if (value != 0) {
                temp |= 0b10000000;
            }
            byteBuf.writeByte(temp);
        } while (value != 0);
    }

    public static int readVarInt(@NotNull ByteBuf byteBuf) {
        int numRead = 0;
        int result = 0;
        byte read;
        do {
            read = byteBuf.readByte();
            int value = (read & 0b01111111);
            result |= (value << (7 * numRead));

            numRead++;
            if (numRead > 5) {
                throw new RuntimeException("VarInt is too big");
            }
        } while ((read & 0b10000000) != 0);

        return result;
    }
}
