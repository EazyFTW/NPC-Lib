package com.github.eazyftw.npc.hologram;

import org.bukkit.Bukkit;

public enum MinecraftVersion {

    UNKNOWN,
    V1_8_R2,
    V1_8_R3,
    V1_9_R1,
    V1_9_R2,
    V1_10_R1,
    V1_11_R1,
    V1_12_R1,
    V1_13_R1,
    V1_13_R2,
    V1_14_R1,
    V1_15_R1,
    V1_16_R1;

    public boolean isAboveOrEqual(MinecraftVersion compare) {
        return ordinal() >= compare.ordinal();
    }

    public static MinecraftVersion getServersVersion() {
        MinecraftVersion mcVersion;
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].toUpperCase();
        try {
            mcVersion = MinecraftVersion.valueOf(version);
            return mcVersion;
        } catch (Exception ex) {
            return UNKNOWN;
        }
    }
}