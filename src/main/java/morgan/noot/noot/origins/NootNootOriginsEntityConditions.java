package morgan.noot.noot.origins;

import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeReference;
import io.github.apace100.origins.Origins;
import net.minecraft.util.Identifier;

public class NootNootOriginsEntityConditions {
    public static final PowerType<?> CAN_EAT_FUEL = new PowerTypeReference<>(new Identifier(NootNootOrigins.MODID, "can_eat_fuel"));
}
