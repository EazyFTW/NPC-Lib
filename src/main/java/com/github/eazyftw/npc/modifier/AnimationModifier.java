package com.github.eazyftw.npc.modifier;

import com.comphenix.protocol.PacketType;
import com.github.eazyftw.npc.NPC;
import org.jetbrains.annotations.NotNull;

public class AnimationModifier extends NPCModifier {

    public AnimationModifier(@NotNull NPC npc) {
        super(npc);
    }

    public AnimationModifier queue(@NotNull EntityAnimation entityAnimation) {
        return this.queue(entityAnimation.ordinal());
    }

    public AnimationModifier queue(int animationId) {
        super.newContainer(PacketType.Play.Server.ANIMATION).getIntegers().write(1, animationId);
        return this;
    }

    public enum EntityAnimation {

        //DO NOT CHANGE ORDER
        SWING_MAIN_ARM,
        TAKE_DAMAGE,
        LEAVE_BED,
        SWING_OFF_HAND,
        CRITICAL_EFFECT,
        MAGIC_CRITICAL_EFFECT;

    }
}
