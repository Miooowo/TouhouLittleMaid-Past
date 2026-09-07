package com.github.tartaricacid.touhoulittlemaid.entity.ai;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;

import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;

public class EntityAIMaidOwnerHurtTarget extends EntityAIOwnerHurtTarget {

    private final EntityMaid maid;

    public EntityAIMaidOwnerHurtTarget(EntityMaid maid) {
        super(maid);
        this.maid = maid;
    }

    @Override
    public boolean shouldExecute() {
        return maid.canProtectOwner() && super.shouldExecute();
    }

    @Override
    public boolean continueExecuting() {
        return maid.canProtectOwner() && super.continueExecuting();
    }
}
