package morgan.noot.noot.origins.entity;

import net.minecraft.item.ItemStack;

public interface LivingEntityExtension {
    boolean canEat(ItemStack stack);

    float getDoubleJumps();

    void setDoubleJumps(int doubleJumps);
}
