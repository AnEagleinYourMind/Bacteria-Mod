package tennox.bacteriamod.entity;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import tennox.bacteriamod.BacteriaMod;
import tennox.bacteriamod.item.ItemBacteriaJammer;
import tennox.bacteriamod.util.Config;
import tennox.bacteriamod.util.TargetBlock;

public class TileEntityBacteria extends TileEntity {

    private static final TargetBlock GRASS = new TargetBlock(Blocks.grass, 0);
    protected static Block block;
    static final Random rand = new Random();
    ArrayList<TargetBlock> targetBlocks; // TODO: could this be a set?
    UUID colony;
    int tick = 0;
    boolean jammed;
    boolean startInstantly;

    public TileEntityBacteria(Block represented) {
        block = represented;
        targetBlocks = new ArrayList<>();
        colony = UUID.randomUUID(); // TODO: figure out how to make this run on the server only?
    }

    @Override
    public void updateEntity() {
        if (!worldObj.isRemote) {
            if (BacteriaMod.jamcolonies.contains(colony)) {
                // eventually you will want to remove the colony UUID from the list to prevent a memory leak buuuuuut
                // it isn't that serious and for now I will just leave this crappy looking redundant conditional thing
                jammed = true;
                die();
                return;
            }

            if (BacteriaMod.jam_all) {
                jammed = true;
                die();
                return;
            }

            if (targetBlocks.isEmpty()) {
                if (!worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)) return;
                initializeTargetList();
                if (targetBlocks.isEmpty()) return;
                if (shouldStartInstantly()) startInstantly = true;
            }
            if (!startInstantly) {
                if (Config.randomSpreadSpeedEnabled) tick = rand.nextInt(Config.spreadSpeed + 1);
                if (tick < Config.spreadSpeed) {
                    ++tick;
                    return;
                }
                tick = 0;
            }

            tryConsumeAdjacentBlocks();
        }
    }

    public boolean shouldStartInstantly() {
        return true;
    }

    public void initializeTargetList() {
        int upDirection = yCoord + 1;

        while (!worldObj.isAirBlock(xCoord, upDirection, zCoord)) {
            TargetBlock wrappedTarget = new TargetBlock(
                worldObj.getBlock(xCoord, upDirection, zCoord),
                worldObj.getBlockMetadata(xCoord, upDirection, zCoord));

            if (!Config.blacklist.contains(wrappedTarget)) targetBlocks.add(wrappedTarget);

            if (targetBlocks.contains(GRASS)) targetBlocks.add(new TargetBlock(Blocks.dirt, 0));

            upDirection++;
        }
    }

    public void addTargetBlock(TargetBlock target) {
        targetBlocks.add(target);
    }

    public void tryConsumeAdjacentBlocks() {
        int i = xCoord;
        int j = yCoord;
        int k = zCoord;
        tryConsumeBlock(i + 1, j, k);
        tryConsumeBlock(i, j + 1, k);
        tryConsumeBlock(i - 1, j, k);
        tryConsumeBlock(i, j - 1, k);
        tryConsumeBlock(i, j, k + 1);
        tryConsumeBlock(i, j, k - 1);

        die();
    }

    public void tryConsumeBlock(int i, int j, int k) {
        if (isAtBorder(i, j, k)) return;

        TargetBlock targetBlock = new TargetBlock(worldObj.getBlock(i, j, k), worldObj.getBlockMetadata(i, j, k));
        if (isFood(targetBlock)) {
            worldObj.setBlock(i, j, k, block);
            ((TileEntityBacteria) worldObj.getTileEntity(i, j, k)).targetBlocks = targetBlocks;
            ((TileEntityBacteria) worldObj.getTileEntity(i, j, k)).colony = colony;
        }
    }

    public boolean isAtBorder(int i, int j, int k) {
        while (worldObj.getBlock(i, j, k) != Config.isolatorBlock) {
            if (j >= worldObj.getActualHeight()) return false;
            ++j;
        }
        return true;
    }

    public boolean isFood(TargetBlock target) {
        if (block == BacteriaMod.jammer) {
            BacteriaMod.jamcolonies.add(colony);

            jammed = true;
            return false;
        }

        return targetBlocks.contains(target);
    }

    public void die() {
        worldObj.setBlockToAir(xCoord, yCoord, zCoord);
        if (jammed) ++ItemBacteriaJammer.jammedBacteriaQuantity;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (targetBlocks == null) targetBlocks = new ArrayList<>();

        colony = UUID.fromString(nbt.getString("colony"));
        int i = nbt.getInteger("numfood");

        for (int j = 0; j < i; j++) {
            int id = nbt.getInteger("food" + j);
            int meta = nbt.getInteger("food_meta" + j);
            targetBlocks.add(new TargetBlock(Block.getBlockById(id), meta));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        nbt.setString("colony", colony.toString());
        nbt.setInteger("numfood", targetBlocks.size());

        for (int j = 0; j < targetBlocks.size(); j++) {
            int id = Block.getIdFromBlock(
                targetBlocks.get(j)
                    .getBlock());

            nbt.setInteger("food" + j, id);
            nbt.setInteger(
                "food_meta" + j,
                targetBlocks.get(j)
                    .getMeta());
        }
    }
}
