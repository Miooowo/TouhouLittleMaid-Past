package com.github.tartaricacid.touhoulittlemaid.block;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.init.MaidCreativeTab;
import com.github.tartaricacid.touhoulittlemaid.item.ItemHakureiGohei;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

/**
 * Single-block stand-in for the 1.20.1 multi-block altar.
 * Using a gohei on the altar still summons a maid (same as using the gohei on any block).
 */
public class BlockAltar extends Block {

    public BlockAltar() {
        super(Material.rock);
        setHardness(2.0F);
        setResistance(10.0F);
        setStepSound(soundTypeStone);
        setBlockName("altar");
        setBlockTextureName(TouhouLittleMaid.MOD_ID + ":altar");
        setCreativeTab(MaidCreativeTab.TAB);
        setHarvestLevel("pickaxe", 1);
    }

    @Override
    public boolean onBlockActivated(
            World world,
            int x,
            int y,
            int z,
            EntityPlayer player,
            int side,
            float hitX,
            float hitY,
            float hitZ) {
        ItemStack held = player.getCurrentEquippedItem();
        if (ItemHakureiGohei.isGohei(held)) {
            return false;
        }
        if (!world.isRemote) {
            player.addChatMessage(new ChatComponentTranslation("message.touhou_little_maid.altar.hint"));
        }
        return true;
    }
}
