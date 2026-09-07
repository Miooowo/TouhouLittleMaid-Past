package com.github.tartaricacid.touhoulittlemaid.inventory;

import java.util.List;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemShears;

public final class MaidInventories {

    private MaidInventories() {}

    public static int findItem(EntityMaid maid, Item item) {
        for (int i = 0; i < maid.getInventory().getSizeInventory(); i++) {
            ItemStack stack = maid.getInventory().getStackInSlot(i);
            if (stack != null && stack.getItem() == item && stack.stackSize > 0) {
                return i;
            }
        }
        return -1;
    }

    public static int findHoe(EntityMaid maid) {
        for (int i = 0; i < maid.getInventory().getSizeInventory(); i++) {
            ItemStack stack = maid.getInventory().getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof ItemHoe) {
                return i;
            }
        }
        return -1;
    }

    public static int findShears(EntityMaid maid) {
        for (int i = 0; i < maid.getInventory().getSizeInventory(); i++) {
            ItemStack stack = maid.getInventory().getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof ItemShears) {
                return i;
            }
        }
        return -1;
    }

    public static Item torchItem() {
        return Item.getItemFromBlock(Blocks.torch);
    }

    public static boolean hasTorch(EntityMaid maid) {
        if (findItem(maid, torchItem()) >= 0) {
            return true;
        }
        EntityPlayer owner = ownerNearby(maid, 8.0D);
        return owner != null && findPlayerItem(owner, torchItem()) >= 0;
    }

    public static boolean consumeTorch(EntityMaid maid) {
        int maidSlot = findItem(maid, torchItem());
        if (maidSlot >= 0) {
            consumeOne(maid, maidSlot);
            return true;
        }
        EntityPlayer owner = ownerNearby(maid, 8.0D);
        if (owner == null) {
            return false;
        }
        int ownerSlot = findPlayerItem(owner, torchItem());
        if (ownerSlot < 0) {
            return false;
        }
        ItemStack stack = owner.inventory.mainInventory[ownerSlot];
        if (stack == null) {
            return false;
        }
        stack.splitStack(1);
        if (stack.stackSize <= 0) {
            owner.inventory.mainInventory[ownerSlot] = null;
        }
        return true;
    }

    private static EntityPlayer ownerNearby(EntityMaid maid, double range) {
        EntityLivingBase owner = maid.getOwner();
        if (!(owner instanceof EntityPlayer)) {
            return null;
        }
        EntityPlayer player = (EntityPlayer) owner;
        if (player.getDistanceSqToEntity(maid) > range * range) {
            return null;
        }
        return player;
    }

    private static int findPlayerItem(EntityPlayer player, Item item) {
        for (int i = 0; i < player.inventory.mainInventory.length; i++) {
            ItemStack stack = player.inventory.mainInventory[i];
            if (stack != null && stack.getItem() == item && stack.stackSize > 0) {
                return i;
            }
        }
        return -1;
    }

    public static ItemStack consumeOne(EntityMaid maid, int slot) {
        ItemStack stack = maid.getInventory().getStackInSlot(slot);
        if (stack == null) {
            return null;
        }
        ItemStack taken = stack.splitStack(1);
        if (stack.stackSize <= 0) {
            maid.getInventory().setInventorySlotContents(slot, null);
        }
        return taken;
    }

    public static boolean insert(EntityMaid maid, ItemStack stack) {
        if (stack == null) {
            return true;
        }
        ItemStack leftover = stack.copy();
        leftover = mergeInto(maid, leftover);
        if (leftover == null || leftover.stackSize <= 0) {
            return true;
        }
        maid.entityDropItem(leftover, 0.0F);
        return false;
    }

    public static void collectNearbyItems(EntityMaid maid, double range) {
        if (maid.worldObj.isRemote) {
            return;
        }
        List list = maid.worldObj.getEntitiesWithinAABB(
                EntityItem.class, maid.boundingBox.expand(range, range, range));
        for (Object obj : list) {
            EntityItem entityItem = (EntityItem) obj;
            ItemStack stack = entityItem.getEntityItem();
            if (stack == null) {
                continue;
            }
            ItemStack leftover = mergeInto(maid, stack.copy());
            if (leftover == null || leftover.stackSize <= 0) {
                entityItem.setDead();
            } else {
                entityItem.setEntityItemStack(leftover);
            }
        }
    }

    private static ItemStack mergeInto(EntityMaid maid, ItemStack stack) {
        if (stack == null) {
            return null;
        }
        for (int i = 0; i < maid.getInventory().getSizeInventory() && stack.stackSize > 0; i++) {
            ItemStack slot = maid.getInventory().getStackInSlot(i);
            if (slot != null && slot.getItem() == stack.getItem() && slot.getItemDamage() == stack.getItemDamage()
                    && ItemStack.areItemStackTagsEqual(slot, stack)) {
                int space = slot.getMaxStackSize() - slot.stackSize;
                if (space > 0) {
                    int move = Math.min(space, stack.stackSize);
                    slot.stackSize += move;
                    stack.stackSize -= move;
                }
            }
        }
        for (int i = 0; i < maid.getInventory().getSizeInventory() && stack.stackSize > 0; i++) {
            if (maid.getInventory().getStackInSlot(i) == null) {
                maid.getInventory().setInventorySlotContents(i, stack);
                return null;
            }
        }
        return stack.stackSize > 0 ? stack : null;
    }
}
