package tennox.bacteriamod;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import org.apache.logging.log4j.Logger;
import tennox.bacteriamod.block.BlockBacteria;
import tennox.bacteriamod.block.BlockBacteriaJammer;
import tennox.bacteriamod.block.BlockBacteriaReplace;
import tennox.bacteriamod.block.BlockMust;
import tennox.bacteriamod.entity.TileEntityBacteria;
import tennox.bacteriamod.entity.TileEntityBacteriaReplacer;
import tennox.bacteriamod.item.ItemBacteriaJammer;
import tennox.bacteriamod.item.ItemBacteriaPotion;
import tennox.bacteriamod.util.Food;
import tennox.bacteriamod.world.BacteriaWorldGenerator;

import java.util.ArrayList;
import java.util.List;

@Mod(modid = "tennox_bacteria", name = "Bacteria", version = "2.3.3")
public class BacteriaMod {
    public static final String MOD_ID = "tennox_bacteria";
    @Instance(MOD_ID)
    public static BacteriaMod instance;
    public static boolean achievements = false;

    public static Logger logger;
    public static BacteriaWorldGenerator worldGen = new BacteriaWorldGenerator();
    public static boolean randomize;
    public static String isolation;
    public static ArrayList<Food> blacklist;
    public static int speed;
    public static Item bacteriaBunch;
    public static Item jammerItem;
    public static Item bacteriaPotion;
    public static Block bacteria;
    public static Block replacer;
    public static Block jammer;
    public static Block must;
    public static Achievement mustAchievement;
    public static Achievement bacteriaAchievement;
    public static Achievement bacteriumAchievement;
    public static Achievement jamAchievement;
    public static ArrayList<Integer> jamcolonies = new ArrayList<>();
    public static boolean jam_all;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        FMLCommonHandler.instance()
            .bus()
            .register(this);
        MinecraftForge.EVENT_BUS.register(this);

        logger = event.getModLog();

        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        achievements = config.get("General", "Enable achievements", true)
            .getBoolean(true);

        isolation = config.get("General", "isolation block", "brick_block")
            .getString();
        speed = config.get("General", "bacteria speed", 50)
            .getInt();
        randomize = config.get("General", "randomize bacteria spread", true)
            .getBoolean(true);
        String blacklist1 = config.get("General", "blacklist", "")
            .getString();
        config.save();

        blacklist = new ArrayList<>();
        if (!blacklist1.isEmpty()) {
            for (String s : blacklist1.split(",")) {
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
                        logger.error("Error while parsing blacklist: ID " + id + " is not a valid block!");
                    } else {
                        blacklist.add(new Food(block, meta));
                    }
                } catch (NumberFormatException e) {
                    logger.error("Error while parsing blacklist: '" + s + "' is not a valid number!");
                }
            }
        }

        bacteriaBunch = new Item().setUnlocalizedName("tennox_bacteriaitem").setTextureName(BacteriaMod.getDomain() + "bacteria_item").setCreativeTab(CreativeTabs.tabMisc);
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

    @EventHandler
    public void load(FMLInitializationEvent event) {
        instance = this;

        GameRegistry.addRecipe(
            new ItemStack(jammer, 1),
                "+#+", "#*#", "+-+", '#', bacteria, '*',
                Items.iron_ingot, '-', Blocks.redstone_torch, '+',
                Blocks.cobblestone);
        GameRegistry.addRecipe(
            new ItemStack(jammerItem, 1),
                " # ", "#*#", " - ", '#', bacteria, '*',
                Items.iron_ingot, '-', Blocks.redstone_torch);
        GameRegistry.addRecipe(
            new ItemStack(bacteria, 1),
                " # ", "#*#", " # ", '#', bacteriaBunch, '*',
                Blocks.redstone_torch);
        GameRegistry.addRecipe(
            new ItemStack(replacer, 1),
                " # ", "#*#", " # ", '#', bacteriaBunch, '*',
                Items.coal);
        GameRegistry.addRecipe(
            new ItemStack(must, 1),
                "+*+", " # ", '+', Items.bread, '#',
                Items.water_bucket, '*', Blocks.sponge);
        GameRegistry.addRecipe(
            new ItemStack(Blocks.sponge, 2),
                "+*+", "*+*", "+#+", '+', Blocks.wool, '#',
                Items.water_bucket, '*', Blocks.yellow_flower);

        GameRegistry.addRecipe(
            new ItemStack(Blocks.sponge, 2),
                "+*+", "*+*", "+#+", '+', Blocks.wool, '#',
                Items.water_bucket, '*', Blocks.yellow_flower);

        GameRegistry
            .addShapelessRecipe(new ItemStack(bacteriaPotion, 1), Items.potionitem, Items.nether_wart, bacteriaBunch);

        if (achievements) {
            AchievementPage achievementPage = new AchievementPage(MOD_ID);
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

    @SubscribeEvent
    public void onPickup(EntityItemPickupEvent event) {
        if (achievements && event.item.getEntityItem()
            .getItem() == bacteriaBunch) event.entityPlayer.addStat(bacteriaAchievement, 1);
    }

    @SubscribeEvent
    public void onCrafting(ItemCraftedEvent event) { // SlotCrafting
        if (achievements) {
            if (event.crafting.getItem() == Item.getItemFromBlock(must)) event.player.addStat(mustAchievement, 1);
            if (event.crafting.getItem() == Item.getItemFromBlock(bacteria))
                event.player.addStat(bacteriumAchievement, 1);
        }
    }

    public static String getDomain() {
        return MOD_ID + ":";
    }
}
