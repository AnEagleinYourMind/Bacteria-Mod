package tennox.bacteriamod;

import net.minecraft.block.Block;

public class Food {

    private final Block block;
    private final int meta;

    public Food(Block block, int meta) {
        this.block = block;
        this.meta = meta;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Food)) return false;
        Food f = (Food) o;
        return block.equals(f.block) && meta == f.meta;
    }

    @Override
    public String toString() {
        return String.format("Food[id=%d, meta=%d]", Block.getIdFromBlock(block), meta);
    }

    public Block getBlock() { return block; }

    public int getMeta() { return meta; }
}
