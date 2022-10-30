package morgan.noot.noot.origins.cardinalComponents;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.util.math.Vec3d;

public interface IntComponent extends ComponentV3 {
    int getValue();
    void setValue(int value);
}
