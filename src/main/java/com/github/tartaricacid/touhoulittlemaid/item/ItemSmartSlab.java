package com.github.tartaricacid.touhoulittlemaid.item;

import java.util.List;

import com.github.tartaricacid.touhoulittlemaid.Config;
import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.init.MaidCreativeTab;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemSmartSlab extends Item {

    public static final String MAID_INFO = "MaidInfo";

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

    public static boolean hasMaidData(ItemStack stack) {
        return stack != null
                && stack.stackTagCompound != null
                && stack.stackTagCompound.hasKey(MAID_INFO);
    }

    public static NBTTagCompound getMaidData(ItemStack stack) {
        return hasMaidData(stack) ? stack.stackTagCompound.getCompoundTag(MAID_INFO) : null;
    }

    public static void storeMaid(ItemStack emptySlab, EntityPlayer player, EntityMaid maid) {
        if (player.worldObj.isRemote) {
            return;
        }
        ItemStack filled = new ItemStack(InitItems.SMART_SLAB_HAS_MAID);
        NBTTagCompound root = new NBTTagCompound();
        NBTTagCompound data = new NBTTagCompound();
        maid.writeToNBT(data);
        root.setTag(MAID_INFO, data);
        filled.setTagCompound(root);
        int slot = player.inventory.currentItem;
        if (!player.capabilities.isCreativeMode) {
            emptySlab.stackSize--;
            if (emptySlab.stackSize <= 0) {
                player.inventory.setInventorySlotContents(slot, filled);
            } else if (!player.inventory.addItemStackToInventory(filled)) {
                player.dropPlayerItemWithRandomChoice(filled, false);
            }
        } else {
            player.inventory.setInventorySlotContents(slot, filled);
        }
        maid.setDead();
        player.addChatMessage(new ChatComponentTranslation("message.touhou_little_maid.slab.store"));
    }

    private static ItemStack toEmptySlab() {
        return new ItemStack(InitItems.SMART_SLAB_EMPTY);
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
        if (player == null) {
            return false;
        }
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        double spawnX = x + 0.5D + dir.offsetX;
        double spawnY = y + (side == 1 ? 1.0D : 0.0D);
        double spawnZ = z + 0.5D + dir.offsetZ;
        if (type == Type.INIT) {
            if (!Config.goheiCanSpawnMaid) {
                return false;
            }
            if (world.isRemote) {
                return true;
            }
            EntityMaid.spawnTamed(world, player, spawnX, spawnY, spawnZ);
            replaceWithEmpty(stack, player);
            player.addChatMessage(new ChatComponentTranslation("message.touhou_little_maid.spawn"));
            return true;
        }
        if (type == Type.HAS_MAID) {
            if (world.isRemote) {
                return true;
            }
            if (!hasMaidData(stack)) {
                return false;
            }
            NBTTagCompound data = getMaidData(stack);
            String owner = data.getString("OwnerUUID");
            if (owner.length() > 0 && !owner.equals(player.getUniqueID().toString())) {
                player.addChatMessage(
                        new ChatComponentTranslation("message.touhou_little_maid.slab.not_yours"));
                return true;
            }
            EntityMaid maid = new EntityMaid(world);
            maid.readFromNBT(data);
            maid.setLocationAndAngles(spawnX, spawnY, spawnZ, player.rotationYaw, 0.0F);
            maid.setSitting(false);
            world.spawnEntityInWorld(maid);
            replaceWithEmpty(stack, player);
            player.addChatMessage(new ChatComponentTranslation("message.touhou_little_maid.slab.release"));
            return true;
        }
        return false;
    }

    private static void replaceWithEmpty(ItemStack stack, EntityPlayer player) {
        player.inventory.setInventorySlotContents(player.inventory.currentItem, toEmptySlab());
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        tooltip.add(StatCollector.translateToLocal("tooltip.touhou_little_maid.smart_slab.desc"));
        if (type == Type.HAS_MAID && hasMaidData(stack)) {
            NBTTagCompound data = getMaidData(stack);
            if (data.hasKey("CustomName") && data.getString("CustomName").length() > 0) {
                tooltip.add(StatCollector.translateToLocalFormatted(
                        "tooltip.touhou_little_maid.smart_slab.name", data.getString("CustomName")));
            }
        }
    }
}
