package tennox.bacteriamod.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

import tennox.bacteriamod.BacteriaMod;

public class BlockBacteriaJammer extends Block {

    public BlockBacteriaJammer() {
        super(Material.rock);
        setCreativeTab(CreativeTabs.tabMisc);
        setHardness(0.5F);
        setBlockTextureName(BacteriaMod.getDomain() + "jammer");
    }
}
