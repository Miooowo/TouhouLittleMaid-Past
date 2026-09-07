package com.github.tartaricacid.touhoulittlemaid.item;

import java.util.List;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.init.MaidCreativeTab;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Registers a 1.20.1 item id so recipes/lang/creative tabs can land now.
 * Behaviour is filled in later phases.
 */
public class ItemSimplePlaceholder extends Item {

    public ItemSimplePlaceholder(String name) {
        setUnlocalizedName(name);
        setTextureName(TouhouLittleMaid.MOD_ID + ":" + name);
        setCreativeTab(MaidCreativeTab.TAB);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        tooltip.add(net.minecraft.util.StatCollector.translateToLocal("tooltip.touhou_little_maid.placeholder"));
    }
}
