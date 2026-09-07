package com.github.tartaricacid.touhoulittlemaid.init;

import com.github.tartaricacid.touhoulittlemaid.block.BlockAltar;

import net.minecraft.block.Block;

import cpw.mods.fml.common.registry.GameRegistry;

public final class InitBlocks {

    public static Block ALTAR;

    private InitBlocks() {}

    public static void register() {
        ALTAR = new BlockAltar();
        GameRegistry.registerBlock(ALTAR, "altar");
    }
}
