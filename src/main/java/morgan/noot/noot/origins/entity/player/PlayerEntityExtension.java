package morgan.noot.noot.origins.entity.player;

import morgan.noot.noot.origins.NootNootOriginsComponents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface PlayerEntityExtension {
    boolean hasHost();

    @Nullable
    Entity getHost();

    void setHost( Entity entity );

    void infect( LivingEntity entity );

    void unInfect();
}
