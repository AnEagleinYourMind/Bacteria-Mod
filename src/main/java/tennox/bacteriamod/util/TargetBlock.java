package tennox.bacteriamod.util;

import net.minecraft.block.Block;

public class TargetBlock {

    private final Block block;
    private final int meta;

    public TargetBlock(Block block, int meta) {
        this.block = block;
        this.meta = meta;
    }

    @Override
    public boolean equals(Object target) {
        if (!(target instanceof TargetBlock)) return false;

        return block.equals(((TargetBlock) target).getBlock()) && meta == ((TargetBlock) target).getMeta();
    }

    @Override
    public String toString() {
        return String.format("Food[id=%d, meta=%d]", Block.getIdFromBlock(block), meta);
    }

    public Block getBlock() {
        return block;
    }

    public int getMeta() {
        return meta;
    }
}