package morgan.noot.noot.origins.cardinalComponents;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;

public class AutoSyncedFloatComponent implements FloatComponentExtension, AutoSyncedComponent
{
    String key;
    ComponentKey<AutoSyncedFloatComponent> componentComponentKey;
    float value = 0;
    public final Object provider;

    public AutoSyncedFloatComponent(Entity provider, String key, ComponentKey<AutoSyncedFloatComponent> componentComponentKey)
    {
        this.provider = provider;
        this.key = key;
        this.componentComponentKey = componentComponentKey;
    }

    public AutoSyncedFloatComponent(Object provider, String key, ComponentKey<AutoSyncedFloatComponent> componentComponentKey, float value)
    {
        this.provider = provider;
        this.key = key;
        this.componentComponentKey = componentComponentKey;
        this.value = value;
    }

    @Override
    public float getValue() {
        return this.value;
    }

    public void setValue(float value) {
        this.value = value;
        componentComponentKey.sync(this.provider);
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