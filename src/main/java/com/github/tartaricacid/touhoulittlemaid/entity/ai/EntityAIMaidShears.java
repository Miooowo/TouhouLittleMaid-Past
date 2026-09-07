package com.github.tartaricacid.touhoulittlemaid.entity.ai;

import java.util.ArrayList;
import java.util.List;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.task.MaidTask;
import com.github.tartaricacid.touhoulittlemaid.inventory.MaidInventories;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IShearable;

public class EntityAIMaidShears extends EntityAIBase {

    private final EntityMaid maid;
    private EntityLivingBase target;
    private int timeout;

    public EntityAIMaidShears(EntityMaid maid) {
        this.maid = maid;
        setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (maid.getTask() != MaidTask.SHEARS || maid.isSitting() || maid.worldObj.isRemote) {
            return false;
        }
        if (MaidInventories.findShears(maid) < 0) {
            return false;
        }
        if (maid.getRNG().nextInt(10) != 0) {
            return false;
        }
        target = findSheep();
        return target != null;
    }

    @Override
    public boolean continueExecuting() {
        return timeout > 0
                && target != null
                && target.isEntityAlive()
                && maid.getTask() == MaidTask.SHEARS
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
            shear();
            timeout = 0;
        } else if (maid.getNavigator().noPath()) {
            maid.getNavigator().tryMoveToEntityLiving(target, 1.0D);
        }
    }

    @SuppressWarnings("unchecked")
    private EntityLivingBase findSheep() {
        List list = maid.worldObj.getEntitiesWithinAABB(
                EntitySheep.class, maid.boundingBox.expand(8.0D, 3.0D, 8.0D));
        EntityLivingBase closest = null;
        double best = Double.MAX_VALUE;
        for (Object obj : list) {
            EntitySheep sheep = (EntitySheep) obj;
            if (sheep.isChild() || !(sheep instanceof IShearable)) {
                continue;
            }
            IShearable shearable = (IShearable) sheep;
            if (!shearable.isShearable(null, maid.worldObj, (int) sheep.posX, (int) sheep.posY, (int) sheep.posZ)) {
                continue;
            }
            double dist = maid.getDistanceSqToEntity(sheep);
            if (dist < best) {
                best = dist;
                closest = sheep;
            }
        }
        return closest;
    }

    private void shear() {
        int slot = MaidInventories.findShears(maid);
        if (slot < 0 || !(target instanceof IShearable)) {
            return;
        }
        ItemStack shears = maid.getInventory().getStackInSlot(slot);
        IShearable shearable = (IShearable) target;
        if (!shearable.isShearable(shears, maid.worldObj, (int) target.posX, (int) target.posY, (int) target.posZ)) {
            return;
        }
        ArrayList<ItemStack> drops = shearable.onSheared(
                shears, maid.worldObj, (int) target.posX, (int) target.posY, (int) target.posZ, 0);
        if (drops != null) {
            for (ItemStack drop : drops) {
                MaidInventories.insert(maid, drop);
            }
        }
        if (shears != null) {
            shears.damageItem(1, maid);
            if (shears.stackSize <= 0) {
                maid.getInventory().setInventorySlotContents(slot, null);
            }
        }
        maid.worldObj.playSoundAtEntity(target, "mob.sheep.shear", 1.0F, 1.0F);
    }
}
