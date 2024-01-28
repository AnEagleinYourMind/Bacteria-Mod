package tennox.bacteriamod.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tennox.bacteriamod.BacteriaMod;
import tennox.bacteriamod.entity.TileEntityBacteria;

public class BlockBacteria extends BlockContainer {

    public BlockBacteria() {
        super(Material.rock);
        setCreativeTab(CreativeTabs.tabMisc);
        setHardness(0.07F);
        setBlockTextureName(BacteriaMod.getDomain() + "normal");
    }

    @Override
    public TileEntity createNewTileEntity(World w, int i) {
        return new TileEntityBacteria();
    }
}
