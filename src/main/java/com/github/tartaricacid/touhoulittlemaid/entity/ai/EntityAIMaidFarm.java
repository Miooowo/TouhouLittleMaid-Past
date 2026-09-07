package com.github.tartaricacid.touhoulittlemaid.entity.ai;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.task.MaidTask;
import com.github.tartaricacid.touhoulittlemaid.inventory.MaidInventories;

import net.minecraft.block.Block;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAIMaidFarm extends EntityAIBase {

    private static final int RANGE = 8;
    private static final int HARVEST = 0;
    private static final int PLANT = 1;
    private static final int TILL = 2;

    private final EntityMaid maid;
    private int targetX;
    private int targetY;
    private int targetZ;
    private int action;
    private int timeout;

    public EntityAIMaidFarm(EntityMaid maid) {
        this.maid = maid;
        setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (maid.getTask() != MaidTask.FARM || maid.isSitting() || maid.worldObj.isRemote) {
            return false;
        }
        if (maid.getRNG().nextInt(12) != 0) {
            return false;
        }
        return findWork();
    }

    @Override
    public boolean continueExecuting() {
        return timeout > 0
                && maid.getTask() == MaidTask.FARM
                && !maid.isSitting()
                && maid.isEntityAlive();
    }

    @Override
    public void startExecuting() {
        timeout = 160;
        maid.getNavigator().tryMoveToXYZ(targetX + 0.5D, targetY, targetZ + 0.5D, 1.0D);
    }

    @Override
    public void resetTask() {
        maid.getNavigator().clearPathEntity();
        timeout = 0;
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
        int bestDist = Integer.MAX_VALUE;
        boolean found = false;
        for (int dx = -RANGE; dx <= RANGE; dx++) {
            for (int dz = -RANGE; dz <= RANGE; dz++) {
                for (int dy = -1; dy <= 2; dy++) {
                    int x = originX + dx;
                    int y = originY + dy;
                    int z = originZ + dz;
                    int dist = dx * dx + dy * dy + dz * dz;
                    if (dist >= bestDist) {
                        continue;
                    }
                    int foundAction = classify(world, x, y, z);
                    if (foundAction >= 0) {
                        bestDist = dist;
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
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        if (isMatureCrop(block, meta)) {
            return HARVEST;
        }
        if (block == Blocks.air && isFarmland(world.getBlock(x, y - 1, z)) && hasAnySeed()) {
            return PLANT;
        }
        if ((block == Blocks.dirt || block == Blocks.grass)
                && world.getBlock(x, y + 1, z) == Blocks.air
                && MaidInventories.findHoe(maid) >= 0
                && hasAnySeed()) {
            return TILL;
        }
        return -1;
    }

    private void perform() {
        World world = maid.worldObj;
        if (action == HARVEST) {
            Block block = world.getBlock(targetX, targetY, targetZ);
            int meta = world.getBlockMetadata(targetX, targetY, targetZ);
            if (!isMatureCrop(block, meta)) {
                return;
            }
            Item seed = seedFor(block);
            world.func_147480_a(targetX, targetY, targetZ, true);
            MaidInventories.collectNearbyItems(maid, 2.5D);
            if (seed != null && isFarmland(world.getBlock(targetX, targetY - 1, targetZ))) {
                int slot = MaidInventories.findItem(maid, seed);
                if (slot >= 0) {
                    MaidInventories.consumeOne(maid, slot);
                    world.setBlock(targetX, targetY, targetZ, cropFor(seed), 0, 3);
                }
            }
            return;
        }
        if (action == TILL) {
            int hoeSlot = MaidInventories.findHoe(maid);
            Block ground = world.getBlock(targetX, targetY, targetZ);
            if (hoeSlot < 0 || (ground != Blocks.dirt && ground != Blocks.grass)) {
                return;
            }
            world.setBlock(targetX, targetY, targetZ, Blocks.farmland);
            ItemStack hoe = maid.getInventory().getStackInSlot(hoeSlot);
            if (hoe != null) {
                hoe.damageItem(1, maid);
                if (hoe.stackSize <= 0) {
                    maid.getInventory().setInventorySlotContents(hoeSlot, null);
                }
            }
            world.playSoundEffect(
                    targetX + 0.5D, targetY + 0.5D, targetZ + 0.5D, "step.gravel", 1.0F, 1.0F);
            return;
        }
        if (action == PLANT) {
            if (world.getBlock(targetX, targetY, targetZ) != Blocks.air
                    || !isFarmland(world.getBlock(targetX, targetY - 1, targetZ))) {
                return;
            }
            int seedSlot = findSeedSlot();
            if (seedSlot < 0) {
                return;
            }
            ItemStack seedStack = maid.getInventory().getStackInSlot(seedSlot);
            Block crop = cropFor(seedStack.getItem());
            if (crop == null) {
                return;
            }
            MaidInventories.consumeOne(maid, seedSlot);
            world.setBlock(targetX, targetY, targetZ, crop, 0, 3);
        }
    }

    private boolean hasAnySeed() {
        return findSeedSlot() >= 0;
    }

    private int findSeedSlot() {
        int wheat = MaidInventories.findItem(maid, Items.wheat_seeds);
        if (wheat >= 0) {
            return wheat;
        }
        int carrot = MaidInventories.findItem(maid, Items.carrot);
        if (carrot >= 0) {
            return carrot;
        }
        return MaidInventories.findItem(maid, Items.potato);
    }

    private static boolean isFarmland(Block block) {
        return block == Blocks.farmland;
    }

    private static boolean isMatureCrop(Block block, int meta) {
        return (block == Blocks.wheat || block == Blocks.carrots || block == Blocks.potatoes) && meta >= 7;
    }

    private static Item seedFor(Block crop) {
        if (crop == Blocks.wheat) {
            return Items.wheat_seeds;
        }
        if (crop == Blocks.carrots) {
            return Items.carrot;
        }
        if (crop == Blocks.potatoes) {
            return Items.potato;
        }
        return null;
    }

    private static Block cropFor(Item seed) {
        if (seed == Items.wheat_seeds) {
            return Blocks.wheat;
        }
        if (seed == Items.carrot) {
            return Blocks.carrots;
        }
        if (seed == Items.potato) {
            return Blocks.potatoes;
        }
        return null;
    }
}
