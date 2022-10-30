package morgan.noot.noot.origins.cardinalComponents;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;

public class AutoSyncedIntComponent implements IntComponent, AutoSyncedComponent {
    String key;
    ComponentKey<AutoSyncedIntComponent> componentComponentKey;
    int value = 0;
    public final Object provider;

    public AutoSyncedIntComponent(Object provider, String key, ComponentKey<AutoSyncedIntComponent> componentComponentKey)
    {
        this.provider = provider;
        this.key = key;
        this.componentComponentKey = componentComponentKey;
    }

    @Override
    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
        componentComponentKey.sync(this.provider);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.value = tag.getInt(key);
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt(key,this.value);
    }
}
