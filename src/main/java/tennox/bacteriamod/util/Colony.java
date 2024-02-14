package tennox.bacteriamod.util;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Colony {

    private UUID colonyId;
    private Set<TargetBlock> targetBlocks;
    private TargetBlock replaceBlock;
    private int numberOfBacteria;

    public Colony(UUID existingColonyId) {
        System.out.println("created new colony");
        targetBlocks = new HashSet<>();
        colonyId = existingColonyId;
    }

    public Colony(String serialized) {
        // TODO
    }

    public void incrementBacteriaCount() {
        ++numberOfBacteria;
    }

    public void decrementBacteriaCount() {
        --numberOfBacteria;
    }

    public boolean isExtinct() {
        return numberOfBacteria <= 0;
    }

    public void setReplaceBlock(TargetBlock block) {
        replaceBlock = block;
    }

    public TargetBlock getReplaceBlock() {
        return replaceBlock;
    }

    public void addTargetBlock(TargetBlock target) {
        targetBlocks.add(target);
    }

    public boolean isTargetBlock(TargetBlock x) {
        return targetBlocks.contains(x);
    }

    public boolean isInitialized() {
        return !targetBlocks.isEmpty();
    }

    public UUID getColonyId() {
        return colonyId;
    }

    public String serialize() {
        return "bingus"; // TODO
    }
}
