package tennox.bacteriamod.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tennox.bacteriamod.BacteriaMod;

public class BlockMust extends Block {

    private static final int GROW_TIME = 2;

    public BlockMust() {
        super(Material.sponge);
        setCreativeTab(CreativeTabs.tabMisc);
        setHardness(0.6F);
        setTickRandomly(true);
        setStepSound(Block.soundTypeGrass);
        setBlockTextureName(BacteriaMod.getDomain() + "must");
    }

    @Override
    public int colorMultiplier(IBlockAccess iblockaccess, int i, int j, int k) {
        if (iblockaccess.getBlockMetadata(i, j, k) >= GROW_TIME) return 2411556;
        return 16777215;
    }

    @Override
    public void updateTick(World world, int i, int j, int k, Random random) {
        if (!world.isRemote) {
            int meta = world.getBlockMetadata(i, j, k);
            Block above = world.getBlock(i, j + 1, k);

            // grows two thirds as fast under flowing water; if not underwater, reset metadata
            if (above.equals(Blocks.water) || (above.equals(Blocks.flowing_water) && random.nextInt(3) != 1)) meta++;
            else meta = 0;

            world.setBlockMetadataWithNotify(i, j, k, meta, 3);
        }
    }

    // Block
    @Override
    public Item getItemDropped(int i, Random random, int j) {
        if (i >= GROW_TIME) return BacteriaMod.bacteriaBunch;
        return Item.getItemFromBlock(this);
    }
}
