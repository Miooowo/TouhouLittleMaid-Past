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

public class EntityAIMaidCocoa extends EntityAIBase {

    private static final int RANGE = 8;
    private static final int COCOA_DAMAGE = 3;
    private static final int HARVEST = 0;
    private static final int PLANT = 1;

    private final EntityMaid maid;
    private int targetX;
    private int targetY;
    private int targetZ;
    private int plantSide;
    private int action;
    private int timeout;

    public EntityAIMaidCocoa(EntityMaid maid) {
        this.maid = maid;
        setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (maid.getTask() != MaidTask.COCOA || maid.isSitting() || maid.worldObj.isRemote) {
            return false;
        }
        if (maid.getRNG().nextInt(12) != 0) {
            return false;
        }
        return findWork();
    }

    @Override
    public boolean continueExecuting() {
        return timeout > 0 && maid.getTask() == MaidTask.COCOA && !maid.isSitting();
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
        if (maid.getDistanceSq(targetX + 0.5D, targetY, targetZ + 0.5D) <= 6.25D) {
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
                for (int dy = -1; dy <= 3; dy++) {
                    int x = originX + dx;
                    int y = originY + dy;
                    int z = originZ + dz;
                    int dist = dx * dx + dy * dy + dz * dz;
                    if (dist >= best) {
                        continue;
                    }
                    if (isMatureCocoa(world, x, y, z)) {
                        best = dist;
                        targetX = x;
                        targetY = y;
                        targetZ = z;
                        action = HARVEST;
                        found = true;
                        continue;
                    }
                    int side = findPlantSide(world, x, y, z);
                    if (side >= 0 && MaidInventories.findItem(maid, Items.dye, COCOA_DAMAGE) >= 0) {
                        best = dist;
                        targetX = x;
                        targetY = y;
                        targetZ = z;
                        plantSide = side;
                        action = PLANT;
                        found = true;
                    }
                }
            }
        }
        return found;
    }

    private void perform() {
        World world = maid.worldObj;
        if (action == HARVEST) {
            if (!isMatureCocoa(world, targetX, targetY, targetZ)) {
                return;
            }
            int facing = world.getBlockMetadata(targetX, targetY, targetZ) & 3;
            world.func_147480_a(targetX, targetY, targetZ, true);
            MaidInventories.collectNearbyItems(maid, 2.5D);
            int slot = MaidInventories.findItem(maid, Items.dye, COCOA_DAMAGE);
            if (slot >= 0 && world.getBlock(targetX, targetY, targetZ) == Blocks.air) {
                MaidInventories.consumeOne(maid, slot);
                world.setBlock(targetX, targetY, targetZ, Blocks.cocoa, facing, 3);
            }
            return;
        }
        int slot = MaidInventories.findItem(maid, Items.dye, COCOA_DAMAGE);
        if (slot < 0 || !isJungleLog(world.getBlock(targetX, targetY, targetZ), world.getBlockMetadata(targetX, targetY, targetZ))) {
            return;
        }
        int cocoaX = targetX;
        int cocoaZ = targetZ;
        if (plantSide == 2) {
            cocoaZ--;
        } else if (plantSide == 3) {
            cocoaZ++;
        } else if (plantSide == 4) {
            cocoaX--;
        } else if (plantSide == 5) {
            cocoaX++;
        }
        if (world.getBlock(cocoaX, targetY, cocoaZ) != Blocks.air) {
            return;
        }
        int meta = Blocks.cocoa.onBlockPlaced(world, cocoaX, targetY, cocoaZ, plantSide, 0.5F, 0.5F, 0.5F, 0);
        MaidInventories.consumeOne(maid, slot);
        world.setBlock(cocoaX, targetY, cocoaZ, Blocks.cocoa, meta, 3);
    }

    private static boolean isMatureCocoa(World world, int x, int y, int z) {
        return world.getBlock(x, y, z) == Blocks.cocoa && (world.getBlockMetadata(x, y, z) >> 2) >= 2;
    }

    private static int findPlantSide(World world, int x, int y, int z) {
        if (!isJungleLog(world.getBlock(x, y, z), world.getBlockMetadata(x, y, z))) {
            return -1;
        }
        if (world.getBlock(x, y, z - 1) == Blocks.air) {
            return 2;
        }
        if (world.getBlock(x, y, z + 1) == Blocks.air) {
            return 3;
        }
        if (world.getBlock(x - 1, y, z) == Blocks.air) {
            return 4;
        }
        if (world.getBlock(x + 1, y, z) == Blocks.air) {
            return 5;
        }
        return -1;
    }

    private static boolean isJungleLog(Block block, int meta) {
        return block == Blocks.log && (meta & 3) == 3;
    }
}
