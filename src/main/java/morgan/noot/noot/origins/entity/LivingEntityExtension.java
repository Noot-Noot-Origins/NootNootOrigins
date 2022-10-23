package morgan.noot.noot.origins.entity;

import morgan.noot.noot.origins.entity.projectile.HookEntity;
import net.minecraft.item.ItemStack;

public interface LivingEntityExtension {
    boolean canEat(ItemStack stack);

    float getDoubleJumps();

    void setDoubleJumps(int doubleJumps);

    boolean hasHook();

    HookEntity getHook();

    void setHook(HookEntity hook);
}
