package com.github.tartaricacid.touhoulittlemaid.item;

import com.github.tartaricacid.touhoulittlemaid.Config;
import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.MaidCreativeTab;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemHakureiGohei extends Item {

    public ItemHakureiGohei(String name) {
        setUnlocalizedName(name);
        setTextureName(TouhouLittleMaid.MOD_ID + ":" + name);
        setCreativeTab(MaidCreativeTab.TAB);
        setMaxStackSize(1);
        setMaxDamage(1200);
        setFull3D();
    }

    public static boolean isGohei(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemHakureiGohei;
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
        if (!Config.goheiCanSpawnMaid || player == null) {
            return false;
        }
        if (world.isRemote) {
            return true;
        }
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        double spawnX = x + 0.5D + dir.offsetX;
        double spawnY = y + (side == 1 ? 1.0D : 0.0D);
        double spawnZ = z + 0.5D + dir.offsetZ;
        EntityMaid.spawnTamed(world, player, spawnX, spawnY, spawnZ);
        stack.damageItem(1, player);
        player.addChatMessage(new ChatComponentTranslation("message.touhou_little_maid.spawn"));
        return true;
    }

    @Override
    public boolean isFull3D() {
        return true;
    }
}
