package com.github.tartaricacid.touhoulittlemaid.init;

import com.github.tartaricacid.touhoulittlemaid.item.ItemHakureiGohei;
import com.github.tartaricacid.touhoulittlemaid.item.ItemMaidSpawnEgg;
import com.github.tartaricacid.touhoulittlemaid.item.ItemSimplePlaceholder;
import com.github.tartaricacid.touhoulittlemaid.item.ItemSmartSlab;

import net.minecraft.item.Item;

import cpw.mods.fml.common.registry.GameRegistry;

public final class InitItems {

    public static Item HAKUREI_GOHEI;
    public static Item SANAE_GOHEI;
    public static Item MAID_SPAWN_EGG;
    public static Item SMART_SLAB_INIT;
    public static Item SMART_SLAB_EMPTY;
    public static Item SMART_SLAB_HAS_MAID;
    public static Item MAID_BACKPACK_SMALL;
    public static Item MAID_BACKPACK_MIDDLE;
    public static Item MAID_BACKPACK_BIG;
    public static Item CHAIR;
    public static Item POWER_POINT;
    public static Item CAMERA;
    public static Item PHOTO;
    public static Item MAID_BED;
    public static Item SUBSTITUTE_JIZO;
    public static Item TRUMPET;

    private InitItems() {}

    public static void register() {
        HAKUREI_GOHEI = register(new ItemHakureiGohei("hakurei_gohei"), "hakurei_gohei");
        SANAE_GOHEI = register(new ItemHakureiGohei("sanae_gohei"), "sanae_gohei");
        MAID_SPAWN_EGG = register(new ItemMaidSpawnEgg(), "maid_spawn_egg");
        SMART_SLAB_INIT = register(new ItemSmartSlab("smart_slab_init", ItemSmartSlab.Type.INIT), "smart_slab_init");
        SMART_SLAB_EMPTY =
                register(new ItemSmartSlab("smart_slab_empty", ItemSmartSlab.Type.EMPTY), "smart_slab_empty");
        SMART_SLAB_HAS_MAID = register(
                new ItemSmartSlab("smart_slab_has_maid", ItemSmartSlab.Type.HAS_MAID), "smart_slab_has_maid");
        MAID_BACKPACK_SMALL = register(new ItemSimplePlaceholder("maid_backpack_small"), "maid_backpack_small");
        MAID_BACKPACK_MIDDLE = register(new ItemSimplePlaceholder("maid_backpack_middle"), "maid_backpack_middle");
        MAID_BACKPACK_BIG = register(new ItemSimplePlaceholder("maid_backpack_big"), "maid_backpack_big");
        CHAIR = register(new ItemSimplePlaceholder("chair"), "chair");
        POWER_POINT = register(new ItemSimplePlaceholder("power_point"), "power_point");
        CAMERA = register(new ItemSimplePlaceholder("camera"), "camera");
        PHOTO = register(new ItemSimplePlaceholder("photo"), "photo");
        MAID_BED = register(new ItemSimplePlaceholder("maid_bed"), "maid_bed");
        SUBSTITUTE_JIZO = register(new ItemSimplePlaceholder("substitute_jizo"), "substitute_jizo");
        TRUMPET = register(new ItemSimplePlaceholder("trumpet"), "trumpet");
    }

    private static Item register(Item item, String name) {
        GameRegistry.registerItem(item, name);
        return item;
    }
}
