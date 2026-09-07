package com.github.tartaricacid.touhoulittlemaid.tileentity;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public class TileEntityTombstone extends TileEntity {

    public static final int SIZE = EntityMaid.INVENTORY_SIZE;
    private static final String NBT_OWNER = "OwnerId";
    private static final String NBT_NAME = "MaidName";
    private static final String NBT_ITEMS = "TombstoneItems";
    private static final String NBT_MAID = "MaidData";

    private final InventoryBasic inventory = new InventoryBasic("Tombstone", false, SIZE);
    private String ownerId = "";
    private String maidName = "";
    private NBTTagCompound maidData;

    public void captureMaid(EntityMaid maid) {
        ownerId = maid.func_152113_b() != null ? maid.func_152113_b() : "";
        maidName = maid.getCommandSenderName();
        for (int i = 0; i < SIZE && i < maid.getInventory().getSizeInventory(); i++) {
            ItemStack stack = maid.getInventory().getStackInSlot(i);
            inventory.setInventorySlotContents(i, stack != null ? stack.copy() : null);
            maid.getInventory().setInventorySlotContents(i, null);
        }
        NBTTagCompound tag = new NBTTagCompound();
        maid.writeToNBT(tag);
        tag.removeTag("Pos");
        tag.removeTag("Motion");
        maidData = tag;
        markDirty();
    }

    public boolean isOwner(EntityPlayer player) {
        return player != null && ownerId != null && ownerId.equals(player.getUniqueID().toString());
    }

    public boolean hasMaidData() {
        return maidData != null;
    }

    public boolean isInventoryEmpty() {
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            if (inventory.getStackInSlot(i) != null) {
                return false;
            }
        }
        return true;
    }

    public boolean giveItemsTo(EntityPlayer player, boolean force) {
        boolean blocked = false;
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack == null) {
                continue;
            }
            if (player.inventory.addItemStackToInventory(stack)) {
                inventory.setInventorySlotContents(i, null);
            } else if (force) {
                player.dropPlayerItemWithRandomChoice(stack, false);
                inventory.setInventorySlotContents(i, null);
            } else {
                blocked = true;
            }
        }
        markDirty();
        return !blocked;
    }

    public boolean reviveMaid(net.minecraft.world.World world, EntityPlayer player, double x, double y, double z) {
        if (maidData == null) {
            return false;
        }
        EntityMaid maid = new EntityMaid(world);
        maid.readFromNBT(maidData);
        maid.setLocationAndAngles(x, y, z, player.rotationYaw, 0.0F);
        maid.setHealth(Math.max(1.0F, maid.getMaxHealth()));
        maid.setSitting(false);
        if (!ownerId.isEmpty()) {
            maid.func_152115_b(ownerId);
            maid.setTamed(true);
        }
        world.spawnEntityInWorld(maid);
        maidData = null;
        markDirty();
        return true;
    }

    public String getMaidName() {
        return maidName;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setString(NBT_OWNER, ownerId);
        tag.setString(NBT_NAME, maidName);
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack != null) {
                NBTTagCompound slot = new NBTTagCompound();
                slot.setByte("Slot", (byte) i);
                stack.writeToNBT(slot);
                list.appendTag(slot);
            }
        }
        tag.setTag(NBT_ITEMS, list);
        if (maidData != null) {
            tag.setTag(NBT_MAID, maidData);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        ownerId = tag.getString(NBT_OWNER);
        maidName = tag.getString(NBT_NAME);
        NBTTagList list = tag.getTagList(NBT_ITEMS, 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound slot = list.getCompoundTagAt(i);
            int index = slot.getByte("Slot") & 255;
            if (index < inventory.getSizeInventory()) {
                inventory.setInventorySlotContents(index, ItemStack.loadItemStackFromNBT(slot));
            }
        }
        maidData = tag.hasKey(NBT_MAID) ? tag.getCompoundTag(NBT_MAID) : null;
    }
}
