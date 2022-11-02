package morgan.noot.noot.origins.mixin.entity.player;

import morgan.noot.noot.origins.NootNootOrigins;
import morgan.noot.noot.origins.NootNootOriginsComponents;
import morgan.noot.noot.origins.entity.player.PlayerEntityExtension;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityParasite extends LivingEntity implements PlayerEntityExtension {
    protected PlayerEntityParasite(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    LivingEntity hostEntity;

    public boolean hasHost(){
        UUID uuid = this.getComponent(NootNootOriginsComponents.ENTITY_HOST_UUID).getValue();
        Entity entity;
        if (this.world instanceof ServerWorld && (entity = ((ServerWorld)this.world).getEntity(uuid))instanceof LivingEntity)
        {
            this.hostEntity = (LivingEntity) entity;
        }
        if (this.hostEntity == null) return false;
        return this.hostEntity.isAlive();
    }

    @Nullable
    public Entity getHost(){
        UUID uuid = this.getComponent(NootNootOriginsComponents.ENTITY_HOST_UUID).getValue();
        Entity entity;
        if (this.world instanceof ServerWorld && (entity = ((ServerWorld)this.world).getEntity(uuid))instanceof LivingEntity)
        {
            this.hostEntity = (LivingEntity) entity;
        }
        return this.hostEntity;
    }

    public void setHost( Entity entity ){
        UUID uuid;
        if ( entity == null )
        {
            uuid = null;
        }
        else
        {
            uuid = entity.getUuid();
        }
        this.getComponent(NootNootOriginsComponents.ENTITY_HOST_UUID).setValue(uuid);
    }

    public void infect( LivingEntity entity ) {
        if (entity == null || !entity.isAlive()) return;
        NootNootOrigins.LOGGER.info("start riding");
        boolean riding = false;
        if (!this.world.isClient) {
            riding = this.startRiding(entity);
        }

        NootNootOrigins.LOGGER.info(String.valueOf(riding));
    }

    @Inject(method = "shouldDismount",at = @At("HEAD"),cancellable = true)
    public void shouldDismount(CallbackInfoReturnable<Boolean> cir)
    {
        if (this.hasHost())
        {
            cir.setReturnValue(false);
        }
    }

}
