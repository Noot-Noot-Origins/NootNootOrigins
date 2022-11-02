package morgan.noot.noot.origins.cardinalComponents;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.UUID;

public class AutoSyncedUUIDComponent implements UUIDComponent, AutoSyncedComponent {
    String key;
    ComponentKey<AutoSyncedUUIDComponent> componentComponentKey;
    UUID value = null;
    public final Object provider;

    public AutoSyncedUUIDComponent(Object provider, String key, ComponentKey<AutoSyncedUUIDComponent> componentComponentKey)
    {
        this.provider = provider;
        this.key = key;
        this.componentComponentKey = componentComponentKey;
    }

    public AutoSyncedUUIDComponent(Object provider, String key, ComponentKey<AutoSyncedUUIDComponent> componentComponentKey, UUID value)
    {
        this.provider = provider;
        this.key = key;
        this.componentComponentKey = componentComponentKey;
        this.value = value;
    }

    @Override
    public UUID getValue() {
        return this.value;
    }

    public void setValue( UUID value) {
        this.value = value;
        componentComponentKey.sync(this.provider);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        if (tag.contains(key))
        {
            this.value = tag.getUuid(key);
        }
        else
        {
            this.value = null;
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        if (value != null)
        {
            tag.putUuid(key,this.value);
        }
    }
}
