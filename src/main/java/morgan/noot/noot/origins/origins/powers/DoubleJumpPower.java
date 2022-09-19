package morgan.noot.noot.origins.origins.powers;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.ElytraFlightPower;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import morgan.noot.noot.origins.NootNootOrigins;
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public class DoubleJumpPower extends Power {
    private final int doubleJumps;

    public DoubleJumpPower(PowerType<?> type, LivingEntity entity, int doubleJumps) {
        super(type, entity);
        this.doubleJumps = doubleJumps;
    }

    public int getDoubleJumps() {
        return doubleJumps;
    }
}
