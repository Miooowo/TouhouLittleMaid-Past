package com.github.tartaricacid.touhoulittlemaid.entity.passive;

import com.github.tartaricacid.touhoulittlemaid.Config;
import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.EntityAIMaidOwnerHurtByTarget;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.EntityAIMaidOwnerHurtTarget;
import com.github.tartaricacid.touhoulittlemaid.entity.task.MaidTask;
import com.github.tartaricacid.touhoulittlemaid.item.ItemHakureiGohei;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityMaid extends EntityTameable {

    public static final int INVENTORY_SIZE = 15;
    private static final int DW_TASK = 18;
    private static final String NBT_TASK = "MaidTask";
    private static final String NBT_INVENTORY = "MaidInventory";

    private final InventoryBasic inventory;

    public EntityMaid(World world) {
        super(world);
        this.inventory = new InventoryBasic("MaidInventory", false, INVENTORY_SIZE);
        this.setSize(0.6F, 1.5F);
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, this.aiSit);
        this.tasks.addTask(3, new EntityAIAttackOnCollide(this, 1.2D, true));
        this.tasks.addTask(
                4,
                new EntityAIFollowOwner(
                        this, 1.0D, Config.maidFollowStartDistance, Config.maidFollowStopDistance));
        this.tasks.addTask(5, new EntityAIWander(this, 0.8D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIMaidOwnerHurtByTarget(this));
        this.targetTasks.addTask(2, new EntityAIMaidOwnerHurtTarget(this));
        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.30D);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(64.0D);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(2.0D);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(DW_TASK, Byte.valueOf((byte) MaidTask.IDLE.ordinal()));
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }

    public MaidTask getTask() {
        return MaidTask.byOrdinal(this.dataWatcher.getWatchableObjectByte(DW_TASK));
    }

    public void setTask(MaidTask task) {
        if (task == null) {
            task = MaidTask.IDLE;
        }
        this.dataWatcher.updateObject(DW_TASK, Byte.valueOf((byte) task.ordinal()));
        if (task != MaidTask.ATTACK) {
            this.setAttackTarget(null);
        }
    }

    public void cycleTask(EntityPlayer player) {
        MaidTask next = getTask().nextImplemented();
        setTask(next);
        if (player != null && !worldObj.isRemote) {
            player.addChatMessage(
                    new ChatComponentTranslation("message.touhou_little_maid.task.set", next.namespacedId()));
        }
    }

    public boolean canProtectOwner() {
        return isTamed() && !isSitting() && (Config.ownerAlwaysProtected || getTask() == MaidTask.ATTACK);
    }

    public InventoryBasic getInventory() {
        return inventory;
    }

    public static EntityMaid spawnTamed(World world, EntityPlayer player, double x, double y, double z) {
        EntityMaid maid = new EntityMaid(world);
        maid.setLocationAndAngles(x, y, z, player.rotationYaw, 0.0F);
        maid.setTamed(true);
        maid.func_152115_b(player.getUniqueID().toString());
        maid.setSitting(false);
        maid.setTask(MaidTask.IDLE);
        if (!world.isRemote) {
            world.spawnEntityInWorld(maid);
        }
        return maid;
    }

    @Override
    public boolean interact(EntityPlayer player) {
        ItemStack held = player.getCurrentEquippedItem();
        if (!isTamed()) {
            if (!worldObj.isRemote) {
                setTamed(true);
                func_152115_b(player.getUniqueID().toString());
                setSitting(false);
                playTameEffect(true);
                worldObj.setEntityState(this, (byte) 7);
            }
            return true;
        }
        if (func_152114_e(player)) {
            if (held != null && ItemHakureiGohei.isGohei(held)) {
                if (!worldObj.isRemote) {
                    cycleTask(player);
                }
                return true;
            }
            if (!worldObj.isRemote) {
                setSitting(!isSitting());
                isJumping = false;
                setPathToEntity(null);
                setTarget(null);
                setAttackTarget(null);
                player.addChatMessage(
                        new ChatComponentTranslation(
                                isSitting()
                                        ? "message.touhou_little_maid.sit"
                                        : "message.touhou_little_maid.stand"));
            }
            return true;
        }
        return super.interact(player);
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        float damage = (float) getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
        return target.attackEntityFrom(DamageSource.causeMobDamage(this), damage);
    }

    @Override
    public EntityAgeable createChild(EntityAgeable parent) {
        return null;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setString(NBT_TASK, getTask().namespacedId());
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack != null) {
                NBTTagCompound slot = new NBTTagCompound();
                slot.setByte("Slot", (byte) i);
                stack.writeToNBT(slot);
                list.appendTag(slot);
            }
        }
        tag.setTag(NBT_INVENTORY, list);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        if (tag.hasKey(NBT_TASK)) {
            setTask(MaidTask.byId(tag.getString(NBT_TASK)));
        }
        NBTTagList list = tag.getTagList(NBT_INVENTORY, 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound slot = list.getCompoundTagAt(i);
            int index = slot.getByte("Slot") & 255;
            if (index < inventory.getSizeInventory()) {
                inventory.setInventorySlotContents(index, ItemStack.loadItemStackFromNBT(slot));
            }
        }
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    protected String getLivingSound() {
        return "mob.villager.idle";
    }

    @Override
    protected String getHurtSound() {
        return "game.neutral.hurt";
    }

    @Override
    protected String getDeathSound() {
        return "game.neutral.die";
    }

    @Override
    public boolean getCanSpawnHere() {
        return false;
    }

    @Override
    public String getCommandSenderName() {
        return hasCustomNameTag() ? getCustomNameTag() : super.getCommandSenderName();
    }

    @SuppressWarnings("unused")
    public boolean isOwner(EntityLivingBase living) {
        return func_152114_e(living);
    }

    @Override
    public String toString() {
        return TouhouLittleMaid.MOD_ID + ":maid";
    }
}
