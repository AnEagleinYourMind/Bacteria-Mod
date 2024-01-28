package tennox.bacteriamod.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import tennox.bacteriamod.BacteriaMod;
import tennox.bacteriamod.util.Config;

public class ItemBacteriaJammer extends Item {

    private int tick;
    public static long jammedBacteriaQuantity;

    public ItemBacteriaJammer() {
        maxStackSize = 1;
        setCreativeTab(CreativeTabs.tabMisc);
        setTextureName(BacteriaMod.getDomain() + "jammer_item");
    }

    @Override
    public void onUpdate(ItemStack item, World world, Entity entity, int i, boolean flag) {
        if (tick > 0) {
            tick -= 1;
            if (tick == 0) {
                BacteriaMod.jam_all = false;
                ((EntityPlayer) entity)
                    .addChatMessage(new ChatComponentText("Jammed " + jammedBacteriaQuantity + " bacteria!"));
                jammedBacteriaQuantity = 0L;

                System.out.println(BacteriaMod.jamcolonies.size());
            }
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack item, World world, EntityPlayer player) {
        if (!world.isRemote) {
            System.out.print("Before: ");
            System.out.println(BacteriaMod.jamcolonies.size());
            BacteriaMod.jam_all = true;
            tick = 30;
            if (Config.achievementsEnabled) player.addStat(BacteriaMod.jamAchievement, 1);
            player.addChatMessage(new ChatComponentText("Jamming bacteria..."));
        }
        return item;
    }
}
