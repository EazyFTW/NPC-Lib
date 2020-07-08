package com.github.eazyftw.npc.modifier;

import com.github.eazyftw.npc.NPC;
import com.github.eazyftw.npc.hologram.Hologram;
import com.github.eazyftw.npc.hologram.MinecraftVersion;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HologramModifier extends NPCModifier {

    public HologramModifier(@NotNull NPC npc) {
        super(npc);
    }

    private Hologram hologram;

    public HologramModifier make(List<String> lines) {
        hologram = new Hologram(MinecraftVersion.getServersVersion(), npc.getLocation().clone().add(0, 0.5, 0), lines);
        return this;
    }

    public HologramModifier update(List<String> lines) {
        hologram.setText(lines);
        return this;
    }

    public Hologram getHologram() {
        return hologram;
    }
}
