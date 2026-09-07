package com.github.tartaricacid.touhoulittlemaid.entity.ai;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.task.MaidTask;
import com.github.tartaricacid.touhoulittlemaid.inventory.MaidInventories;

import net.minecraft.block.BlockSnow;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAIMaidSnow extends EntityAIBase {

    private static final int RANGE = 8;
    private final EntityMaid maid;
    private int targetX;
    private int targetY;
    private int targetZ;
    private int timeout;

    public EntityAIMaidSnow(EntityMaid maid) {
        this.maid = maid;
        setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (maid.getTask() != MaidTask.SNOW || maid.isSitting() || maid.worldObj.isRemote) {
            return false;
        }
        if (maid.getRNG().nextInt(8) != 0) {
            return false;
        }
        return findSnow();
    }

    @Override
    public boolean continueExecuting() {
        return timeout > 0 && maid.getTask() == MaidTask.SNOW && !maid.isSitting();
    }

    @Override
    public void startExecuting() {
        timeout = 140;
        maid.getNavigator().tryMoveToXYZ(targetX + 0.5D, targetY, targetZ + 0.5D, 1.0D);
    }

    @Override
    public void resetTask() {
        maid.getNavigator().clearPathEntity();
    }

    @Override
    public void updateTask() {
        timeout--;
        maid.getLookHelper().setLookPosition(targetX + 0.5D, targetY + 0.5D, targetZ + 0.5D, 10.0F, 40.0F);
        if (maid.getDistanceSq(targetX + 0.5D, targetY, targetZ + 0.5D) <= 4.0D) {
            shovel();
            timeout = 0;
        } else if (maid.getNavigator().noPath()) {
            maid.getNavigator().tryMoveToXYZ(targetX + 0.5D, targetY, targetZ + 0.5D, 1.0D);
        }
    }

    private boolean findSnow() {
        int originX = MathHelper.floor_double(maid.posX);
        int originY = MathHelper.floor_double(maid.posY);
        int originZ = MathHelper.floor_double(maid.posZ);
        World world = maid.worldObj;
        int best = Integer.MAX_VALUE;
        boolean found = false;
        for (int dx = -RANGE; dx <= RANGE; dx++) {
            for (int dz = -RANGE; dz <= RANGE; dz++) {
                for (int dy = -1; dy <= 2; dy++) {
                    int x = originX + dx;
                    int y = originY + dy;
                    int z = originZ + dz;
                    int dist = dx * dx + dy * dy + dz * dz;
                    if (dist >= best || !(world.getBlock(x, y, z) instanceof BlockSnow)) {
                        continue;
                    }
                    best = dist;
                    targetX = x;
                    targetY = y;
                    targetZ = z;
                    found = true;
                }
            }
        }
        return found;
    }

    private void shovel() {
        if (!(maid.worldObj.getBlock(targetX, targetY, targetZ) instanceof BlockSnow)) {
            return;
        }
        int shovel = MaidInventories.findShovel(maid);
        // 1.20.1: shovel drops snowballs and takes damage; bare hands remove snow with no drop.
        maid.worldObj.func_147480_a(targetX, targetY, targetZ, shovel >= 0);
        if (shovel >= 0) {
            MaidInventories.damageSlot(maid, shovel, 1);
            MaidInventories.collectNearbyItems(maid, 2.0D);
        }
    }
}
