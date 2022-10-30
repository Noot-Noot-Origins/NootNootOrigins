package morgan.noot.noot.origins;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import morgan.noot.noot.origins.cardinalComponents.AutoSyncedFloatComponent;
import morgan.noot.noot.origins.cardinalComponents.AutoSyncedVec3dComponent;
import morgan.noot.noot.origins.cardinalComponents.AutoSyncedWorldRegistryKeyComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;



public final class NootNootOriginsComponents implements EntityComponentInitializer, WorldComponentInitializer {
    public static final ComponentKey<AutoSyncedFloatComponent> MOUNT_POSITION =
            ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("nootnoot:mount_position"), AutoSyncedFloatComponent.class);

    public static final ComponentKey<AutoSyncedVec3dComponent> STAR_POSITION =
            ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("nootnoot:star_position"), AutoSyncedVec3dComponent.class);

    public static final ComponentKey<AutoSyncedVec3dComponent> STAR_VELOCITY =
            ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("nootnoot:star_velocity"), AutoSyncedVec3dComponent.class);

    public static final ComponentKey<AutoSyncedWorldRegistryKeyComponent> STAR_WORLD_KEY =
            ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("nootnoot:star_world_key"), AutoSyncedWorldRegistryKeyComponent.class);

    public static final ComponentKey<AutoSyncedFloatComponent> STAR_HEALTH =
            ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("nootnoot:star_health"), AutoSyncedFloatComponent.class);

    public static final ComponentKey<AutoSyncedFloatComponent> WORLD_GRAVITY =
            ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("nootnoot:world_gravity"), AutoSyncedFloatComponent.class);

    public static final ComponentKey<AutoSyncedFloatComponent> ENTITY_GRAVITY =
            ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("nootnoot:entity_gravity"), AutoSyncedFloatComponent.class);


    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(Entity.class, MOUNT_POSITION, entity -> new AutoSyncedFloatComponent(entity, "MountPositionComponent", MOUNT_POSITION));
        registry.registerFor(LivingEntity.class, STAR_POSITION, entity -> new AutoSyncedVec3dComponent(entity, "StarPositionComponent", STAR_POSITION));
        registry.registerFor(LivingEntity.class, STAR_VELOCITY, entity -> new AutoSyncedVec3dComponent(entity, "StarVelocityComponent", STAR_VELOCITY));
        registry.registerFor(LivingEntity.class, STAR_WORLD_KEY, entity -> new AutoSyncedWorldRegistryKeyComponent(entity, "StarWorldRegistryKeyComponent", STAR_WORLD_KEY));
        registry.registerFor(LivingEntity.class, STAR_HEALTH, entity -> new AutoSyncedFloatComponent(entity, "StarHealthComponent", STAR_HEALTH));
        registry.registerFor(Entity.class, ENTITY_GRAVITY, entity -> new AutoSyncedFloatComponent(entity, "EntityGravity", ENTITY_GRAVITY,0.08f));
    }

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(WORLD_GRAVITY, world -> new AutoSyncedFloatComponent(world,"WorldGravity",WORLD_GRAVITY,1));
    }
}