package com.github.tartaricacid.touhoulittlemaid.entity.ai;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.task.MaidTask;
import com.github.tartaricacid.touhoulittlemaid.inventory.MaidInventories;

import net.minecraft.block.Block;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAIMaidSugarCane extends EntityAIBase {

    private static final int RANGE = 8;
    private static final int HARVEST = 0;
    private static final int PLANT = 1;

    private final EntityMaid maid;
    private int targetX;
    private int targetY;
    private int targetZ;
    private int action;
    private int timeout;

    public EntityAIMaidSugarCane(EntityMaid maid) {
        this.maid = maid;
        setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (maid.getTask() != MaidTask.SUGAR_CANE || maid.isSitting() || maid.worldObj.isRemote) {
            return false;
        }
        if (maid.getRNG().nextInt(12) != 0) {
            return false;
        }
        return findWork();
    }

    @Override
    public boolean continueExecuting() {
        return timeout > 0 && maid.getTask() == MaidTask.SUGAR_CANE && !maid.isSitting();
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
            perform();
            timeout = 0;
        } else if (maid.getNavigator().noPath()) {
            maid.getNavigator().tryMoveToXYZ(targetX + 0.5D, targetY, targetZ + 0.5D, 1.0D);
        }
    }

    private boolean findWork() {
        int originX = MathHelper.floor_double(maid.posX);
        int originY = MathHelper.floor_double(maid.posY);
        int originZ = MathHelper.floor_double(maid.posZ);
        World world = maid.worldObj;
        int best = Integer.MAX_VALUE;
        boolean found = false;
        for (int dx = -RANGE; dx <= RANGE; dx++) {
            for (int dz = -RANGE; dz <= RANGE; dz++) {
                for (int dy = 3; dy >= -1; dy--) {
                    int x = originX + dx;
                    int y = originY + dy;
                    int z = originZ + dz;
                    int dist = dx * dx + dy * dy + dz * dz;
                    if (dist >= best) {
                        continue;
                    }
                    int foundAction = classify(world, x, y, z);
                    if (foundAction >= 0) {
                        best = dist;
                        targetX = x;
                        targetY = y;
                        targetZ = z;
                        action = foundAction;
                        found = true;
                    }
                }
            }
        }
        return found;
    }

    private int classify(World world, int x, int y, int z) {
        if (world.getBlock(x, y, z) == Blocks.reeds && world.getBlock(x, y - 1, z) == Blocks.reeds) {
            return HARVEST;
        }
        if (world.getBlock(x, y, z) == Blocks.air
                && canSustainCane(world.getBlock(x, y - 1, z))
                && hasAdjacentWater(world, x, y - 1, z)
                && MaidInventories.findItem(maid, Items.reeds) >= 0) {
            return PLANT;
        }
        return -1;
    }

    private void perform() {
        World world = maid.worldObj;
        if (action == HARVEST) {
            if (world.getBlock(targetX, targetY, targetZ) != Blocks.reeds
                    || world.getBlock(targetX, targetY - 1, targetZ) != Blocks.reeds) {
                return;
            }
            world.func_147480_a(targetX, targetY, targetZ, true);
            MaidInventories.collectNearbyItems(maid, 2.5D);
            return;
        }
        if (world.getBlock(targetX, targetY, targetZ) != Blocks.air
                || !canSustainCane(world.getBlock(targetX, targetY - 1, targetZ))
                || !hasAdjacentWater(world, targetX, targetY - 1, targetZ)) {
            return;
        }
        int slot = MaidInventories.findItem(maid, Items.reeds);
        if (slot < 0) {
            return;
        }
        MaidInventories.consumeOne(maid, slot);
        world.setBlock(targetX, targetY, targetZ, Blocks.reeds);
    }

    private static boolean canSustainCane(Block block) {
        return block == Blocks.dirt
                || block == Blocks.grass
                || block == Blocks.sand
                || block == Blocks.farmland;
    }

    private static boolean hasAdjacentWater(World world, int x, int y, int z) {
        return isWater(world.getBlock(x + 1, y, z))
                || isWater(world.getBlock(x - 1, y, z))
                || isWater(world.getBlock(x, y, z + 1))
                || isWater(world.getBlock(x, y, z - 1));
    }

    private static boolean isWater(Block block) {
        return block == Blocks.water || block == Blocks.flowing_water;
    }
}
