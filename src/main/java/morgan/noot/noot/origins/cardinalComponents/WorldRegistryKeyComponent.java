package morgan.noot.noot.origins.cardinalComponents;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

interface WorldRegistryKeyComponent {
    RegistryKey<World> getValue();
    void setValue(RegistryKey<World> value);
}
