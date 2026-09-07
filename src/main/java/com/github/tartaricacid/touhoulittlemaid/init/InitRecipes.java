package com.github.tartaricacid.touhoulittlemaid.init;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;

public final class InitRecipes {

    private InitRecipes() {}

    public static void register() {
        GameRegistry.addShapedRecipe(
                new ItemStack(InitItems.HAKUREI_GOHEI),
                "  P",
                " S ",
                "S  ",
                'P',
                Items.paper,
                'S',
                Items.stick);
        GameRegistry.addShapedRecipe(
                new ItemStack(InitItems.SANAE_GOHEI),
                "  L",
                " S ",
                "S  ",
                'L',
                new ItemStack(Items.dye, 1, 2),
                'S',
                Items.stick);
        GameRegistry.addShapedRecipe(
                new ItemStack(InitItems.SMART_SLAB_INIT),
                " P ",
                "PIP",
                " P ",
                'P',
                Items.paper,
                'I',
                Items.iron_ingot);
        GameRegistry.addShapedRecipe(
                new ItemStack(InitBlocks.ALTAR), "OOO", "O O", "OOO", 'O', Blocks.obsidian);
        GameRegistry.addShapelessRecipe(new ItemStack(InitItems.MAID_SPAWN_EGG), Items.egg, Items.paper);
    }
}
