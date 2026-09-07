package com.github.tartaricacid.touhoulittlemaid.entity.task;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;

/**
 * Task ids match Touhou Little Maid 1.20.1 ({@code touhou_little_maid:<id>}).
 */
public enum MaidTask {
    IDLE("idle", true),
    ATTACK("attack", true),
    RANGED_ATTACK("ranged_attack", false),
    CROSSBOW_ATTACK("crossbow_attack", false),
    DANMAKU_ATTACK("danmaku_attack", false),
    TRIDENT_ATTACK("trident_attack", false),
    FARM("farm", true),
    SUGAR_CANE("sugar_cane", false),
    MELON("melon", false),
    COCOA("cocoa", false),
    HONEY("honey", false),
    GRASS("grass", true),
    SNOW("snow", false),
    FEED("feed", false),
    SHEARS("shears", true),
    MILK("milk", true),
    TORCH("torch", true),
    FEED_ANIMAL("feed_animal", true),
    FISHING("fishing", false),
    EXTINGUISHING("extinguishing", false),
    BOARD_GAMES("board_games", false);

    public final String id;
    public final boolean implemented;

    MaidTask(String id, boolean implemented) {
        this.id = id;
        this.implemented = implemented;
    }

    public String namespacedId() {
        return TouhouLittleMaid.MOD_ID + ":" + id;
    }

    public String translationKey() {
        return "task.touhou_little_maid." + id;
    }

    /** World-work tasks pause owner-follow so EntityAI can path to crops/mobs. */
    public boolean isWorldWork() {
        return this == FARM
                || this == SHEARS
                || this == TORCH
                || this == MILK
                || this == FEED_ANIMAL
                || this == GRASS;
    }

    public MaidTask nextImplemented() {
        MaidTask[] values = values();
        int index = ordinal();
        for (int i = 1; i <= values.length; i++) {
            MaidTask candidate = values[(index + i) % values.length];
            if (candidate.implemented) {
                return candidate;
            }
        }
        return IDLE;
    }

    public static MaidTask byId(String id) {
        if (id == null) {
            return IDLE;
        }
        String key = id;
        int colon = key.indexOf(':');
        if (colon >= 0) {
            key = key.substring(colon + 1);
        }
        for (MaidTask task : values()) {
            if (task.id.equals(key)) {
                return task;
            }
        }
        return IDLE;
    }

    public static MaidTask byOrdinal(int ordinal) {
        MaidTask[] values = values();
        if (ordinal < 0 || ordinal >= values.length) {
            return IDLE;
        }
        return values[ordinal];
    }
}
