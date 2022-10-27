package morgan.noot.noot.origins.cardinalComponents;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;

public class MountPositionComponent extends AutoSyncedFloatComponent{
    public MountPositionComponent(Entity provider) {
        super(provider);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.value = tag.getFloat("MountPositionComponent");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putFloat("MountPositionComponent",this.value);
    }
}
