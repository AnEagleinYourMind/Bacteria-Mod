package tennox.bacteriamod.util;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import tennox.bacteriamod.BacteriaMod;
import tennox.bacteriamod.block.BlockBacteria;
import tennox.bacteriamod.block.BlockBacteriaJammer;
import tennox.bacteriamod.block.BlockBacteriaReplace;
import tennox.bacteriamod.block.BlockMust;
import tennox.bacteriamod.entity.TileEntityBacteria;
import tennox.bacteriamod.entity.TileEntityBacteriaReplacer;
import tennox.bacteriamod.item.ItemBacteriaJammer;
import tennox.bacteriamod.item.ItemBacteriaPotion;
import tennox.bacteriamod.world.BacteriaWorldGenerator;

public class CommonProxy {

    public static Achievement mustAchievement, bacteriaAchievement, bacteriumAchievement, jamAchievement;

    public static Block bacteria, replacer, jammer, must;
    public static Item bacteriaBunch, jammerItem, bacteriaPotion;
    public static BacteriaWorldGenerator worldGen = new BacteriaWorldGenerator();

    public void preInit(FMLPreInitializationEvent event) {
        Config.initializeConfig(new Configuration(event.getSuggestedConfigurationFile()));

        bacteriaBunch = new Item().setUnlocalizedName("tennox_bacteriaitem")
            .setTextureName(BacteriaMod.getDomain() + "bacteria_item")
            .setCreativeTab(CreativeTabs.tabMisc);
        jammerItem = new ItemBacteriaJammer().setUnlocalizedName("tennox_jammeritem");
        bacteriaPotion = new ItemBacteriaPotion().setUnlocalizedName("tennox_bacteriapotion");

        bacteria = new BlockBacteria().setBlockName("tennox_bacteria");
        replacer = new BlockBacteriaReplace().setBlockName("tennox_replacer");
        jammer = new BlockBacteriaJammer().setBlockName("tennox_jammer");
        must = new BlockMust().setBlockName("tennox_must");

        GameRegistry.registerBlock(bacteria, "bacteria");
        GameRegistry.registerBlock(replacer, "replacer");
        GameRegistry.registerBlock(jammer, "jammer");
        GameRegistry.registerBlock(must, "must");
        GameRegistry.registerItem(bacteriaBunch, "bunch");
        GameRegistry.registerItem(jammerItem, "jammerItem");
        GameRegistry.registerItem(bacteriaPotion, "potion");
    }

    public void init(FMLInitializationEvent event) {
        // parse config after PreInitialization when all blocks should be loaded
        if (!Config.unparsedBlacklist.isEmpty()) {
            for (String s : Config.unparsedBlacklist.split(",")) {
                try {
                    int meta = 0;
                    if (s.contains(":")) {
                        String[] s2 = s.split(":");
                        s = s2[0];
                        meta = Integer.parseInt(s2[1]);
                    }

                    int id = Integer.parseInt(s);
                    Block block = Block.getBlockById(id);
                    if (block == Blocks.air) {
                        BacteriaMod.LOGGER.warn("Error while parsing blacklist: ID " + id + " is not a valid block!");
                        continue;
                    }

                    Config.blacklist.add(new TargetBlock(block, meta));

                } catch (NumberFormatException e) {
                    BacteriaMod.LOGGER.warn("Error while parsing blacklist: '" + s + "' is not a valid number!");
                }
            }
        }

        // force blacklist bedrock for obvious reasons
        Config.blacklist.add(new TargetBlock(Blocks.bedrock, 0));
        Config.blacklist.add(new TargetBlock(bacteria, 0));

        GameRegistry.addRecipe(
            new ItemStack(jammer, 1),
            "+#+",
            "#*#",
            "+-+",
            '#',
            bacteria,
            '*',
            Items.iron_ingot,
            '-',
            Blocks.redstone_torch,
            '+',
            Blocks.cobblestone);
        GameRegistry.addRecipe(
            new ItemStack(jammerItem, 1),
            " # ",
            "#*#",
            " - ",
            '#',
            bacteria,
            '*',
            Items.iron_ingot,
            '-',
            Blocks.redstone_torch);
        GameRegistry
            .addRecipe(new ItemStack(bacteria, 1), " # ", "#*#", " # ", '#', bacteriaBunch, '*', Blocks.redstone_torch);
        GameRegistry.addRecipe(new ItemStack(replacer, 1), " # ", "#*#", " # ", '#', bacteriaBunch, '*', Items.coal);
        GameRegistry.addRecipe(
            new ItemStack(must, 1),
            "+*+",
            " # ",
            '+',
            Items.bread,
            '#',
            Items.water_bucket,
            '*',
            Blocks.sponge);
        GameRegistry.addRecipe(
            new ItemStack(Blocks.sponge, 2),
            "+*+",
            "*+*",
            "+#+",
            '+',
            Blocks.wool,
            '#',
            Items.water_bucket,
            '*',
            Blocks.yellow_flower);

        GameRegistry.addRecipe(
            new ItemStack(Blocks.sponge, 2),
            "+*+",
            "*+*",
            "+#+",
            '+',
            Blocks.wool,
            '#',
            Items.water_bucket,
            '*',
            Blocks.yellow_flower);

        GameRegistry
            .addShapelessRecipe(new ItemStack(bacteriaPotion, 1), Items.potionitem, Items.nether_wart, bacteriaBunch);

        if (Config.achievementsEnabled) {
            AchievementPage achievementPage = new AchievementPage(BacteriaMod.MOD_ID);
            AchievementPage.registerAchievementPage(achievementPage);
            List<Achievement> achievementsList = achievementPage.getAchievements();

            mustAchievement = new Achievement("bacteriamod.must", "must", 0, 2, must, AchievementList.buildWorkBench);
            bacteriaAchievement = new Achievement(
                "bacteriamod.bacteria",
                "bacteria",
                1,
                1,
                bacteriaBunch,
                mustAchievement);
            bacteriumAchievement = new Achievement(
                "bacteriamod.bacterium",
                "bacterium",
                2,
                0,
                bacteria,
                bacteriaAchievement).setSpecial();
            jamAchievement = new Achievement("bacteriamod.jammer", "jammer", 3, -1, jammerItem, bacteriumAchievement)
                .setSpecial();

            achievementsList.add(mustAchievement.registerStat());
            achievementsList.add(bacteriaAchievement.registerStat());
            achievementsList.add(bacteriumAchievement.registerStat());
            achievementsList.add(jamAchievement.registerStat());
        }
        GameRegistry.registerTileEntity(TileEntityBacteria.class, "bacteria_tileentity");
        GameRegistry.registerTileEntity(TileEntityBacteriaReplacer.class, "replacer_tileentity");
        GameRegistry.registerWorldGenerator(worldGen, 0);
    }
}
