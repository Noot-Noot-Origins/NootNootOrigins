package morgan.noot.noot.origins;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import morgan.noot.noot.origins.cardinalComponents.MountPositionComponent;
import morgan.noot.noot.origins.cardinalComponents.StarPositionComponent;
import morgan.noot.noot.origins.cardinalComponents.StarWorldRegistryKeyComponent;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;



public final class NootNootOriginsComponents implements EntityComponentInitializer {
    public static final ComponentKey<MountPositionComponent> MOUNT_POSITION =
            ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("nootnoot:mount_position"), MountPositionComponent.class);

    public static final ComponentKey<StarPositionComponent> STAR_POSITION =
            ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("nootnoot:star_position"), StarPositionComponent.class);

    public static final ComponentKey<StarWorldRegistryKeyComponent> STAR_WORLD_KEY =
            ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("nootnoot:star_world_key"), StarWorldRegistryKeyComponent.class);


    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(Entity.class, MOUNT_POSITION, MountPositionComponent::new);
        registry.registerForPlayers(STAR_POSITION , StarPositionComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerForPlayers(STAR_WORLD_KEY , StarWorldRegistryKeyComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
    }
}