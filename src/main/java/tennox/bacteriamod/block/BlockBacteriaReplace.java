package tennox.bacteriamod.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tennox.bacteriamod.entity.TileEntityBacteriaReplacer;

public class BlockBacteriaReplace extends BlockContainer {

    public BlockBacteriaReplace() {
        super(Material.rock);
        setCreativeTab(CreativeTabs.tabMisc);
        setHardness(0.07F);
    }

    @Override
    public void registerBlockIcons(IIconRegister par1IconRegister) {
        blockIcon = par1IconRegister.registerIcon("tennox_bacteria:replacer");
    }

    @Override
    public TileEntity createNewTileEntity(World w, int i) {
        return new TileEntityBacteriaReplacer();
    }
}
