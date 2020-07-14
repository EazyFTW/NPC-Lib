package com.github.eazyftw.npc;


import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.github.eazyftw.npc.event.PlayerNPCInteractEvent;
import com.github.eazyftw.npc.modifier.AnimationModifier;
import com.github.eazyftw.npc.modifier.MetadataModifier;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NPCPool implements Listener {

    private final JavaPlugin javaPlugin;

    private final double spawnDistance;

    private final double actionDistance;

    private final long tabListRemoveTicks;

    private final Map<Integer, NPC> npcMap = new ConcurrentHashMap<>();
    private final Set<UUID> delay = new HashSet<>();

    /**
     * Creates a new NPC pool which handles events, spawning and destruction of the NPCs for players
     *
     * @param javaPlugin the instance of the plugin which creates this pool
     */
    public NPCPool(@NotNull JavaPlugin javaPlugin) {
        this(javaPlugin, 50, 20, 30);
    }

    /**
     * Creates a new NPC pool which handles events, spawning and destruction of the NPCs for players
     *
     * @param javaPlugin         the instance of the plugin which creates this pool
     * @param spawnDistance      the distance in which NPCs are spawned for players
     * @param actionDistance     the distance in which NPC actions are displayed for players
     * @param tabListRemoveTicks the time in ticks after which the NPC will be removed from the players tab
     */
    public NPCPool(@NotNull JavaPlugin javaPlugin, int spawnDistance, int actionDistance, long tabListRemoveTicks) {
        Preconditions.checkArgument(spawnDistance > 0 && actionDistance > 0, "Distance has to be > 0!");
        Preconditions.checkArgument(actionDistance <= spawnDistance, "Action distance cannot be higher than spawn distance!");
        Preconditions.checkArgument(tabListRemoveTicks > 0, "TabListRemoveTicks have to be > 0!");

        this.javaPlugin = javaPlugin;

        this.spawnDistance = spawnDistance * spawnDistance;
        this.actionDistance = actionDistance * actionDistance;
        this.tabListRemoveTicks = tabListRemoveTicks;

        Bukkit.getPluginManager().registerEvents(this, javaPlugin);

        this.addInteractListener();
        this.npcTick();
    }

    private void addInteractListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this.javaPlugin, PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if(delay.contains(event.getPlayer().getUniqueId())) return;
                delay.add(event.getPlayer().getUniqueId());
                PacketContainer packetContainer = event.getPacket();
                int targetId = packetContainer.getIntegers().read(0);

                if (npcMap.containsKey(targetId)) {
                    NPC npc = npcMap.get(targetId);
                    EnumWrappers.EntityUseAction actionPacket = packetContainer.getEntityUseActions().read(0);
                    NPC.NPCAction action;
                    if(actionPacket.name().contains("ATTACK")) {
                        if(event.getPlayer().isSneaking()) {
                            action = NPC.NPCAction.SHIFT_LEFT_CLICK;
                        } else {
                            action = NPC.NPCAction.LEFT_CLICK;
                        }
                    } else {
                        if(event.getPlayer().isSneaking()) {
                            action = NPC.NPCAction.SHIFT_RIGHT_CLICK;
                        } else {
                            action = NPC.NPCAction.RIGHT_CLICK;
                        }
                    }

                    Bukkit.getScheduler().runTask(javaPlugin, () ->
                        Bukkit.getPluginManager().callEvent(new PlayerNPCInteractEvent(event.getPlayer(), npc, action)
                    ));
                    Bukkit.getScheduler().runTaskLater(javaPlugin, () -> {
                        delay.remove(event.getPlayer().getUniqueId());
                    }, 5L);
                }
            }
        });
    }

    private void npcTick() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(this.javaPlugin, () -> {
            for (Player player : ImmutableList.copyOf(Bukkit.getOnlinePlayers())) {
                for (NPC npc : this.npcMap.values()) {
                    if (!npc.getLocation().getWorld().equals(player.getLocation().getWorld())) continue;

                    double distance = npc.getLocation().distanceSquared(player.getLocation());

                    if (distance >= this.spawnDistance && npc.isShownFor(player)) {
                        npc.hide(player, this.javaPlugin);
                        if(npc.hologram() != null && npc.hologram().getHologram() != null) npc.hologram().getHologram().hide(player);
                    } else if (distance <= this.spawnDistance && !npc.isShownFor(player)) {
                        npc.show(player, this.javaPlugin, this.tabListRemoveTicks);
                        if(npc.hologram() != null && npc.hologram().getHologram() != null) npc.hologram().getHologram().show(player);
                    }

                    if (npc.isLookAtPlayer() && distance <= this.actionDistance) {
                        npc.rotation().queueLookAt(player.getLocation()).send(player);
                    }
                }
            }
        }, 20, 2);
    }

    protected void takeCareOf(@NotNull NPC npc) {
        this.npcMap.put(npc.getEntityId(), npc);
    }

    @Nullable
    public NPC getNPC(int entityId) {
        return this.npcMap.get(entityId);
    }

    public void removeNPC(int entityId) {
        NPC npc = this.getNPC(entityId);

        if (npc != null) {
            this.npcMap.remove(entityId);
            npc.getSeeingPlayers().forEach(p -> npc.hide(p, this.javaPlugin));
        }
    }

    @EventHandler
    public void handleQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        this.npcMap.values().stream()
                .filter(npc -> npc.isShownFor(player))
                .forEach(npc -> {
                    npc.hasTeamRegistered.remove(player.getUniqueId());
                    npc.removeSeeingPlayer(player);
                });
    }

    @EventHandler
    public void handleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        this.npcMap.values().stream()
                .filter(npc -> npc.isImitatePlayer() && npc.isShownFor(player) && npc.getLocation().distanceSquared(player.getLocation()) <= this.actionDistance)
                .forEach(npc -> npc.metadata().queue(MetadataModifier.EntityMetadata.SNEAKING, event.isSneaking()).send(player));
    }

    @EventHandler
    public void handleClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            this.npcMap.values().stream()
                    .filter(npc -> npc.isImitatePlayer() && npc.isShownFor(player) && npc.getLocation().distanceSquared(player.getLocation()) <= this.actionDistance)
                    .forEach(npc -> npc.animation().queue(AnimationModifier.EntityAnimation.SWING_MAIN_ARM).send(player));
        }
    }

    /**
     * @return a copy of the NPCs this pool manages
     */
    public Collection<NPC> getNPCs() {
        return new HashSet<>(this.npcMap.values());
    }

}
