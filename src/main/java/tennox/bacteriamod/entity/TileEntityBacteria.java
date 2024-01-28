package tennox.bacteriamod.entity;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import tennox.bacteriamod.BacteriaMod;
import tennox.bacteriamod.util.Food;
import tennox.bacteriamod.item.ItemBacteriaJammer;

public class TileEntityBacteria extends TileEntity {

    Block block;
    ArrayList<Food> food;
    Random rand = new Random();
    int colony;
    boolean jammed;
    int tick = 0;
    boolean startInstantly;

    public TileEntityBacteria() {
        if (food == null) food = new ArrayList<>();
        block = BacteriaMod.bacteria;
        do colony = rand.nextInt(); while (BacteriaMod.jamcolonies.contains(colony));
    }

    @Override
    public void updateEntity() {
        if (worldObj.isRemote) return;
        if (BacteriaMod.jamcolonies.contains(colony) || BacteriaMod.jam_all) {
            jammed = true;
            die();
            return;
        }

        if (food.isEmpty()) {
            if (!worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)) return;
            selectFood();
            if (food.isEmpty()) return;
            if (shouldStartInstantly()) startInstantly = true;
        }
        if (!startInstantly) {
            if (BacteriaMod.randomize) tick = rand.nextInt(BacteriaMod.speed + 1);
            if (tick < BacteriaMod.speed) {
                tick += 1;
                return;
            }
            tick = 0;
        }

        eatEverything();
    }

    public boolean shouldStartInstantly() {
        return true;
    }

    public void selectFood() {
        int upDirection = yCoord + 1;

        Block b;
        while ((b = worldObj.getBlock(xCoord, upDirection, zCoord)) != Blocks.air) {
            addFood(b, worldObj.getBlockMetadata(xCoord, upDirection, zCoord));
            upDirection++;
        }
    }

    public void addFood(Block block, int meta) {
        if (isValidFood(block, meta)) food.add(new Food(block, meta));
    }

    public static boolean isValidFood(Block block, int meta) {
        return block != Blocks.bedrock && block != BacteriaMod.bacteria;
    }

    public void eatEverything() {
        int i = xCoord;
        int j = yCoord;
        int k = zCoord;
        maybeEat(i + 1, j, k);
        maybeEat(i, j + 1, k);
        maybeEat(i - 1, j, k);
        maybeEat(i, j - 1, k);
        maybeEat(i, j, k + 1);
        maybeEat(i, j, k - 1);

        die();
    }

    public void maybeEat(int i, int j, int k) {
        if (isAtBorder(i, j, k)) return;
        if (isFood(worldObj.getBlock(i, j, k), worldObj.getBlockMetadata(i, j, k))) {
            worldObj.setBlock(i, j, k, block);
            ((TileEntityBacteria) worldObj.getTileEntity(i, j, k)).food = food;
            ((TileEntityBacteria) worldObj.getTileEntity(i, j, k)).colony = colony;
        }
    }

    public boolean isAtBorder(int i, int j, int k) { // Block
        while (worldObj.getBlock(i, j, k) != Block.getBlockFromName(BacteriaMod.isolation)) {
            if (j >= worldObj.getActualHeight()) return false;
            j++;
        }
        return true;
    }

    Food grass = new Food(Blocks.grass, 0);
    Food dirt = new Food(Blocks.dirt, 0);

    public boolean isFood(Block block, int meta) {
        if (BacteriaMod.jamcolonies.contains(colony)) return false;
        if (block == BacteriaMod.jammer) {
            BacteriaMod.jamcolonies.add(colony);

            jammed = true;
            return false;
        }

        for (Food f : BacteriaMod.blacklist) {
            if (isFood2(f, block, meta)) return false;
        }

        for (Food f : food) {
            if (isFood2(f, block, meta)) return true;
        }

        if (block == Blocks.grass) return food.contains(dirt);
        if (block == Blocks.dirt) return food.contains(grass);
        if (block == Blocks.flowing_water || block == Blocks.water) {
            for (Food f : food) {
                if (f.getBlock() == Blocks.water || f.getBlock() == Blocks.flowing_water) return true;
            }
        }
        if (block == Blocks.flowing_lava || block == Blocks.lava) {
            for (Food f : food) {
                if (f.getBlock() == Blocks.lava || f.getBlock() == Blocks.flowing_lava) return true;
            }
        }
        return false;
    }

    private boolean isFood2(Food f, Block block, int meta) {
        if (!block.equals(f.getBlock())) return false;
        if (Item.getItemFromBlock(block) != null && !Item.getItemFromBlock(block)
            .getHasSubtypes()) return true;
        return meta == f.getMeta();
    }

    public void die() {
        worldObj.setBlockToAir(xCoord, yCoord, zCoord); // x,y,z
        if (jammed) ++ItemBacteriaJammer.num;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (food == null) food = new ArrayList<>();

        colony = nbt.getInteger("colony");
        int i = nbt.getInteger("numfood");

        for (int j = 0; j < i; j++) {
            int id = nbt.getInteger("food" + j);
            int meta = nbt.getInteger("food_meta" + j);
            food.add(new Food(Block.getBlockById(id), meta));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        nbt.setInteger("colony", colony);
        nbt.setInteger("numfood", food.size());

        for (int j = 0; j < food.size(); j++) {
            int id = Block.getIdFromBlock(food.get(j).getBlock());

            nbt.setInteger("food" + j, id);
            nbt.setInteger("food_meta" + j, food.get(j).getMeta());
        }
    }
}
