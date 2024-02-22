package tennox.bacteriamod.entity;

import java.util.Random;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import tennox.bacteriamod.BacteriaMod;
import tennox.bacteriamod.item.ItemBacteriaJammer;
import tennox.bacteriamod.util.Colony;
import tennox.bacteriamod.util.ColonyWorldSavedData;
import tennox.bacteriamod.util.CommonProxy;
import tennox.bacteriamod.util.Config;
import tennox.bacteriamod.util.TargetBlock;

public class TileEntityBacteria extends TileEntity {

    private static final TargetBlock GRASS = new TargetBlock(Blocks.grass, 0);
    protected static Block block = CommonProxy.bacteria;
    private static final Random rand = new Random();
    protected Colony colony;
    int tick = 0;
    boolean jammed;
    boolean startInstantly;

    public TileEntityBacteria(int meta) {
        if (meta == 0) {
            UUID newRandomId = UUID.randomUUID();
            // colony = ColonyWorldSavedData.loadOrCreate(worldObj).getColony(newRandomId); // TODO
            colony = new Colony(newRandomId); // FIXME this runs twice, once on server and once on client
            colony.incrementBacteriaCount();
        }
    }

    public void conjugate(Colony colony) {
        this.colony = colony;
        this.colony.incrementBacteriaCount();
    }

    @Override
    public void updateEntity() {
        if (!worldObj.isRemote) {
            if (BacteriaMod.jam_all) {
                jammed = true;
                die();
                return;
            }

            if (BacteriaMod.jamcolonies.contains(colony.getColonyId())) {
                // eventually you will want to remove the colony UUID from the list to prevent a memory leak buuuuuut
                // it isn't that serious and for now I will just leave this crappy looking redundant conditional thing
                jammed = true;
                die();
                return;
            }

            if (!colony.isInitialized()) {
                if (!worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)) return;
                initializeTargetList();
                if (!colony.isInitialized()) return;
                if (shouldStartInstantly()) startInstantly = true; // FIXME: what??
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
            TargetBlock target = new TargetBlock(
                worldObj.getBlock(xCoord, upDirection, zCoord),
                worldObj.getBlockMetadata(xCoord, upDirection, zCoord));

            if (!Config.blacklist.contains(target)) colony.addTargetBlock(target);

            if (colony.isTargetBlock(GRASS)) colony.addTargetBlock(new TargetBlock(Blocks.dirt, 0));

            upDirection++;
        }
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
            worldObj.setBlock(i, j, k, block, 1, 2);

            ((TileEntityBacteria) worldObj.getTileEntity(i, j, k)).conjugate(colony);
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
        if (block == CommonProxy.jammer) {
            BacteriaMod.jamcolonies.add(colony.getColonyId());

            jammed = true;
            return false;
        }

        return colony.isTargetBlock(target);
    }

    public void die() {
        colony.decrementBacteriaCount();
        worldObj.setBlockToAir(xCoord, yCoord, zCoord);
        if (jammed) ++ItemBacteriaJammer.jammedBacteriaQuantity;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        try {
            System.out.println(nbt.getString("colony"));
            colony = ColonyWorldSavedData.getOrCreate(worldObj)
                .getColony(UUID.fromString(nbt.getString("colony")));
        } catch (Exception e) {
            BacteriaMod.LOGGER.warn(e);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        nbt.setString(
            "colony",
            colony.getColonyId()
                .toString());
    }
}
