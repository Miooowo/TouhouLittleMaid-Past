package com.github.tartaricacid.touhoulittlemaid.block;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.init.MaidCreativeTab;
import com.github.tartaricacid.touhoulittlemaid.item.ItemHakureiGohei;
import com.github.tartaricacid.touhoulittlemaid.tileentity.TileEntityTombstone;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

public class BlockTombstone extends BlockContainer {

    public BlockTombstone() {
        super(Material.rock);
        setHardness(-1.0F);
        setResistance(6000000.0F);
        setStepSound(soundTypeStone);
        setBlockName("tombstone");
        setBlockTextureName(TouhouLittleMaid.MOD_ID + ":tombstone");
        setCreativeTab(MaidCreativeTab.TAB);
        setBlockUnbreakable();
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityTombstone();
    }

    @Override
    public boolean onBlockActivated(
            World world,
            int x,
            int y,
            int z,
            EntityPlayer player,
            int side,
            float hitX,
            float hitY,
            float hitZ) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TileEntityTombstone)) {
            return true;
        }
        TileEntityTombstone tomb = (TileEntityTombstone) te;
        if (!tomb.isOwner(player)) {
            if (!world.isRemote) {
                player.addChatMessage(new ChatComponentTranslation("message.touhou_little_maid.tombstone.not_yours"));
            }
            return true;
        }
        ItemStack held = player.getCurrentEquippedItem();
        if (ItemHakureiGohei.isGohei(held) && tomb.hasMaidData()) {
            if (!world.isRemote) {
                tomb.giveItemsTo(player, true);
                if (tomb.reviveMaid(world, player, x + 0.5D, y + 1.0D, z + 0.5D)) {
                    world.setBlockToAir(x, y, z);
                    player.addChatMessage(
                            new ChatComponentTranslation("message.touhou_little_maid.tombstone.revive"));
                }
            }
            return true;
        }
        if (!world.isRemote) {
            boolean allGiven = tomb.giveItemsTo(player, player.isSneaking());
            if (!allGiven) {
                player.addChatMessage(
                        new ChatComponentTranslation("message.touhou_little_maid.tombstone.inventory_full"));
            } else if (tomb.isInventoryEmpty() && !tomb.hasMaidData()) {
                world.setBlockToAir(x, y, z);
            } else if (tomb.isInventoryEmpty()) {
                player.addChatMessage(
                        new ChatComponentTranslation("message.touhou_little_maid.tombstone.use_gohei"));
            }
        }
        return true;
    }

    @Override
    public boolean canSilkHarvest(
            World world, EntityPlayer player, int x, int y, int z, int metadata) {
        return false;
    }
}
