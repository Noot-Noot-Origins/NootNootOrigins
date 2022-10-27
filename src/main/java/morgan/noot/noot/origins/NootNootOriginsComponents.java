package morgan.noot.noot.origins;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import morgan.noot.noot.origins.cardinalComponents.MountPositionComponent;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;



public final class NootNootOriginsComponents implements EntityComponentInitializer {
    public static final ComponentKey<MountPositionComponent> MOUNT_POSITION =
            ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("nootnoot:mount_position"), MountPositionComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(Entity.class, MOUNT_POSITION, MountPositionComponent::new);
    }
}