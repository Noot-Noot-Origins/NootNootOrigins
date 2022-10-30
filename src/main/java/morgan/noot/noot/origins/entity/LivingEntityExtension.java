package morgan.noot.noot.origins.entity;

import morgan.noot.noot.origins.entity.projectile.HookEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public interface LivingEntityExtension {
    boolean canEat(ItemStack stack);

    float getDoubleJumps();

    void setDoubleJumps(int doubleJumps);

    boolean hasHook();

    HookEntity getHook();

    void setHook(HookEntity hook);

    boolean enterStarDimension();

    boolean leaveStarDimension();

    boolean teleportToDimension(ServerWorld world, double x, double y, double z, float yaw, float pitch);
}
