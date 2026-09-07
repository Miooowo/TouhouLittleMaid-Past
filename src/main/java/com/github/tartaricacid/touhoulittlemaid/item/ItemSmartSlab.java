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

public class ItemSmartSlab extends Item {

    public enum Type {
        INIT,
        EMPTY,
        HAS_MAID
    }

    private final Type type;

    public ItemSmartSlab(String name, Type type) {
        this.type = type;
        setUnlocalizedName(name);
        setTextureName(TouhouLittleMaid.MOD_ID + ":" + name);
        setCreativeTab(MaidCreativeTab.TAB);
        setMaxStackSize(1);
    }

    public Type getType() {
        return type;
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
        if (type != Type.INIT || !Config.goheiCanSpawnMaid || player == null) {
            return false;
        }
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
        player.addChatMessage(new ChatComponentTranslation("message.touhou_little_maid.spawn"));
        return true;
    }
}
