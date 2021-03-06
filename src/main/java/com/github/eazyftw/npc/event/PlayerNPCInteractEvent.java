package com.github.eazyftw.npc.event;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.github.eazyftw.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerNPCInteractEvent extends PlayerEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final NPC npc;

    private final NPC.NPCAction action;

    public PlayerNPCInteractEvent(@NotNull Player who, @NotNull NPC npc, @NotNull NPC.NPCAction action) {
        super(who);
        this.npc = npc;
        this.action = action;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @NotNull
    public NPC getNPC() {
        return npc;
    }

    @NotNull
    public NPC.NPCAction getAction() {
        return action;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

}
