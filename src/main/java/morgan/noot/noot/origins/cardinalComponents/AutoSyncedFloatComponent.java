package morgan.noot.noot.origins.cardinalComponents;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import morgan.noot.noot.origins.NootNootOriginsComponents;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;

class AutoSyncedFloatComponent implements FloatComponent, AutoSyncedComponent
{
    String key;
    float value = 0;
    public final Entity provider;

    public AutoSyncedFloatComponent(Entity provider)
    {
        this.provider = provider;
        this.key = "AutoSyncedFloatComponent";
    }

    @Override
    public float getValue() {
        return this.value;
    }

    public void setValue(float value) {
        this.value = value;
        //NootNootOriginsComponents.COMPONENTKEY.sync(this.provider);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.value = tag.getFloat(key);
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putFloat(key,this.value);
    }
}