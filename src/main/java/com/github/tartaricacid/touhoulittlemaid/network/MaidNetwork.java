package com.github.tartaricacid.touhoulittlemaid.network;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public final class MaidNetwork {

    public static SimpleNetworkWrapper CHANNEL;

    private MaidNetwork() {}

    public static void init() {
        CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(TouhouLittleMaid.MOD_ID);
        CHANNEL.registerMessage(PacketMaidGuiAction.class, PacketMaidGuiAction.class, 0, Side.SERVER);
    }
}
