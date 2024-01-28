package tennox.bacteriamod.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraftforge.common.config.Configuration;

public class Config {

    public static boolean achievementsEnabled;
    public static Block isolatorBlock;
    public static int spreadSpeed;
    public static boolean randomSpreadSpeedEnabled;
    public static String unparsedBlacklist;
    public static List<TargetBlock> blacklist = new ArrayList<>();

    public static void initializeConfig(Configuration config) {
        config.load();
        achievementsEnabled = config.get("General", "Enable achievements", true)
            .getBoolean(true);

        isolatorBlock = Block.getBlockFromName(
            config.get("General", "isolation block", "brick_block")
                .getString());

        spreadSpeed = config.get("General", "bacteria speed", 50)
            .getInt();

        randomSpreadSpeedEnabled = config.get("General", "randomize bacteria spread", true)
            .getBoolean(true);

        unparsedBlacklist = config.get("General", "blacklist", "")
            .getString();

        config.save();
    }

}
