package com.github.tartaricacid.touhoulittlemaid.inventory;

import com.github.tartaricacid.touhoulittlemaid.client.gui.GuiMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.IGuiHandler;

public class MaidGuiHandler implements IGuiHandler {

    public static final int MAID = 0;

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == MAID) {
            Entity entity = world.getEntityByID(x);
            if (entity instanceof EntityMaid) {
                EntityMaid maid = (EntityMaid) entity;
                if (maid.func_152114_e(player)) {
                    return new ContainerMaid(player.inventory, maid);
                }
            }
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == MAID) {
            Entity entity = world.getEntityByID(x);
            if (entity instanceof EntityMaid) {
                EntityMaid maid = (EntityMaid) entity;
                if (maid.func_152114_e(player)) {
                    return new GuiMaid(player.inventory, maid);
                }
            }
        }
        return null;
    }
}
