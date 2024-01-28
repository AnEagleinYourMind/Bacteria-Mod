package tennox.bacteriamod.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import tennox.bacteriamod.BacteriaMod;

public class ItemBacteria extends Item {

    public ItemBacteria() {
        super();
        maxStackSize = 64;
        setCreativeTab(CreativeTabs.tabMisc);
        setTextureName(BacteriaMod.getDomain() + "bacteria_item");
    }

}
