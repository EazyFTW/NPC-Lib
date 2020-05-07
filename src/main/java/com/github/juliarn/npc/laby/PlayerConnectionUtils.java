package com.github.juliarn.npc.laby;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Experimental
public final class PlayerConnectionUtils {

    private PlayerConnectionUtils() {
        throw new UnsupportedOperationException();
    }

    static void sendCustomByteBuf(@NotNull Player player, @NotNull ByteBuf byteBuf) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object connection = handle.getClass().getField("playerConnection").get(handle);
            Object networkManager = connection.getClass().getMethod("a").invoke(connection);
            Channel channel = (Channel) networkManager.getClass().getField("channel").get(networkManager);
            channel.writeAndFlush(byteBuf);
        } catch (final Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
