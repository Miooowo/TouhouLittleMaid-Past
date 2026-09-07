package com.github.tartaricacid.touhoulittlemaid.entity.ai;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.task.MaidTask;
import com.github.tartaricacid.touhoulittlemaid.inventory.MaidInventories;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAIMaidTorch extends EntityAIBase {

    private static final int RANGE = 8;
    private final EntityMaid maid;
    private int targetX;
    private int targetY;
    private int targetZ;
    private int timeout;

    public EntityAIMaidTorch(EntityMaid maid) {
        this.maid = maid;
        setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (maid.getTask() != MaidTask.TORCH || maid.isSitting() || maid.worldObj.isRemote) {
            return false;
        }
        if (!MaidInventories.hasTorch(maid)) {
            return false;
        }
        if (maid.getRNG().nextInt(16) != 0) {
            return false;
        }
        return findDarkSpot();
    }

    @Override
    public boolean continueExecuting() {
        return timeout > 0 && maid.getTask() == MaidTask.TORCH && !maid.isSitting();
    }

    @Override
    public void startExecuting() {
        timeout = 160;
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
            placeTorch();
            timeout = 0;
        } else if (maid.getNavigator().noPath()) {
            maid.getNavigator().tryMoveToXYZ(targetX + 0.5D, targetY, targetZ + 0.5D, 1.0D);
        }
    }

    private boolean findDarkSpot() {
        int originX = MathHelper.floor_double(maid.posX);
        int originY = MathHelper.floor_double(maid.posY);
        int originZ = MathHelper.floor_double(maid.posZ);
        World world = maid.worldObj;
        int best = Integer.MAX_VALUE;
        boolean found = false;
        for (int dx = -RANGE; dx <= RANGE; dx++) {
            for (int dz = -RANGE; dz <= RANGE; dz++) {
                for (int dy = -1; dy <= 1; dy++) {
                    int x = originX + dx;
                    int y = originY + dy;
                    int z = originZ + dz;
                    int dist = dx * dx + dy * dy + dz * dz;
                    if (dist >= best || !canPlaceTorch(world, x, y, z)) {
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

    private static boolean canPlaceTorch(World world, int x, int y, int z) {
        if (world.getBlock(x, y, z) != Blocks.air) {
            return false;
        }
        if (world.getBlockLightValue(x, y, z) >= 8) {
            return false;
        }
        return Blocks.torch.canPlaceBlockAt(world, x, y, z);
    }

    private void placeTorch() {
        if (!canPlaceTorch(maid.worldObj, targetX, targetY, targetZ) || !MaidInventories.consumeTorch(maid)) {
            return;
        }
        maid.worldObj.setBlock(targetX, targetY, targetZ, Blocks.torch, 0, 3);
        maid.worldObj.playSoundEffect(
                targetX + 0.5D, targetY + 0.5D, targetZ + 0.5D, "step.wood", 1.0F, 1.0F);
    }
}
