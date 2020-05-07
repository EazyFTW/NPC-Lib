package com.github.juliarn.npc;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.github.juliarn.npc.modifier.*;
import com.github.juliarn.npc.profile.Profile;
import com.github.juliarn.npc.profile.ProfileBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

public class NPC {

    private static final Random RANDOM = new Random();

    private final Collection<Player> seeingPlayers = new CopyOnWriteArraySet<>();

    private final int entityId = RANDOM.nextInt(Short.MAX_VALUE);

    private final WrappedGameProfile gameProfile;

    private final Location location;

    private boolean lookAtPlayer;

    private boolean imitatePlayer;

    private final SpawnCustomizer spawnCustomizer;

    private NPC(Collection<WrappedSignedProperty> profileProperties, WrappedGameProfile gameProfile, Location location, boolean lookAtPlayer, boolean imitatePlayer, SpawnCustomizer spawnCustomizer) {
        this.gameProfile = gameProfile;

        profileProperties.forEach(property -> this.gameProfile.getProperties().put(property.getName(), property));

        this.location = location;
        this.lookAtPlayer = lookAtPlayer;
        this.imitatePlayer = imitatePlayer;
        this.spawnCustomizer = spawnCustomizer;
    }

    protected void show(@NotNull Player player, @NotNull JavaPlugin javaPlugin) {
        this.seeingPlayers.add(player);

        VisibilityModifier visibilityModifier = new VisibilityModifier(this);
        visibilityModifier.queuePlayerListChange(EnumWrappers.PlayerInfoAction.ADD_PLAYER).send(player);

        Bukkit.getScheduler().runTaskLater(javaPlugin, () -> {
            visibilityModifier.queueSpawn().send(player);
            this.spawnCustomizer.handleSpawn(this, player);

            // keeping the NPC longer in the player list, otherwise the skin might not be shown sometimes.
            Bukkit.getScheduler().runTaskLater(javaPlugin, () -> visibilityModifier.queuePlayerListChange(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER).send(player), 10L);
        }, 20L);
    }

    protected void hide(@NotNull Player player) {
        new VisibilityModifier(this)
                .queuePlayerListChange(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER)
                .queueDestroy()
                .send(player);

        this.removeSeeingPlayer(player);
    }

    protected void removeSeeingPlayer(Player player) {
        this.seeingPlayers.remove(player);
    }

    /**
     * @return a copy of all players seeing this NPC
     */
    public Collection<Player> getSeeingPlayers() {
        return new HashSet<>(this.seeingPlayers);
    }

    public boolean isShownFor(Player player) {
        return this.seeingPlayers.contains(player);
    }

    /**
     * Creates a new animation modifier which serves methods to play animations on an NPC
     *
     * @return a animation modifier modifying this NPC
     */
    public AnimationModifier animation() {
        return new AnimationModifier(this);
    }

    /**
     * Creates a new rotation modifier which serves methods related to entity rotation
     *
     * @return a rotation modifier modifying this NPC
     */
    public RotationModifier rotation() {
        return new RotationModifier(this);
    }

    /**
     * Creates a new equipemt modifier which serves methods to change an NPCs equipment
     *
     * @return an equipment modifier modifying this NPC
     */
    public EquipmentModifier equipment() {
        return new EquipmentModifier(this);
    }

    /**
     * Creates a new metadata modifier which serves methods to change an NPCs metadata, including sneaking etc.
     *
     * @return a metadata modifier modifying this NPC
     */
    public MetadataModifier metadata() {
        return new MetadataModifier(this);
    }


    public WrappedGameProfile getGameProfile() {
        return gameProfile;
    }

    public int getEntityId() {
        return entityId;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isLookAtPlayer() {
        return lookAtPlayer;
    }

    public void setLookAtPlayer(boolean lookAtPlayer) {
        this.lookAtPlayer = lookAtPlayer;
    }

    public boolean isImitatePlayer() {
        return imitatePlayer;
    }

    public void setImitatePlayer(boolean imitatePlayer) {
        this.imitatePlayer = imitatePlayer;
    }


    public static class Builder {

        private Collection<WrappedSignedProperty> profileProperties;

        private final String name;

        private UUID textureUUID;

        private UUID uuid = new UUID(RANDOM.nextLong(), 0);

        private Location location = new Location(Bukkit.getWorlds().get(0), 0D, 0D, 0D);

        private boolean lookAtPlayer = true;

        private boolean imitatePlayer = true;

        private SpawnCustomizer spawnCustomizer = (npc, player) -> {
        };

        /**
         * Creates a new instance of the NPC builder
         *
         * @param textureUUID textures of this profile will be fetched and shown on the NPC
         * @param name        the name the NPC should have
         */
        public Builder(@NotNull UUID textureUUID, @NotNull String name) {
            this.textureUUID = textureUUID;
            this.name = name;
        }

        /**
         * Creates a new instance of the NPC builder
         *
         * @param profileProperties a collection of profile properties, including textures
         * @param name              the name the NPC should have
         */
        public Builder(@NotNull Collection<WrappedSignedProperty> profileProperties, @NotNull String name) {
            this.profileProperties = profileProperties;
            this.name = name;
        }

        /**
         * Sets a custom uuid for the NPC instead of generating a random one.
         * This breaks the compatibility of the npc with labymod emotes.
         *
         * @param uuid the uuid the NPC should have
         * @return this builder instance
         */
        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        /**
         * Sets the location of the npc, cannot be changed afterwards
         *
         * @param location the location
         * @return this builder instance
         */
        public Builder location(@NotNull Location location) {
            this.location = location;
            return this;
        }

        /**
         * Enables/disables looking at the player, default is true
         *
         * @param lookAtPlayer if the NPC should look at the player
         * @return this builder instance
         */
        public Builder lookAtPlayer(boolean lookAtPlayer) {
            this.lookAtPlayer = lookAtPlayer;
            return this;
        }

        /**
         * Enables/disables imitation of the player, such as sneaking and hitting the player, default is true
         *
         * @param imitatePlayer if the NPC should imitate players
         * @return this builder instance
         */
        public Builder imitatePlayer(boolean imitatePlayer) {
            this.imitatePlayer = imitatePlayer;
            return this;
        }

        /**
         * Sets an executor which will be called every time the NPC is spawned for a certain player.
         * Permanent NPC modifications should be done in this method, otherwise they will be lost at the next respawn of the NPC.
         *
         * @param spawnCustomizer the spawn customizer which will be called on every spawn
         * @return this builder instance
         */
        public Builder spawnCustomizer(@NotNull SpawnCustomizer spawnCustomizer) {
            this.spawnCustomizer = spawnCustomizer;
            return this;
        }

        /**
         * Passes the NPC to a pool which handles events, spawning and destruction of this NPC for players
         *
         * @param pool the pool the NPC will be passed to
         * @return this builder instance
         */
        @NotNull
        public NPC build(@NotNull NPCPool pool) {
            if (this.profileProperties == null) {
                Profile profile = new ProfileBuilder(this.textureUUID).complete(true).build();

                this.profileProperties = profile.getWrappedProperties();
            }

            NPC npc = new NPC(
                    this.profileProperties,
                    new WrappedGameProfile(this.uuid, this.name),
                    this.location,
                    this.lookAtPlayer,
                    this.imitatePlayer,
                    this.spawnCustomizer
            );
            pool.takeCareOf(npc);

            return npc;
        }

    }

}
