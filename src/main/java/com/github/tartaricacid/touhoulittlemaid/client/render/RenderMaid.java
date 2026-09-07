package com.github.tartaricacid.touhoulittlemaid.client.render;

import org.lwjgl.opengl.GL11;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class RenderMaid extends RenderBiped {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(TouhouLittleMaid.MOD_ID, "textures/entity/maid.png");

    public RenderMaid() {
        super(new ModelBiped(0.0F), 0.4F);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return TEXTURE;
    }

    @Override
    protected void preRenderCallback(EntityLivingBase entity, float partialTick) {
        GL11.glScalef(0.85F, 0.85F, 0.85F);
        if (entity instanceof EntityMaid && ((EntityMaid) entity).isSitting()) {
            GL11.glTranslatef(0.0F, 0.35F, 0.0F);
        }
    }
}
