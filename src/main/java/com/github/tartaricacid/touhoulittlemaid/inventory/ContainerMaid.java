package com.github.tartaricacid.touhoulittlemaid.inventory;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerMaid extends Container {

    public static final int MAID_SLOTS = EntityMaid.INVENTORY_SIZE;
    private final EntityMaid maid;

    public ContainerMaid(InventoryPlayer playerInv, EntityMaid maid) {
        this.maid = maid;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 5; col++) {
                addSlotToContainer(
                        new Slot(maid.getInventory(), col + row * 5, 44 + col * 18, 18 + row * 18));
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlotToContainer(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlotToContainer(new Slot(playerInv, col, 8 + col * 18, 142));
        }
    }

    public EntityMaid getMaid() {
        return maid;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return maid.isEntityAlive()
                && maid.func_152114_e(player)
                && player.getDistanceToEntity(maid) < 8.0F;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack result = null;
        Slot slot = (Slot) inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            result = stack.copy();
            if (index < MAID_SLOTS) {
                if (!mergeItemStack(stack, MAID_SLOTS, inventorySlots.size(), true)) {
                    return null;
                }
            } else if (!mergeItemStack(stack, 0, MAID_SLOTS, false)) {
                return null;
            }
            if (stack.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }
        }
        return result;
    }
}
