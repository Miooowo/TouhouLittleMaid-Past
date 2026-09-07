package com.github.tartaricacid.touhoulittlemaid.entity.ai;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;

import net.minecraft.entity.ai.EntityAIFollowOwner;

public class EntityAIMaidFollowOwner extends EntityAIFollowOwner {

    private final EntityMaid maid;

    public EntityAIMaidFollowOwner(EntityMaid maid, double speed, float minDist, float maxDist) {
        super(maid, speed, minDist, maxDist);
        this.maid = maid;
    }

    @Override
    public boolean shouldExecute() {
        return !maid.getTask().isWorldWork() && super.shouldExecute();
    }

    @Override
    public boolean continueExecuting() {
        return !maid.getTask().isWorldWork() && super.continueExecuting();
    }
}
