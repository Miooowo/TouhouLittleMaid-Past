package com.github.tartaricacid.touhoulittlemaid.entity.ai;

import java.util.List;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.task.MaidTask;
import com.github.tartaricacid.touhoulittlemaid.inventory.MaidInventories;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class EntityAIMaidMilk extends EntityAIBase {

    private final EntityMaid maid;
    private EntityCow target;
    private int timeout;

    public EntityAIMaidMilk(EntityMaid maid) {
        this.maid = maid;
        setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (maid.getTask() != MaidTask.MILK || maid.isSitting() || maid.worldObj.isRemote) {
            return false;
        }
        if (MaidInventories.findItem(maid, Items.bucket) < 0) {
            return false;
        }
        if (maid.getRNG().nextInt(12) != 0) {
            return false;
        }
        target = findCow();
        return target != null;
    }

    @Override
    public boolean continueExecuting() {
        return timeout > 0 && target != null && target.isEntityAlive() && maid.getTask() == MaidTask.MILK && !maid.isSitting();
    }

    @Override
    public void startExecuting() {
        timeout = 160;
        maid.getNavigator().tryMoveToEntityLiving(target, 1.0D);
    }

    @Override
    public void resetTask() {
        target = null;
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
            milk();
            timeout = 0;
        } else if (maid.getNavigator().noPath()) {
            maid.getNavigator().tryMoveToEntityLiving(target, 1.0D);
        }
    }

    @SuppressWarnings("unchecked")
    private EntityCow findCow() {
        List list = maid.worldObj.getEntitiesWithinAABB(
                EntityCow.class, maid.boundingBox.expand(8.0D, 3.0D, 8.0D));
        EntityCow closest = null;
        double best = Double.MAX_VALUE;
        for (Object obj : list) {
            EntityCow cow = (EntityCow) obj;
            if (cow.isChild()) {
                continue;
            }
            double dist = maid.getDistanceSqToEntity(cow);
            if (dist < best) {
                best = dist;
                closest = cow;
            }
        }
        return closest;
    }

    private void milk() {
        int slot = MaidInventories.findItem(maid, Items.bucket);
        if (slot < 0) {
            return;
        }
        MaidInventories.consumeOne(maid, slot);
        MaidInventories.insert(maid, new ItemStack(Items.milk_bucket));
        maid.worldObj.playSoundAtEntity(target, "mob.cow.say", 1.0F, 1.0F);
    }
}
