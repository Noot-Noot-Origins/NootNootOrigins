package morgan.noot.noot.origins.entity.passive;

import net.minecraft.entity.LivingEntity;

public interface BeeEntityExtension {
    boolean isTamed();
    LivingEntity getOwner();
    boolean canAttackWithOwner(LivingEntity target, LivingEntity owner);
}
