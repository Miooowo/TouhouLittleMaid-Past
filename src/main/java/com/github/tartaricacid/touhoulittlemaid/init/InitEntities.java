package com.github.tartaricacid.touhoulittlemaid.init;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;

import cpw.mods.fml.common.registry.EntityRegistry;

public final class InitEntities {

    public static final int MAID_ID = 0;

    private InitEntities() {}

    public static void register() {
        EntityRegistry.registerModEntity(
                EntityMaid.class, "maid", MAID_ID, TouhouLittleMaid.instance, 80, 3, true);
    }
}
