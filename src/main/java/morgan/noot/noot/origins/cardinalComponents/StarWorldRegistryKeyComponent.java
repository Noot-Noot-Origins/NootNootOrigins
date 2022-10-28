package morgan.noot.noot.origins.cardinalComponents;

import morgan.noot.noot.origins.NootNootOriginsComponents;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class StarWorldRegistryKeyComponent extends AutoSyncedWorldRegistryKeyComponent{
    public StarWorldRegistryKeyComponent(Entity provider) {
        super(provider);
        this.key = "StarWorldRegistryKeyComponent";
    }

    public void setValue(RegistryKey<World> value) {
        this.value = value;
        NootNootOriginsComponents.STAR_WORLD_KEY.sync(this.provider);
    }
}
