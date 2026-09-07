package com.github.tartaricacid.touhoulittlemaid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(
        modid = TouhouLittleMaid.MOD_ID,
        version = Tags.VERSION,
        name = TouhouLittleMaid.MOD_NAME,
        acceptedMinecraftVersions = "[1.7.10]")
public class TouhouLittleMaid {

    public static final String MOD_ID = "touhou_little_maid";
    public static final String MOD_NAME = "TouhouLittleMaid: Past";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Mod.Instance(MOD_ID)
    public static TouhouLittleMaid instance;

    @SidedProxy(
            clientSide = "com.github.tartaricacid.touhoulittlemaid.ClientProxy",
            serverSide = "com.github.tartaricacid.touhoulittlemaid.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }
}
