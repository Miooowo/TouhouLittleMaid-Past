package com.github.tartaricacid.touhoulittlemaid.client.gui;

import org.lwjgl.opengl.GL11;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.inventory.ContainerMaid;
import com.github.tartaricacid.touhoulittlemaid.network.MaidNetwork;
import com.github.tartaricacid.touhoulittlemaid.network.PacketMaidGuiAction;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiMaid extends GuiContainer {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(TouhouLittleMaid.MOD_ID, "textures/gui/maid.png");

    private final EntityMaid maid;
    private GuiButton sitButton;
    private GuiButton taskButton;

    public GuiMaid(InventoryPlayer playerInv, EntityMaid maid) {
        super(new ContainerMaid(playerInv, maid));
        this.maid = maid;
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        super.initGui();
        buttonList.clear();
        sitButton = new GuiButton(0, guiLeft + 7, guiTop + 16, 34, 20, sitLabel());
        taskButton = new GuiButton(1, guiLeft + 7, guiTop + 38, 34, 20, taskLabel());
        buttonList.add(sitButton);
        buttonList.add(taskButton);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (sitButton != null) {
            sitButton.displayString = sitLabel();
        }
        if (taskButton != null) {
            taskButton.displayString = taskLabel();
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            MaidNetwork.CHANNEL.sendToServer(new PacketMaidGuiAction(maid.getEntityId(), PacketMaidGuiAction.SIT));
        } else if (button.id == 1) {
            MaidNetwork.CHANNEL.sendToServer(new PacketMaidGuiAction(maid.getEntityId(), PacketMaidGuiAction.TASK));
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRendererObj.drawString(maid.getCommandSenderName(), 44, 6, 0x404040);
        String hp = I18n.format(
                "gui.touhou_little_maid.health",
                (int) Math.ceil(maid.getHealth()),
                (int) Math.ceil(maid.getMaxHealth()));
        fontRendererObj.drawString(hp, 110, 6, 0x404040);
        fontRendererObj.drawString(
                I18n.format("gui.touhou_little_maid.task", I18n.format(maid.getTask().translationKey())),
                44,
                18,
                0x404040);
        fontRendererObj.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    private String sitLabel() {
        return maid.isSitting()
                ? I18n.format("gui.touhou_little_maid.stand")
                : I18n.format("gui.touhou_little_maid.sit");
    }

    private String taskLabel() {
        return I18n.format("gui.touhou_little_maid.cycle_task");
    }
}
