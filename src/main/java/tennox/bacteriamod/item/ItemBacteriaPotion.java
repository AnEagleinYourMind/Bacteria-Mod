package tennox.bacteriamod.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import tennox.bacteriamod.BacteriaMod;
import tennox.bacteriamod.entity.EntityBacteriaPotion;

public class ItemBacteriaPotion extends Item {

    // Items
    public ItemBacteriaPotion() {
        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.tabMisc);
        setTextureName(BacteriaMod.getDomain() + "bacteria_potion");
    }

    @Override // EntityPotion
    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {
        if (!player.capabilities.isCreativeMode) {
            --itemstack.stackSize;
        }

        world.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        if (!world.isRemote) {
            world.spawnEntityInWorld(new EntityBacteriaPotion(world, player, itemstack));
        }

        return itemstack;
    }
}
