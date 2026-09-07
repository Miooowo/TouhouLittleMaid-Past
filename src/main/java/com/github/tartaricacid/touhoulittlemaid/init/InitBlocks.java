package com.github.tartaricacid.touhoulittlemaid.init;

import com.github.tartaricacid.touhoulittlemaid.block.BlockAltar;
import com.github.tartaricacid.touhoulittlemaid.block.BlockTombstone;
import com.github.tartaricacid.touhoulittlemaid.tileentity.TileEntityTombstone;

import net.minecraft.block.Block;

import cpw.mods.fml.common.registry.GameRegistry;

public final class InitBlocks {

    public static Block ALTAR;
    public static Block TOMBSTONE;

    private InitBlocks() {}

    public static void register() {
        ALTAR = new BlockAltar();
        GameRegistry.registerBlock(ALTAR, "altar");
        TOMBSTONE = new BlockTombstone();
        GameRegistry.registerBlock(TOMBSTONE, "tombstone");
        GameRegistry.registerTileEntity(TileEntityTombstone.class, "touhou_little_maid.tombstone");
    }
}
