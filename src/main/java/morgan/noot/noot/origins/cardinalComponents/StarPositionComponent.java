package morgan.noot.noot.origins.cardinalComponents;

import morgan.noot.noot.origins.NootNootOriginsComponents;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;

public class StarPositionComponent extends AutoSyncedVec3dComponent{
    public StarPositionComponent(Entity provider) {
        super(provider);
        this.key = "StarPositionComponent";
    }

    public void setValue(Vec3d value) {
        this.value = value;
        NootNootOriginsComponents.STAR_POSITION.sync(this.provider);
    }
}