package com.github.eazyftw.npc.modifier;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.github.eazyftw.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.entity.EntityPoseChangeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class MetadataModifier extends NPCModifier {

    private final List<WrappedWatchableObject> metadata = new ArrayList<>();

    public MetadataModifier(@NotNull NPC npc) {
        super(npc);
    }

    public <I, O> MetadataModifier queue(@NotNull EntityMetadata<I, O> metadata, @NotNull I value) {
        return this.queue(metadata.getIndex(), metadata.getMapper().apply(value), metadata.getOutputType());
    }

    public <T> MetadataModifier queue(int index, @NotNull T value, @NotNull Class<T> clazz) {
        return this.queue(index, value, MINECRAFT_VERSION < 9 ? null : WrappedDataWatcher.Registry.get(clazz));
    }

    public <T> MetadataModifier queue(int index, @NotNull T value, @Nullable WrappedDataWatcher.Serializer serializer) {
        this.metadata.add(
                serializer == null ?
                        new WrappedWatchableObject(
                                index,
                                value
                        ) :
                        new WrappedWatchableObject(
                                new WrappedDataWatcher.WrappedDataWatcherObject(index, serializer),
                                value
                        )
        );

        return this;
    }

    @Override
    public void send(@NotNull Player... targetPlayers) {
        PacketContainer packetContainer = super.newContainer(PacketType.Play.Server.ENTITY_METADATA);

        packetContainer.getIntegers().write(0, npc.getEntityId());
        packetContainer.getWatchableCollectionModifier().write(0, this.metadata);

        super.send(targetPlayers);
    }

    public static class EntityMetadata<I, O> {

        public static final EntityMetadata<Boolean, Byte> ON_FIRE = new EntityMetadata<>(
                0,
                Byte.class,
                Collections.emptyList(),
                input -> (byte) (input ? 0x01 : 0)
        );

        public static final EntityMetadata<Boolean, Byte> SNEAKING = new EntityMetadata<>(
                0,
                Byte.class,
                Collections.emptyList(),
                input -> (byte) (input ? 0x02 : 0)
        );

        public static final EntityMetadata<Boolean, Byte> SPRINTING = new EntityMetadata<>(
                0,
                Byte.class,
                Collections.emptyList(),
                input -> (byte) (input ? 0x08 : 0)
        );

        public static final EntityMetadata<Boolean, Byte> SWIMMING = new EntityMetadata<>(
                0,
                Byte.class,
                Collections.emptyList(),
                input -> (byte) (input ? 0x10 : 0)
        );

        public static final EntityMetadata<Boolean, Byte> INVISIBLE = new EntityMetadata<>(
                0,
                Byte.class,
                Collections.emptyList(),
                input -> (byte) (input ? 0x20 : 0)
        );

        public static final EntityMetadata<Boolean, Byte> GLOWING = new EntityMetadata<>(
                0,
                Byte.class,
                Collections.emptyList(),
                input -> (byte) (input ? 0x40 : 0)
        );

        public static final EntityMetadata<Boolean, Byte> FLYING_ELYTRA = new EntityMetadata<>(
                0,
                Byte.class,
                Collections.emptyList(),
                input -> (byte) (input ? 0x80 : 0)
        );

        public static final EntityMetadata<Boolean, Byte> SKIN_LAYERS = new EntityMetadata<>(
                10,
                Byte.class,
                Arrays.asList(9, 9, 10, 14, 14, 15),
                input -> (byte) (input ? 0xff : 0)
        );

        public static final EntityMetadata<Boolean, Integer> SLEEPING = new EntityMetadata<>(
                6,
                Integer.class,
                Collections.emptyList(),
                input -> (input ? 2 : 0)
        );

        private final int baseIndex;

        private final Class<O> outputType;

        private final Collection<Integer> shiftVersions;

        private final Function<I, O> mapper;

        EntityMetadata(int baseIndex, Class<O> outputType, Collection<Integer> shiftVersions, Function<I, O> mapper) {
            this.baseIndex = baseIndex;
            this.outputType = outputType;
            this.shiftVersions = shiftVersions;
            this.mapper = mapper;
        }

        public int getIndex() {
            return this.baseIndex + Math.toIntExact(this.shiftVersions.stream().filter(minor -> NPCModifier.MINECRAFT_VERSION >= minor).count());
        }

        public Class<O> getOutputType() {
            return outputType;
        }

        public Function<I, O> getMapper() {
            return mapper;
        }

    }

    public enum EntityPose {

        //DO NOT CHANGE ORDER
        STANDING,
        FALL_FLYING,
        SLEEPING,
        SWIMMING,
        SPIN_ATTACK,
        SNEAKING,
        DYING;

    }
}
