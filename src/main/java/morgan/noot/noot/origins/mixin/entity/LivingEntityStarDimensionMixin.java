package morgan.noot.noot.origins.mixin.entity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import morgan.noot.noot.origins.NootNootOriginsComponents;
import morgan.noot.noot.origins.entity.LivingEntityExtension;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(LivingEntity.class)
public abstract class LivingEntityStarDimensionMixin extends Entity implements LivingEntityExtension {

    @Shadow public abstract float getHealth();

    @Shadow public abstract void setHealth(float health);

    @Shadow public abstract boolean teleport(double x, double y, double z, boolean particleEffects);

    @Shadow public abstract float getMaxHealth();

    @Shadow public abstract boolean clearStatusEffects();

    @Shadow public abstract boolean addStatusEffect(StatusEffectInstance effect);

    protected LivingEntityStarDimensionMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    public boolean teleportToDimension(ServerWorld world, double x, double y, double z, float yaw, float pitch) {
        LivingEntity entity = (LivingEntity) (Object)this;
        BlockPos blockPos = new BlockPos(x, y, z);
        if (!World.isValid(blockPos)) {
            return false;
        }
        float f = MathHelper.wrapDegrees(yaw);
        float g = MathHelper.wrapDegrees(pitch);
        if (entity instanceof ServerPlayerEntity) {
            ChunkPos chunkPos = new ChunkPos(new BlockPos(x, y, z));
            world.getChunkManager().addTicket(ChunkTicketType.POST_TELEPORT, chunkPos, 1, entity.getId());
            entity.stopRiding();
            if (((ServerPlayerEntity)entity).isSleeping()) {
                ((ServerPlayerEntity)entity).wakeUp(true, true);
            }
            if (world == entity.world) {
                ((ServerPlayerEntity)entity).networkHandler.requestTeleport(x, y, z, f, g);
            } else {
                ((ServerPlayerEntity)entity).teleport(world, x, y, z, f, g);
            }
            entity.setHeadYaw(f);
        } else {
            float h = MathHelper.clamp(g, -90.0f, 90.0f);
            if (world == entity.world) {
                entity.refreshPositionAndAngles(x, y, z, f, h);
                entity.setHeadYaw(f);
            } else {
                entity.detach();
                LivingEntity entity2 = entity;
                entity = (LivingEntity) entity2.getType().create(world);
                if (entity != null) {
                    entity.copyFrom(entity2);
                    entity.refreshPositionAndAngles(x, y, z, f, h);
                    entity.setHeadYaw(f);
                    entity2.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
                    world.onDimensionChanged(entity);
                } else {
                    return false;
                }
            }
        }
        if (!entity.isFallFlying()) {
            entity.setVelocity(entity.getVelocity().multiply(1.0, 0.0, 1.0));
            entity.setOnGround(true);
        }

        if (entity instanceof PathAwareEntity) {
            ((PathAwareEntity)entity).getNavigation().stop();
        }

        return true;
    }

    public void saveStarDimensionInfo() {
        this.getComponent(NootNootOriginsComponents.STAR_POSITION).setValue(this.getPos());
        this.getComponent(NootNootOriginsComponents.STAR_VELOCITY).setValue(this.getVelocity());
        this.getComponent(NootNootOriginsComponents.STAR_HEALTH).setValue(this.getHealth());
        this.getComponent(NootNootOriginsComponents.STAR_WORLD_KEY).setValue(this.world.getRegistryKey());
    }

    public void loadStarDimensionInfo() {
        this.setVelocity(this.getComponent(NootNootOriginsComponents.STAR_VELOCITY).getValue());
        this.setHealth(this.getComponent(NootNootOriginsComponents.STAR_HEALTH).getValue());
    }

    public boolean enterStarDimension()
    {
        saveStarDimensionInfo();

        this.fallDistance = 0;
        this.setHealth(this.getMaxHealth());

        ServerWorld world = this.getServer().getWorld(RegistryKey.of(Registry.WORLD_KEY, new Identifier("nootnoot","star")));
        if (!teleportToDimension(world, 0.5,64,0.5, this.getYaw(), this.getPitch()))
        {
            return false;
        }

        generateStarDimensionPlatform();
        return true;
    }

    public boolean leaveStarDimension()
    {
        loadStarDimensionInfo();

        this.clearStatusEffects();
        this.setOnFire(false);
        this.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 10));
        this.fallDistance = 0;

        Vec3d position = this.getComponent(NootNootOriginsComponents.STAR_POSITION).getValue();
        ServerWorld world = this.getServer().getWorld(this.getComponent(NootNootOriginsComponents.STAR_WORLD_KEY).getValue());

        return teleportToDimension(world, position.x, position.y, position.z, this.getYaw(), this.getPitch());
    }

    public void generateStarDimensionPlatform()
    {
        ServerWorld starDimension = this.getServer().getWorld(RegistryKey.of(Registry.WORLD_KEY, new Identifier("nootnoot","star")));
        int i = 0;
        int j = 62;
        int k = 0;
        BlockPos.iterate(i - 2, j + 1, k - 2, i + 2, j + 3, k + 2).forEach(pos -> starDimension.breakBlock((BlockPos)pos,true));
        BlockPos.iterate(i - 2, j, k - 2, i + 2, j, k + 2).forEach(pos -> starDimension.setBlockState((BlockPos)pos, Blocks.OBSIDIAN.getDefaultState()));
    }

    @Inject(method = "tryUseTotem", at = @At("HEAD"), cancellable = true)
    private void tryUseTotem(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (Objects.equals(this.world.getRegistryKey().getValue().toString(), "nootnoot:star"))
        {
            this.setHealth(1.0f);
            leaveStarDimension();
            cir.setReturnValue(true);
        }
    }
}