package tennox.bacteriamod.util;

import java.util.*;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

import tennox.bacteriamod.BacteriaMod;

public class ColonyWorldSavedData extends WorldSavedData {

    private static final String COLONY_DATA = BacteriaMod.MOD_ID + "_ColonyData";
    private static Map<UUID, Colony> colonies = new HashMap<>();

    public ColonyWorldSavedData() {
        super(COLONY_DATA);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        @SuppressWarnings("rawtypes") // teehee
        Set set = tagCompound.func_150296_c();

        for (Object o : set) {
            String colonyIdAsString = (String) o;
            UUID colonyId = UUID.fromString(colonyIdAsString);
            String colonyAsString = tagCompound.getString(colonyIdAsString);
            Colony colony = new Colony(colonyAsString);

            colonies.put(colonyId, colony);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        NBTTagCompound tagList = new NBTTagCompound();

        colonies.forEach((id, colony) -> {
            // do not save colonies to disk with 0 bacteria
            if (!colony.isExtinct()) tagList.setString(id.toString(), colony.serialize());
        });

        tagCompound.setTag("Colonies", tagList);
    }

    public Colony getColony(UUID colonyId) {
        return colonies.get(colonyId);
    }

    public static ColonyWorldSavedData getOrCreate(World world) {
        System.out.println("HELLO FROM getOrCreate YES this method is running");
        try {
            MapStorage storage = world.mapStorage;

            ColonyWorldSavedData globalData = (ColonyWorldSavedData) storage
                .loadData(ColonyWorldSavedData.class, COLONY_DATA);

            if (globalData == null) {
                globalData = new ColonyWorldSavedData();
                storage.setData(COLONY_DATA, globalData);
            }

            globalData.markDirty();

            return globalData;
        } catch (Exception e) {
            System.err.println("could not access mapStorage?");

            return new ColonyWorldSavedData(); // shut up
        }
    }
}
