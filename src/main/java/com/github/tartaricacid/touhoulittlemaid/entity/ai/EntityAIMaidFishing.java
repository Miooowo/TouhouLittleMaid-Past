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

/**
 * 1.7.10 stand-in for MaidFishingHook: stand by water, wait, then reel loot and wear the rod.
 */
public class EntityAIMaidFishing extends EntityAIBase {

    private static final int RANGE = 8;
    private final EntityMaid maid;
    private int standX;
    private int standY;
    private int standZ;
    private int waterX;
    private int waterY;
    private int waterZ;
    private int fishTicks;
    private int waitTicks;

    public EntityAIMaidFishing(EntityMaid maid) {
        this.maid = maid;
        setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (maid.getTask() != MaidTask.FISHING || maid.isSitting() || maid.worldObj.isRemote) {
            return false;
        }
        if (MaidInventories.findFishingRod(maid) < 0) {
            return false;
        }
        if (maid.getRNG().nextInt(20) != 0) {
            return false;
        }
        return findShore();
    }

    @Override
    public boolean continueExecuting() {
        return fishTicks < waitTicks
                && maid.getTask() == MaidTask.FISHING
                && !maid.isSitting()
                && MaidInventories.findFishingRod(maid) >= 0;
    }

    @Override
    public void startExecuting() {
        fishTicks = 0;
        waitTicks = 80 + maid.getRNG().nextInt(80);
        maid.getNavigator().tryMoveToXYZ(standX + 0.5D, standY, standZ + 0.5D, 1.0D);
    }

    @Override
    public void resetTask() {
        maid.getNavigator().clearPathEntity();
    }

    @Override
    public void updateTask() {
        maid.getLookHelper().setLookPosition(waterX + 0.5D, waterY + 0.5D, waterZ + 0.5D, 10.0F, 40.0F);
        if (maid.getDistanceSq(standX + 0.5D, standY, standZ + 0.5D) > 4.0D) {
            if (maid.getNavigator().noPath()) {
                maid.getNavigator().tryMoveToXYZ(standX + 0.5D, standY, standZ + 0.5D, 1.0D);
            }
            return;
        }
        maid.getNavigator().clearPathEntity();
        fishTicks++;
        if (fishTicks >= waitTicks) {
            reel();
        }
    }

    private boolean findShore() {
        int originX = MathHelper.floor_double(maid.posX);
        int originY = MathHelper.floor_double(maid.posY);
        int originZ = MathHelper.floor_double(maid.posZ);
        World world = maid.worldObj;
        int best = Integer.MAX_VALUE;
        boolean found = false;
        for (int dx = -RANGE; dx <= RANGE; dx++) {
            for (int dz = -RANGE; dz <= RANGE; dz++) {
                for (int dy = -2; dy <= 1; dy++) {
                    int x = originX + dx;
                    int y = originY + dy;
                    int z = originZ + dz;
                    if (!isWater(world.getBlock(x, y, z)) || world.getBlock(x, y + 1, z) != Blocks.air) {
                        continue;
                    }
                    int[][] sides = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
                    for (int i = 0; i < sides.length; i++) {
                        int sx = x + sides[i][0];
                        int sz = z + sides[i][1];
                        if (!isStandable(world, sx, y, sz)) {
                            continue;
                        }
                        int dist = dx * dx + dy * dy + dz * dz;
                        if (dist >= best) {
                            continue;
                        }
                        best = dist;
                        waterX = x;
                        waterY = y;
                        waterZ = z;
                        standX = sx;
                        standY = y;
                        standZ = sz;
                        found = true;
                    }
                }
            }
        }
        return found;
    }

    private static boolean isStandable(World world, int x, int y, int z) {
        Block ground = world.getBlock(x, y - 1, z);
        return ground.getMaterial().isSolid()
                && world.getBlock(x, y, z) == Blocks.air
                && world.getBlock(x, y + 1, z) == Blocks.air;
    }

    private static boolean isWater(Block block) {
        return block == Blocks.water || block == Blocks.flowing_water;
    }

    private void reel() {
        int rodSlot = MaidInventories.findFishingRod(maid);
        if (rodSlot < 0) {
            return;
        }
        MaidInventories.insert(maid, rollLoot());
        MaidInventories.damageSlot(maid, rodSlot, 1);
        maid.worldObj.playSoundAtEntity(maid, "random.splash", 0.6F, 1.0F + maid.getRNG().nextFloat() * 0.2F);
    }

    private ItemStack rollLoot() {
        int roll = maid.getRNG().nextInt(100);
        if (roll < 80) {
            int type = 0;
            int sub = maid.getRNG().nextInt(100);
            if (sub < 10) {
                type = 1;
            } else if (sub < 13) {
                type = 2;
            }
            return new ItemStack(Items.fish, 1, type);
        }
        if (roll < 95) {
            Item[] junk = new Item[] {Items.stick, Items.bone, Items.bowl, Items.leather, Items.rotten_flesh};
            return new ItemStack(junk[maid.getRNG().nextInt(junk.length)]);
        }
        return maid.getRNG().nextBoolean() ? new ItemStack(Items.name_tag) : new ItemStack(Items.saddle);
    }
}
