package morgan.noot.noot.origins.mixin.entity.passive;

import io.github.apace100.origins.power.OriginsPowerTypes;
import morgan.noot.noot.origins.NootNootOrigins;
import morgan.noot.noot.origins.entity.ai.goal.BeeAttackWithOwnerGoal;
import morgan.noot.noot.origins.entity.ai.goal.BeeFollowOwnerGoal;
import morgan.noot.noot.origins.entity.ai.goal.BeeTrackOwnerAttackerGoal;
import morgan.noot.noot.origins.entity.passive.BeeEntityExtension;
import morgan.noot.noot.origins.origins.powers.NootNootOriginsPowers;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.AttackWithOwnerGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.TrackOwnerAttackerGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.UUID;

@Mixin(BeeEntity.class)
public abstract class BeeEntityOwnableMixin
        extends AnimalEntity
        implements Angerable,
        Flutterer, BeeEntityExtension {

    private static final TrackedData<Byte> TAMEABLE_FLAGS = DataTracker.registerData(BeeEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(BeeEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private static final TrackedData<Boolean> CAN_BE_TAMED = DataTracker.registerData(BeeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    protected BeeEntityOwnableMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initDataTracker",at = @At("TAIL"))
    public void initDataTracker(CallbackInfo ci) {
        this.dataTracker.startTracking(TAMEABLE_FLAGS, (byte)0);
        this.dataTracker.startTracking(OWNER_UUID, Optional.empty());
        this.dataTracker.startTracking(CAN_BE_TAMED, false);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean("CanBeTamed",this.getCanBeTamed());
        if (this.getOwnerUuid() != null) {
            nbt.putUuid("Owner", this.getOwnerUuid());
        }
    }

    @Inject(method = "readCustomDataFromNbt",at = @At("TAIL"))
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        UUID uUID;
        this.setCanBeTamed(nbt.getBoolean("CanBeTamed"));
        if (nbt.containsUuid("Owner")) {
            uUID = nbt.getUuid("Owner");
        } else {
            String string = nbt.getString("Owner");
            uUID = ServerConfigHandler.getPlayerUuidByName(this.getServer(), string);
        }
        if (uUID != null) {
            try {
                this.setOwnerUuid(uUID);
                this.setTamed(true);
            }
            catch (Throwable throwable) {
                this.setTamed(false);
            }
        }
    }

    public boolean getCanBeTamed() {
        return this.dataTracker.get(CAN_BE_TAMED);
    }

    public void setCanBeTamed(boolean canBeTamed) {
        this.dataTracker.set(CAN_BE_TAMED, canBeTamed);
    }

    @Override
    public boolean canBeLeashedBy(PlayerEntity player) {
        return !this.isLeashed();
    }

    public boolean isTamed() {
        return (this.dataTracker.get(TAMEABLE_FLAGS) & 4) != 0;
    }

    public void setTamed(boolean tamed) {
        byte b = this.dataTracker.get(TAMEABLE_FLAGS);
        if (tamed) {
            this.dataTracker.set(TAMEABLE_FLAGS, (byte)(b | 4));
        } else {
            this.dataTracker.set(TAMEABLE_FLAGS, (byte)(b & 0xFFFFFFFB));
        }
    }

    @Nullable
    public UUID getOwnerUuid() {
        return this.dataTracker.get(OWNER_UUID).orElse(null);
    }

    public void setOwnerUuid(@Nullable UUID uuid) {
        this.dataTracker.set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    public void setOwner(PlayerEntity player) {
        this.setTamed(true);
        this.setOwnerUuid(player.getUuid());
        if (player instanceof ServerPlayerEntity) {
            Criteria.TAME_ANIMAL.trigger((ServerPlayerEntity)player, this);
        }
    }

    @Nullable
    public LivingEntity getOwner() {
        try {
            UUID uUID = this.getOwnerUuid();
            if (uUID == null) {
                return null;
            }
            return this.world.getPlayerByUuid(uUID);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            return null;
        }
    }

    @Override
    public boolean canTarget(LivingEntity target) {
        if (this.isOwner(target)) {
            return false;
        }
        return super.canTarget(target);
    }

    public boolean isOwner(LivingEntity entity) {
        return entity == this.getOwner();
    }

    public boolean canAttackWithOwner(LivingEntity target, LivingEntity owner) {
        return true;
    }

    @Override
    public AbstractTeam getScoreboardTeam() {
        LivingEntity livingEntity;
        if (this.isTamed() && (livingEntity = this.getOwner()) != null) {
            return livingEntity.getScoreboardTeam();
        }
        return super.getScoreboardTeam();
    }

    @Override
    public boolean isTeammate(Entity other) {
        if (this.isTamed()) {
            LivingEntity livingEntity = this.getOwner();
            if (other == livingEntity) {
                return true;
            }
            if (livingEntity != null) {
                return livingEntity.isTeammate(other);
            }
        }
        return super.isTeammate(other);
    }

    @Inject(method = "initGoals",at = @At("HEAD"))
    public void initGoals(CallbackInfo ci){
        this.goalSelector.add(3, new BeeFollowOwnerGoal((BeeEntity)(Object)this, 1.0, 16.0f, 2.0f, false));
        this.targetSelector.add(1, new BeeTrackOwnerAttackerGoal((BeeEntity)(Object)this));
        this.targetSelector.add(2, new BeeAttackWithOwnerGoal((BeeEntity)(Object)this));
    }

    public void findClosestOwner() {
        if (this.getCanBeTamed()) {
            double distance = -1.0;
            PlayerEntity player = null;
            for (PlayerEntity player2 : this.world.getPlayers()) {
                if (!NootNootOriginsPowers.BEE_TAME.isActive(player2)) continue;
                double e = player2.squaredDistanceTo(this.getX(), this.getEyeY(), this.getZ());
                if (distance != -1.0 && !(e < distance)) continue;
                if (distance > 6) continue;
                distance = e;
                player = player2;
            }

            if(player!=null) this.setOwner(player);
        }
    }

    @Inject(method = "tick",at = @At("TAIL"))
    public void tick(CallbackInfo ci){
        this.findClosestOwner();
    }
}
