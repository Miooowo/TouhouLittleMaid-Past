package com.github.tartaricacid.touhoulittlemaid.network;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketMaidGuiAction implements IMessage, IMessageHandler<PacketMaidGuiAction, IMessage> {

    public static final byte SIT = 0;
    public static final byte TASK = 1;

    private int entityId;
    private byte action;

    public PacketMaidGuiAction() {}

    public PacketMaidGuiAction(int entityId, byte action) {
        this.entityId = entityId;
        this.action = action;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        action = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeByte(action);
    }

    @Override
    public IMessage onMessage(PacketMaidGuiAction message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        if (player == null || player.worldObj == null) {
            return null;
        }
        Entity entity = player.worldObj.getEntityByID(message.entityId);
        if (!(entity instanceof EntityMaid)) {
            return null;
        }
        EntityMaid maid = (EntityMaid) entity;
        if (!maid.isEntityAlive() || !maid.func_152114_e(player) || player.getDistanceToEntity(maid) > 8.0F) {
            return null;
        }
        if (message.action == SIT) {
            maid.toggleSitting(player);
        } else if (message.action == TASK) {
            maid.cycleTask(player);
        }
        return null;
    }
}
