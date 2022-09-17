package morgan.noot.noot.origins;

import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeReference;
import io.github.apace100.origins.Origins;
import net.minecraft.util.Identifier;

public class NootNootOriginsEntityConditions {
    public static final PowerType<?> PLANE_CAN_EAT_FUEL = new PowerTypeReference<>(new Identifier(NootNootOrigins.MODID, "plane_can_eat_fuel"));
    public static final PowerType<?> DOUBLE_JUMP = new PowerTypeReference<>(new Identifier(NootNootOrigins.MODID, "double_jump"));
}
