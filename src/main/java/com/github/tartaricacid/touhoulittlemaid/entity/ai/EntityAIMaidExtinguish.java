package com.github.tartaricacid.touhoulittlemaid.entity.ai;

import java.util.List;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.task.MaidTask;
import com.github.tartaricacid.touhoulittlemaid.inventory.MaidInventories;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;

/**
 * 1.20.1 sprays an extinguisher agent. 1.7.10 uses a water bucket from the 15-slot inv
 * to put out fire blocks and burning mobs in the same 2×1×2 radius.
 */
public class EntityAIMaidExtinguish extends EntityAIBase {

    private final EntityMaid maid;
    private EntityLivingBase entityTarget;
    private int fireX;
    private int fireY;
    private int fireZ;
    private boolean targetingFire;
    private int timeout;

    public EntityAIMaidExtinguish(EntityMaid maid) {
        this.maid = maid;
        setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (maid.getTask() != MaidTask.EXTINGUISHING || maid.isSitting() || maid.worldObj.isRemote) {
            return false;
        }
        if (MaidInventories.findItem(maid, Items.water_bucket) < 0) {
            return false;
        }
        return findTarget();
    }

    @Override
    public boolean continueExecuting() {
        return timeout > 0
                && maid.getTask() == MaidTask.EXTINGUISHING
                && !maid.isSitting()
                && MaidInventories.findItem(maid, Items.water_bucket) >= 0;
    }

    @Override
    public void startExecuting() {
        timeout = 80;
        if (targetingFire) {
            maid.getNavigator().tryMoveToXYZ(fireX + 0.5D, fireY, fireZ + 0.5D, 1.2D);
        } else if (entityTarget != null) {
            maid.getNavigator().tryMoveToEntityLiving(entityTarget, 1.2D);
        }
    }

    @Override
    public void resetTask() {
        entityTarget = null;
        targetingFire = false;
        maid.getNavigator().clearPathEntity();
    }

    @Override
    public void updateTask() {
        timeout--;
        if (targetingFire) {
            maid.getLookHelper().setLookPosition(fireX + 0.5D, fireY + 0.5D, fireZ + 0.5D, 10.0F, 40.0F);
            if (maid.getDistanceSq(fireX + 0.5D, fireY, fireZ + 0.5D) <= 9.0D) {
                spray();
                timeout = 0;
            } else if (maid.getNavigator().noPath()) {
                maid.getNavigator().tryMoveToXYZ(fireX + 0.5D, fireY, fireZ + 0.5D, 1.2D);
            }
            return;
        }
        if (entityTarget == null || !entityTarget.isEntityAlive()) {
            timeout = 0;
            return;
        }
        maid.getLookHelper().setLookPositionWithEntity(entityTarget, 10.0F, 40.0F);
        if (maid.getDistanceSqToEntity(entityTarget) <= 9.0D) {
            spray();
            timeout = 0;
        } else if (maid.getNavigator().noPath()) {
            maid.getNavigator().tryMoveToEntityLiving(entityTarget, 1.2D);
        }
    }

    private boolean findTarget() {
        targetingFire = false;
        entityTarget = null;
        if (maid.isBurning()) {
            entityTarget = maid;
            return true;
        }
        EntityLivingBase owner = maid.getOwner();
        if (owner != null && owner.isEntityAlive() && owner.isBurning()) {
            entityTarget = owner;
            return true;
        }
        List list = maid.worldObj.getEntitiesWithinAABB(
                EntityLivingBase.class, maid.boundingBox.expand(8.0D, 3.0D, 8.0D));
        EntityLivingBase closest = null;
        double best = Double.MAX_VALUE;
        for (Object obj : list) {
            EntityLivingBase living = (EntityLivingBase) obj;
            if (!living.isBurning() || !living.isEntityAlive()) {
                continue;
            }
            double dist = maid.getDistanceSqToEntity(living);
            if (dist < best) {
                best = dist;
                closest = living;
            }
        }
        if (closest != null) {
            entityTarget = closest;
            return true;
        }
        return findFireBlock();
    }

    private boolean findFireBlock() {
        int originX = MathHelper.floor_double(maid.posX);
        int originY = MathHelper.floor_double(maid.posY);
        int originZ = MathHelper.floor_double(maid.posZ);
        int best = Integer.MAX_VALUE;
        boolean found = false;
        for (int dx = -8; dx <= 8; dx++) {
            for (int dz = -8; dz <= 8; dz++) {
                for (int dy = -1; dy <= 2; dy++) {
                    int x = originX + dx;
                    int y = originY + dy;
                    int z = originZ + dz;
                    int dist = dx * dx + dy * dy + dz * dz;
                    if (dist >= best || maid.worldObj.getBlock(x, y, z) != Blocks.fire) {
                        continue;
                    }
                    best = dist;
                    fireX = x;
                    fireY = y;
                    fireZ = z;
                    targetingFire = true;
                    found = true;
                }
            }
        }
        return found;
    }

    private void spray() {
        if (MaidInventories.findItem(maid, Items.water_bucket) < 0) {
            return;
        }
        int cx;
        int cy;
        int cz;
        if (targetingFire) {
            cx = fireX;
            cy = fireY;
            cz = fireZ;
        } else if (entityTarget != null) {
            cx = MathHelper.floor_double(entityTarget.posX);
            cy = MathHelper.floor_double(entityTarget.posY);
            cz = MathHelper.floor_double(entityTarget.posZ);
        } else {
            cx = MathHelper.floor_double(maid.posX);
            cy = MathHelper.floor_double(maid.posY);
            cz = MathHelper.floor_double(maid.posZ);
        }
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -2; dz <= 2; dz++) {
                    int x = cx + dx;
                    int y = cy + dy;
                    int z = cz + dz;
                    if (maid.worldObj.getBlock(x, y, z) == Blocks.fire) {
                        maid.worldObj.setBlockToAir(x, y, z);
                    }
                }
            }
        }
        List list = maid.worldObj.getEntitiesWithinAABB(
                EntityLivingBase.class,
                AxisAlignedBB.getBoundingBox(cx - 2, cy - 1, cz - 2, cx + 3, cy + 2, cz + 3));
        for (Object obj : list) {
            ((EntityLivingBase) obj).extinguish();
        }
        maid.extinguish();
        maid.worldObj.playSoundAtEntity(maid, "random.fizz", 1.0F, 1.0F);
    }
}
