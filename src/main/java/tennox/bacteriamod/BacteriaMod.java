package tennox.bacteriamod;

import java.util.ArrayList;
import java.util.UUID;

import net.minecraft.item.Item;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.world.WorldEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import tennox.bacteriamod.util.ColonyWorldSavedData;
import tennox.bacteriamod.util.CommonProxy;
import tennox.bacteriamod.util.Config;

@Mod(modid = "tennox_bacteria", name = "Bacteria", version = "2.3.3")
public class BacteriaMod {

    public static final String MOD_ID = "tennox_bacteria";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static ArrayList<UUID> jamcolonies = new ArrayList<>();
    public static boolean jam_all;
    @SidedProxy(clientSide = "tennox.bacteriamod.util.ClientProxy", serverSide = "tennox.bacteriamod.util.CommonProxy")
    public static CommonProxy proxy;

    // TODO: one could add a Colony object, that is shared between all bacteria TEs belonging to the colony.
    // This colony class would contain its UUID, and the target blocks to be destroyed or replaced
    // It would also make a HashSet more feasable for better target lookup time
    // However, this would need to be saved to disk somehow

    // TODO: add replacer blacklist

    // TODO: when a new bacterium belonging to a colony is created, keep track of the number of bacteria in the colony
    // when the colony is saved to disk, check if the # of bacteria in the colony is zero
    // if it is, do not save the colony to disk, essentially delete it

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @EventHandler
    public void load(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @SubscribeEvent
    public void onServerLoad(WorldEvent.Load event) { // TODO: fix crash on world load
        if (!event.world.isRemote) {
            ColonyWorldSavedData.getOrCreate(event.world);
        }
    }

    @SubscribeEvent
    public void onPickup(EntityItemPickupEvent event) {
        if (Config.achievementsEnabled && event.item.getEntityItem()
            .getItem() == CommonProxy.bacteriaBunch) event.entityPlayer.addStat(CommonProxy.bacteriaAchievement, 1);
    }

    @SubscribeEvent
    public void onCrafting(ItemCraftedEvent event) { // SlotCrafting
        if (Config.achievementsEnabled) {
            if (event.crafting.getItem() == Item.getItemFromBlock(CommonProxy.must))
                event.player.addStat(CommonProxy.mustAchievement, 1);
            if (event.crafting.getItem() == Item.getItemFromBlock(CommonProxy.bacteria))
                event.player.addStat(CommonProxy.bacteriumAchievement, 1);
        }
    }

    public static String getDomain() {
        return MOD_ID + ":";
    }
}
