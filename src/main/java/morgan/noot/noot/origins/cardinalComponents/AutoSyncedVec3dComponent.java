package morgan.noot.noot.origins.cardinalComponents;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import morgan.noot.noot.origins.NootNootOriginsComponents;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;

public class AutoSyncedVec3dComponent implements Vec3dComponent, AutoSyncedComponent {
    String key;
    Vec3d value = Vec3d.ZERO;
    public final Entity provider;

    public AutoSyncedVec3dComponent(Entity provider)
    {
        this.provider = provider;
        this.key = "AutoSyncedVec3dComponent";
    }

    @Override
    public Vec3d getValue() {
        return this.value;
    }

    public void setValue(Vec3d value) {
        this.value = value;
        //NootNootOriginsComponents.COMPONENTKEY.sync(this.provider);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.value = new Vec3d(tag.getFloat(key+"X"),tag.getFloat(key+"Y"),tag.getFloat(key+"Z"));
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putFloat(key+"X", (float) this.value.getX());
        tag.putFloat(key+"Y", (float) this.value.getY());
        tag.putFloat(key+"Z", (float) this.value.getZ());
    }
}
