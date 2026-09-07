package com.github.tartaricacid.touhoulittlemaid;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public final class Config {

    public static boolean goheiCanSpawnMaid = true;
    public static int maidFollowStartDistance = 6;
    public static int maidFollowStopDistance = 2;
    public static boolean ownerAlwaysProtected = false;

    public static void synchronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);

        goheiCanSpawnMaid = configuration.getBoolean(
                "goheiCanSpawnMaid",
                Configuration.CATEGORY_GENERAL,
                goheiCanSpawnMaid,
                "If true, using Hakurei/Sanae Gohei or Smart Slab (Init) on a block spawns a tamed maid. "
                        + "1.20.1 uses an altar ritual instead; this is the 1.7.10 bootstrap path.");
        maidFollowStartDistance = configuration.getInt(
                "maidFollowStartDistance",
                Configuration.CATEGORY_GENERAL,
                maidFollowStartDistance,
                2,
                64,
                "Distance at which a standing maid starts following her owner.");
        maidFollowStopDistance = configuration.getInt(
                "maidFollowStopDistance",
                Configuration.CATEGORY_GENERAL,
                maidFollowStopDistance,
                1,
                16,
                "Distance at which a following maid stops walking.");
        ownerAlwaysProtected = configuration.getBoolean(
                "ownerAlwaysProtected",
                Configuration.CATEGORY_GENERAL,
                ownerAlwaysProtected,
                "If true, maids always retaliate for their owner. If false, only the attack task does.");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    private Config() {}
}
