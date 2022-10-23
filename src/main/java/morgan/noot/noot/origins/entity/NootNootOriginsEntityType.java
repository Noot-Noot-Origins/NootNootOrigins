package morgan.noot.noot.origins.entity;

import morgan.noot.noot.origins.NootNootOrigins;
import morgan.noot.noot.origins.entity.projectile.HookEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class NootNootOriginsEntityType {
    public static final EntityType<HookEntity> HookEntityType = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier(NootNootOrigins.MODID,"hook_entity"),
            FabricEntityTypeBuilder.<HookEntity>create(SpawnGroup.MISC, HookEntity::new)
                .dimensions(EntityDimensions.fixed(0.25F, 0.25F)) // dimensions in Minecraft units of the projectile
                .trackRangeBlocks(4).trackedUpdateRate(10) // necessary for all thrown projectiles (as it prevents it from breaking, lol)
                .build()
    );

    public static void init(){

    }
}
