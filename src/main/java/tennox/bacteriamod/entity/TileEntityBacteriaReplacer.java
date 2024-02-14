package tennox.bacteriamod.entity;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import tennox.bacteriamod.item.ItemBacteriaJammer;
import tennox.bacteriamod.util.CommonProxy;
import tennox.bacteriamod.util.TargetBlock;

public class TileEntityBacteriaReplacer extends TileEntityBacteria {

    protected static Block block = CommonProxy.replacer;

    public TileEntityBacteriaReplacer(int meta) {
        super(meta);
    }

    @Override
    public void initializeTargetList() {
        int i = xCoord;
        int j = yCoord;
        int k = zCoord;
        if (worldObj.isBlockIndirectlyGettingPowered(i, j, k)) {
            Block above = worldObj.getBlock(i, j + 1, k);
            Block below = worldObj.getBlock(i, j - 1, k);
            if (above == Blocks.air || above == CommonProxy.replacer
                || below == Blocks.air
                || below == CommonProxy.replacer) return;
            if (above == below && worldObj.getBlockMetadata(i, j - 1, k) == worldObj.getBlockMetadata(i, j + 1, k))
                return;

            colony.addTargetBlock(new TargetBlock(below, worldObj.getBlockMetadata(i, j - 1, k)));
            colony.setReplaceBlock(new TargetBlock(above, worldObj.getBlockMetadata(i, j + 1, k)));
            worldObj.setBlockToAir(i, j + 1, k);
        }
    }

    @Override
    public boolean shouldStartInstantly() {
        return false;
    }

    @Override
    public void tryConsumeBlock(int i, int j, int k) {
        if (isAtBorder(i, j, k)) return;

        TargetBlock targetBlock = new TargetBlock(worldObj.getBlock(i, j, k), worldObj.getBlockMetadata(i, j, k));
        if (isFood(targetBlock)) {
            worldObj.setBlock(i, j, k, block, 1, 2);
            TileEntityBacteria newTile = (TileEntityBacteria) worldObj.getTileEntity(i, j, k);

            newTile.conjugate(colony);
            // newTile2.targetBlocks = targetBlocks;
            // newTile2.colonyId = colonyId;
            // newTile2.replace = replace;
        }
    }

    @Override
    public void die() {
        TargetBlock replaceBlock = colony.getReplaceBlock();
        if (replaceBlock != null)
            worldObj.setBlock(xCoord, yCoord, zCoord, replaceBlock.getBlock(), replaceBlock.getMeta(), 3);
        else worldObj.setBlockToAir(xCoord, yCoord, zCoord);
        if (jammed) ++ItemBacteriaJammer.jammedBacteriaQuantity;
    }

    /*
     * @Override
     * public void readFromNBT(NBTTagCompound nbt) {
     * super.readFromNBT(nbt);
     * replace = new TargetBlock(Block.getBlockById(nbt.getInteger("replace")), nbt.getInteger("replace_meta"));
     * }
     * @Override
     * public void writeToNBT(NBTTagCompound nbt) {
     * super.writeToNBT(nbt);
     * if (replace != null) {
     * nbt.setInteger("replace", Block.getIdFromBlock(replace.getBlock()));
     * nbt.setInteger("replace_meta", replace.getMeta());
     * }
     * }
     */
}
