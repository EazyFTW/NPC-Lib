package com.github.eazyftw.npc.modifier;

import com.github.eazyftw.npc.NPC;
import com.github.eazyftw.npc.hologram.Hologram;
import com.github.eazyftw.npc.hologram.MinecraftVersion;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
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

    public HologramModifier make(String... lines) {
        return make(Arrays.asList(lines));
    }

    public HologramModifier update(List<String> lines) {
        hologram.setText(lines);
        return this;
    }

    public HologramModifier update(String... lines) {
        return update(Arrays.asList(lines));
    }

    public Hologram getHologram() {
        return hologram;
    }
}
