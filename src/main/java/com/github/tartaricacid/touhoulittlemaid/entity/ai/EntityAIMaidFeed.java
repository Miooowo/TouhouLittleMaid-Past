package com.github.tartaricacid.touhoulittlemaid.entity.ai;

import java.util.Collection;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.task.MaidTask;
import com.github.tartaricacid.touhoulittlemaid.inventory.MaidInventories;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class EntityAIMaidFeed extends EntityAIBase {

    private static final int HIGH = 2;
    private static final int LOW = 1;
    private static final int LOWEST = 0;

    private final EntityMaid maid;
    private EntityPlayer owner;
    private int foodSlot = -1;
    private int timeout;

    public EntityAIMaidFeed(EntityMaid maid) {
        this.maid = maid;
        setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (maid.getTask() != MaidTask.FEED || maid.isSitting() || maid.worldObj.isRemote) {
            return false;
        }
        EntityLivingBase living = maid.getOwner();
        if (!(living instanceof EntityPlayer) || !living.isEntityAlive()) {
            return false;
        }
        owner = (EntityPlayer) living;
        foodSlot = findBestFood(owner);
        return foodSlot >= 0;
    }

    @Override
    public boolean continueExecuting() {
        return timeout > 0
                && owner != null
                && owner.isEntityAlive()
                && maid.getTask() == MaidTask.FEED
                && !maid.isSitting();
    }

    @Override
    public void startExecuting() {
        timeout = 160;
        maid.getNavigator().tryMoveToEntityLiving(owner, 1.0D);
    }

    @Override
    public void resetTask() {
        owner = null;
        foodSlot = -1;
        maid.getNavigator().clearPathEntity();
    }

    @Override
    public void updateTask() {
        timeout--;
        if (owner == null) {
            return;
        }
        maid.getLookHelper().setLookPositionWithEntity(owner, 10.0F, 40.0F);
        if (maid.getDistanceSqToEntity(owner) <= 4.0D) {
            feed();
            timeout = 0;
        } else if (maid.getNavigator().noPath()) {
            maid.getNavigator().tryMoveToEntityLiving(owner, 1.0D);
        }
    }

    private int findBestFood(EntityPlayer player) {
        int bestSlot = -1;
        int bestPriority = -1;
        boolean dying = player.getHealth() * 2.0F < player.getMaxHealth();
        for (int i = 0; i < maid.getInventory().getSizeInventory(); i++) {
            ItemStack stack = maid.getInventory().getStackInSlot(i);
            if (stack == null) {
                continue;
            }
            int priority = priorityOf(stack, player);
            if (priority == LOWEST && !dying) {
                continue;
            }
            if (priority > bestPriority) {
                bestPriority = priority;
                bestSlot = i;
            }
        }
        return bestSlot;
    }

    private int priorityOf(ItemStack stack, EntityPlayer player) {
        Item item = stack.getItem();
        if (item == Items.milk_bucket && hasLingeringHarm(player)) {
            return HIGH;
        }
        if (item == Items.golden_apple) {
            return player.getHealth() * 2.0F < player.getMaxHealth() ? HIGH : LOWEST;
        }
        if (!(item instanceof ItemFood) || isUnsafeFood(stack)) {
            return -1;
        }
        if (!player.getFoodStats().needFood()) {
            return LOWEST;
        }
        int heal = ((ItemFood) item).func_150905_g(stack);
        int hunger = 20 - player.getFoodStats().getFoodLevel();
        return heal >= hunger ? HIGH : LOW;
    }

    private void feed() {
        if (foodSlot < 0 || owner == null) {
            return;
        }
        ItemStack stack = maid.getInventory().getStackInSlot(foodSlot);
        if (stack == null || priorityOf(stack, owner) < 0) {
            return;
        }
        if (stack.getItem() == Items.milk_bucket) {
            owner.curePotionEffects(stack);
            MaidInventories.consumeOne(maid, foodSlot);
            MaidInventories.insert(maid, new ItemStack(Items.bucket));
            maid.worldObj.playSoundAtEntity(owner, "random.drink", 0.5F, 1.0F);
            return;
        }
        ItemStack taken = MaidInventories.consumeOne(maid, foodSlot);
        if (taken == null) {
            return;
        }
        ItemStack leftover = taken.getItem().onEaten(taken, maid.worldObj, owner);
        if (leftover != null && leftover.stackSize > 0) {
            MaidInventories.insert(maid, leftover);
        }
    }

    private static boolean hasLingeringHarm(EntityPlayer player) {
        Collection effects = player.getActivePotionEffects();
        for (Object obj : effects) {
            PotionEffect effect = (PotionEffect) obj;
            Potion potion = Potion.potionTypes[effect.getPotionID()];
            if (potion != null && potion.isBadEffect() && effect.getDuration() > 60) {
                return true;
            }
        }
        return false;
    }

    private static boolean isUnsafeFood(ItemStack stack) {
        Item item = stack.getItem();
        if (item == Items.rotten_flesh
                || item == Items.spider_eye
                || item == Items.fermented_spider_eye
                || item == Items.poisonous_potato
                || item == Items.chicken) {
            return true;
        }
        return item == Items.fish && stack.getItemDamage() == 3;
    }
}
