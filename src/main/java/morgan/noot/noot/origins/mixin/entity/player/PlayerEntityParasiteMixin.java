package morgan.noot.noot.origins.mixin.entity.player;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import morgan.noot.noot.origins.NootNootOrigins;
import morgan.noot.noot.origins.NootNootOriginsComponents;
import morgan.noot.noot.origins.entity.player.PlayerEntityExtension;
import morgan.noot.noot.origins.network.packet.NootNootOriginsPackets;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityParasiteMixin extends LivingEntity implements PlayerEntityExtension {
    protected PlayerEntityParasiteMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    LivingEntity hostEntity;

    public boolean hasHost() {
        return this.getHost() != null && this.getHost().isAlive();
    }

    @Nullable
    public LivingEntity getHost() {
        UUID uuid = this.getComponent(NootNootOriginsComponents.ENTITY_HOST_UUID).getValue();
        if (this.world instanceof ServerWorld)
        {
            Entity entity = ((ServerWorld) this.world).getEntity(uuid);
            if (entity instanceof LivingEntity)
            {
                this.hostEntity = (LivingEntity) entity;
            }
        }

        return this.hostEntity;
    }

    public void setHost(LivingEntity entity) {
        UUID uuid;
        if (entity == null) {
            uuid = null;
        } else {
            uuid = entity.getUuid();
        }

        this.getComponent(NootNootOriginsComponents.ENTITY_HOST_UUID).setValue(uuid);
        this.hostEntity = entity;
    }

    @Override
    public double getAttributeValue(EntityAttribute attribute) {
        if (this.hasHost())
        {
            if ( this.getHost().getAttributes().hasAttribute(attribute))
            return this.getHost().getAttributes().getValue(attribute);
        }
        return this.getAttributes().getValue(attribute);
    }

    public void infect(LivingEntity entity) {
        if (entity == null || !entity.isAlive()) return;


        this.setYaw(entity.getYaw());
        this.setPitch(entity.getPitch());
        this.startRiding(entity,true);


        if (!this.world.isClient) {
            if (((ServerPlayerEntity) (Object) this).networkHandler != null) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeInt(entity.getId());
                ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, NootNootOriginsPackets.ON_INFECTED_ENTITY_PACKET_ID, buf);
            }
        }

        this.hostEntity = entity;
    }

    public void unInfect() {
        this.stopRiding();
        this.setHost(null);

        if (!this.world.isClient) {
            if (((ServerPlayerEntity) (Object) this).networkHandler != null) {
                PacketByteBuf buf = PacketByteBufs.create();
                ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, NootNootOriginsPackets.ON_UNINFECTED_ENTITY_PACKET_ID, buf);
            }
        }
    }

    @Inject(method = "shouldDismount", at = @At("HEAD"), cancellable = true)
    public void shouldDismount(CallbackInfoReturnable<Boolean> cir) {
        if (this.hasHost()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        NootNootOrigins.LOGGER.info("vehc"+String.valueOf(this.getVehicle()));
        NootNootOrigins.LOGGER.info("host"+String.valueOf(this.getHost()));

        LivingEntity host = (LivingEntity) this.getHost();
        if (this.getHost() != null )
        {
            if (!this.getHost().isAlive()) {
                this.unInfect();
            }
            this.setHealth(host.getHealth());
        }
        if ( this.hasHost() && (this.getVehicle() != this.getHost())) {
            this.startRiding(this.getHost(),true);
        }
    }
    
    @ModifyReturnValue(method = "canTakeDamage", at = @At("RETURN"))
    public boolean modifyParasiteCanTakeDamage(boolean original) {
        return original && !this.hasHost();
    }
}