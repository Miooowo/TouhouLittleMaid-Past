package com.github.tartaricacid.touhoulittlemaid.entity.ai;

import java.util.List;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.task.MaidTask;
import com.github.tartaricacid.touhoulittlemaid.inventory.MaidInventories;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class EntityAIMaidFeedAnimal extends EntityAIBase {

    private final EntityMaid maid;
    private EntityAnimal target;
    private int foodSlot = -1;
    private int timeout;

    public EntityAIMaidFeedAnimal(EntityMaid maid) {
        this.maid = maid;
        setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (maid.getTask() != MaidTask.FEED_ANIMAL || maid.isSitting() || maid.worldObj.isRemote) {
            return false;
        }
        if (maid.getRNG().nextInt(16) != 0) {
            return false;
        }
        return findPair();
    }

    @Override
    public boolean continueExecuting() {
        return timeout > 0
                && target != null
                && target.isEntityAlive()
                && maid.getTask() == MaidTask.FEED_ANIMAL
                && !maid.isSitting();
    }

    @Override
    public void startExecuting() {
        timeout = 160;
        maid.getNavigator().tryMoveToEntityLiving(target, 1.0D);
    }

    @Override
    public void resetTask() {
        target = null;
        foodSlot = -1;
        maid.getNavigator().clearPathEntity();
    }

    @Override
    public void updateTask() {
        timeout--;
        if (target == null) {
            return;
        }
        maid.getLookHelper().setLookPositionWithEntity(target, 10.0F, 40.0F);
        if (maid.getDistanceSqToEntity(target) <= 4.0D) {
            feed();
            timeout = 0;
        } else if (maid.getNavigator().noPath()) {
            maid.getNavigator().tryMoveToEntityLiving(target, 1.0D);
        }
    }

    @SuppressWarnings("unchecked")
    private boolean findPair() {
        List list = maid.worldObj.getEntitiesWithinAABB(
                EntityAnimal.class, maid.boundingBox.expand(8.0D, 3.0D, 8.0D));
        EntityAnimal closest = null;
        int slot = -1;
        double best = Double.MAX_VALUE;
        for (Object obj : list) {
            EntityAnimal animal = (EntityAnimal) obj;
            if (animal.getGrowingAge() != 0 || animal.isInLove()) {
                continue;
            }
            int food = findFood(animal);
            if (food < 0) {
                continue;
            }
            double dist = maid.getDistanceSqToEntity(animal);
            if (dist < best) {
                best = dist;
                closest = animal;
                slot = food;
            }
        }
        if (closest == null) {
            return false;
        }
        target = closest;
        foodSlot = slot;
        return true;
    }

    private int findFood(EntityAnimal animal) {
        for (int i = 0; i < maid.getInventory().getSizeInventory(); i++) {
            ItemStack stack = maid.getInventory().getStackInSlot(i);
            if (stack != null && animal.isBreedingItem(stack)) {
                return i;
            }
        }
        return -1;
    }

    private void feed() {
        if (foodSlot < 0 || target.getGrowingAge() != 0 || target.isInLove()) {
            return;
        }
        ItemStack food = maid.getInventory().getStackInSlot(foodSlot);
        if (food == null || !target.isBreedingItem(food)) {
            return;
        }
        MaidInventories.consumeOne(maid, foodSlot);
        EntityLivingBase owner = maid.getOwner();
        if (owner instanceof EntityPlayer) {
            target.func_146082_f((EntityPlayer) owner);
        } else {
            target.func_146082_f(null);
        }
        maid.worldObj.setEntityState(target, (byte) 18);
    }
}
