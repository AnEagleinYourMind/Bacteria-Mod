package tennox.bacteriamod.event;

import net.minecraft.item.Item;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import tennox.bacteriamod.util.CommonProxy;
import tennox.bacteriamod.util.Config;

public class CommonEventListener {

    @SubscribeEvent
    public void onItemCraft(PlayerEvent.ItemCraftedEvent event) {
        if (Config.achievementsEnabled) {
            if (event.crafting.getItem() == Item.getItemFromBlock(CommonProxy.must))
                event.player.addStat(CommonProxy.mustAchievement, 1);
            if (event.crafting.getItem() == Item.getItemFromBlock(CommonProxy.bacteria))
                event.player.addStat(CommonProxy.bacteriumAchievement, 1);
        }
    }

    @SubscribeEvent
    public void onItemCollected(PlayerEvent.ItemPickupEvent event) {
        if (Config.achievementsEnabled && event.pickedUp.getEntityItem()
            .getItem() == CommonProxy.bacteriaBunch) event.player.addStat(CommonProxy.bacteriaAchievement, 1);
    }
}
