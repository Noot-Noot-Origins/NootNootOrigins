package morgan.noot.noot.origins.origins.powers;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import net.minecraft.entity.LivingEntity;

public class ChangeTradePrice extends Power {
    private final int Amount;

    public ChangeTradePrice(PowerType<?> type, LivingEntity entity, int Amount) {
        super(type, entity);
        this.Amount = Amount;
    }

    public int getAmount() {
        return Amount;
    }
}
