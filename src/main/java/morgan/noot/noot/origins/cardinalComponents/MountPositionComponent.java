package morgan.noot.noot.origins.cardinalComponents;

import morgan.noot.noot.origins.NootNootOriginsComponents;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;

public class MountPositionComponent extends AutoSyncedFloatComponent{
    public MountPositionComponent(Entity provider) {
        super(provider);
        this.key = "MountPositionComponent";
    }

    public void setValue(float value) {
        this.value = value;
        NootNootOriginsComponents.MOUNT_POSITION.sync(this.provider);
    }
}
