package com.github.tartaricacid.touhoulittlemaid.entity.ai;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.task.MaidTask;
import com.github.tartaricacid.touhoulittlemaid.inventory.MaidInventories;

import net.minecraft.block.Block;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * Harvest grown melons/pumpkins attached to a stem. 1.20.1 does not replant.
 */
public class EntityAIMaidMelon extends EntityAIBase {

    private static final int RANGE = 8;
    private final EntityMaid maid;
    private int targetX;
    private int targetY;
    private int targetZ;
    private int timeout;

    public EntityAIMaidMelon(EntityMaid maid) {
        this.maid = maid;
        setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (maid.getTask() != MaidTask.MELON || maid.isSitting() || maid.worldObj.isRemote) {
            return false;
        }
        if (maid.getRNG().nextInt(12) != 0) {
            return false;
        }
        return findFruit();
    }

    @Override
    public boolean continueExecuting() {
        return timeout > 0 && maid.getTask() == MaidTask.MELON && !maid.isSitting();
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
            harvest();
            timeout = 0;
        } else if (maid.getNavigator().noPath()) {
            maid.getNavigator().tryMoveToXYZ(targetX + 0.5D, targetY, targetZ + 0.5D, 1.0D);
        }
    }

    private boolean findFruit() {
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
                    if (dist >= best || !isStemFruit(world, x, y, z)) {
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

    private static boolean isStemFruit(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        Block stem;
        if (block == Blocks.melon_block) {
            stem = Blocks.melon_stem;
        } else if (block == Blocks.pumpkin) {
            stem = Blocks.pumpkin_stem;
        } else {
            return false;
        }
        return world.getBlock(x + 1, y, z) == stem
                || world.getBlock(x - 1, y, z) == stem
                || world.getBlock(x, y, z + 1) == stem
                || world.getBlock(x, y, z - 1) == stem;
    }

    private void harvest() {
        if (!isStemFruit(maid.worldObj, targetX, targetY, targetZ)) {
            return;
        }
        maid.worldObj.func_147480_a(targetX, targetY, targetZ, true);
        MaidInventories.collectNearbyItems(maid, 2.5D);
    }
}
