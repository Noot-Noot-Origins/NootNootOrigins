package morgan.noot.noot.origins.cardinalComponents;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import morgan.noot.noot.origins.NootNootOriginsComponents;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class AutoSyncedWorldRegistryKeyComponent implements WorldRegistryKeyComponent, AutoSyncedComponent {
    String key;
    ComponentKey<AutoSyncedWorldRegistryKeyComponent> componentComponentKey;
    RegistryKey<World> value = World.OVERWORLD;
    public final Object provider;

    public AutoSyncedWorldRegistryKeyComponent(Object provider, String key, ComponentKey<AutoSyncedWorldRegistryKeyComponent> componentComponentKey)
    {
        this.provider = provider;
        this.key = key;
        this.componentComponentKey = componentComponentKey;
    }

    @Override
    public RegistryKey<World> getValue() {
        return this.value;
    }

    public void setValue(RegistryKey<World> value) {
        this.value = value;
        componentComponentKey.sync(this.provider);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.value = RegistryKey.of(Registry.WORLD_KEY, new Identifier(tag.getString(key+"Namespace"),tag.getString(key+"Path")));
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putString(key+"Namespace",this.value.getValue().getNamespace());
        tag.putString(key+"Path",this.value.getValue().getPath());
    }
}
