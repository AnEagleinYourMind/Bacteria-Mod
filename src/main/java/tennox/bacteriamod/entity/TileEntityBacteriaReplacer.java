package tennox.bacteriamod.entity;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import tennox.bacteriamod.BacteriaMod;
import tennox.bacteriamod.util.Food;
import tennox.bacteriamod.item.ItemBacteriaJammer;

public class TileEntityBacteriaReplacer extends TileEntityBacteria {

    Food replace;

    public TileEntityBacteriaReplacer() {
        block = BacteriaMod.replacer;
        do colony = rand.nextInt(); while (BacteriaMod.jamcolonies.contains(colony));
    }

    @Override
    public void selectFood() {
        int i = xCoord;
        int j = yCoord;
        int k = zCoord;
        if (worldObj.isBlockIndirectlyGettingPowered(i, j, k)) {
            Block above = worldObj.getBlock(i, j + 1, k);
            Block below = worldObj.getBlock(i, j - 1, k);
            if (above == Blocks.air || above == BacteriaMod.replacer ||
                below == Blocks.air || below == BacteriaMod.replacer) return;
            if (above == below && worldObj.getBlockMetadata(i, j - 1, k) == worldObj.getBlockMetadata(i, j + 1, k))
                return;
            addFood(below, worldObj.getBlockMetadata(i, j - 1, k));
            replace = new Food(above, worldObj.getBlockMetadata(i, j + 1, k));
            worldObj.setBlockToAir(i, j + 1, k);
        }
    }

    @Override
    public boolean shouldStartInstantly() {
        return false;
    }

    @Override
    public void maybeEat(int i, int j, int k) {
        if (isAtBorder(i, j, k)) return;
        if (isFood(worldObj.getBlock(i, j, k), worldObj.getBlockMetadata(i, j, k))) {
            worldObj.setBlock(i, j, k, block);
            TileEntity newTile = worldObj.getTileEntity(i, j, k);
            TileEntityBacteriaReplacer newTile2 = (TileEntityBacteriaReplacer) newTile;
            newTile2.food = food;
            newTile2.colony = colony;
            newTile2.replace = replace;
        }
    }

    @Override
    public void die() {
        if (replace != null) worldObj.setBlock(xCoord, yCoord, zCoord, replace.getBlock(), replace.getMeta(), 3);
        else worldObj.setBlockToAir(xCoord, yCoord, zCoord);
        if (jammed) ItemBacteriaJammer.jammedBacteriaQuantity += 1L;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        Block r = Block.getBlockById(nbt.getInteger("replace"));
        replace = new Food(r, nbt.getInteger("replace_meta"));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        if (replace != null) {
            nbt.setInteger("replace", Block.getIdFromBlock(replace.getBlock()));
            nbt.setInteger("replace_meta", replace.getMeta());
        }
    }
}
