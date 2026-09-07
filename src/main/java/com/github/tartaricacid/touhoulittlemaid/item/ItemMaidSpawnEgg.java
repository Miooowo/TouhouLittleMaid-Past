package com.github.tartaricacid.touhoulittlemaid.item;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.MaidCreativeTab;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemMaidSpawnEgg extends Item {

    public ItemMaidSpawnEgg() {
        setUnlocalizedName("maid_spawn_egg");
        setTextureName(TouhouLittleMaid.MOD_ID + ":maid_spawn_egg");
        setCreativeTab(MaidCreativeTab.TAB);
    }

    @Override
    public boolean onItemUse(
            ItemStack stack,
            EntityPlayer player,
            World world,
            int x,
            int y,
            int z,
            int side,
            float hitX,
            float hitY,
            float hitZ) {
        if (world.isRemote) {
            return true;
        }
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        EntityMaid.spawnTamed(
                world,
                player,
                x + 0.5D + dir.offsetX,
                y + (side == 1 ? 1.0D : 0.0D),
                z + 0.5D + dir.offsetZ);
        if (!player.capabilities.isCreativeMode) {
            --stack.stackSize;
        }
        return true;
    }
}
