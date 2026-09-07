package com.github.tartaricacid.touhoulittlemaid.init;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class MaidCreativeTab {

    public static final CreativeTabs TAB = new CreativeTabs(TouhouLittleMaid.MOD_ID) {

        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return InitItems.HAKUREI_GOHEI != null ? InitItems.HAKUREI_GOHEI : Items.paper;
        }
    };

    private MaidCreativeTab() {}
}
