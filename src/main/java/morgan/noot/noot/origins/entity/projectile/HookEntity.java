package morgan.noot.noot.origins.entity.projectile;

import com.mojang.datafixers.types.templates.Hook;
import com.mojang.logging.LogUtils;
import morgan.noot.noot.origins.NootNootOrigins;
import morgan.noot.noot.origins.entity.LivingEntityExtension;
import morgan.noot.noot.origins.entity.NootNootOriginsEntityType;
import morgan.noot.noot.origins.network.packet.NootNootOriginsPacketsInit;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.impl.game.minecraft.Hooks;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class HookEntity extends ProjectileEntity {
    private static final Logger field_36336 = LogUtils.getLogger();
    public State state = State.UNHOOKED;
    public Vec3d hookOffset = null;
    public Entity hookedEntity = null;
    public float hookLength = 40;
    public static final float minHookLength = 2.5f;
    @Nullable
    private BlockState inBlockState;

    private static final TrackedDataHandler<State> STATE = TrackedDataHandler.ofEnum(State.class);
    static {
        TrackedDataHandlerRegistry.register(STATE);
    }
    private static final TrackedData<Integer> HOOK_ENTITY_ID = DataTracker.registerData(HookEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> HOOK_LENGTH = DataTracker.registerData(HookEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<State> HOOK_STATE = DataTracker.registerData(HookEntity.class, STATE);

    public float getHookLength() { return this.hookLength; }

    public void updateHookLength(float hookLength) {
        this.hookLength = hookLength;
        this.getDataTracker().set(HOOK_LENGTH, hookLength);
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(HOOK_ENTITY_ID, 0);
        this.getDataTracker().startTracking(HOOK_LENGTH, 40.f);
        this.getDataTracker().startTracking(HOOK_STATE, State.UNHOOKED);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (HOOK_ENTITY_ID.equals(data)) {
            int i = this.getDataTracker().get(HOOK_ENTITY_ID);
            this.hookedEntity = i > 0 ? this.world.getEntityById(i - 1) : null;
        }
        if (HOOK_LENGTH.equals(data)) {
            this.hookLength = this.getDataTracker().get(HOOK_LENGTH);
        }
        if (HOOK_STATE.equals(data)) {
            this.state = this.getDataTracker().get(HOOK_STATE);
        }
        super.onTrackedDataSet(data);
    }

    private void updateHookedEntityId(@Nullable Entity entity) {
        this.hookedEntity = entity;
        this.getDataTracker().set(HOOK_ENTITY_ID, entity == null ? 0 : entity.getId() + 1);
    }

    private void updateHookState( State state )
    {
        this.state = state;
        this.getDataTracker().set(HOOK_STATE, state);
    }

    public HookEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
        if (this.getOwner() == null)
        {
            double d = -1.0;
            PlayerEntity playerEntity = null;
            for (PlayerEntity playerEntity2 : this.world.getPlayers()) {
                double e = playerEntity2.squaredDistanceTo(this.getX(), this.getY(), this.getZ());
                if (d != -1.0 && !(e < d)) continue;
                d = e;
                playerEntity = playerEntity2;
            }
            this.setOwner(playerEntity);
        }
        ((LivingEntityExtension)this.getOwner()).setHook(this);
    }

    public HookEntity(World world, LivingEntity owner) {
        super(NootNootOriginsEntityType.HookEntityType, world);
    }

    @Nullable
    protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
        return ProjectileUtil.getEntityCollision(this.world, this, currentPosition, nextPosition, this.getBoundingBox().stretch(this.getVelocity()).expand(1.0), this::canHit);
    }

    @Override
    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
        this.setPosition(x, y, z);
        this.setRotation(yaw, pitch);
    }

    @Override
    public void tick() {
        Vec3d vec3d2;
        VoxelShape voxelShape;
        BlockPos blockPos;
        BlockState blockState;
        super.tick();
        Vec3d vec3d = this.getVelocity();
        if (this.prevPitch == 0.0f && this.prevYaw == 0.0f) {
            double d = vec3d.horizontalLength();
            this.setYaw((float)(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875));
            this.setPitch((float)(MathHelper.atan2(vec3d.y, d) * 57.2957763671875));
            this.prevYaw = this.getYaw();
            this.prevPitch = this.getPitch();
        }
        if (!((blockState = this.world.getBlockState(blockPos = this.getBlockPos())).isAir()  || (voxelShape = blockState.getCollisionShape(this.world, blockPos)).isEmpty())) {
            vec3d2 = this.getPos();
            for (Box box : voxelShape.getBoundingBoxes()) {
                if (!box.offset(blockPos).contains(vec3d2)) continue;
                this.updateHookState(State.HOOKED_TO_BLOCK);
                break;
            }
        }
        if (this.state == State.HOOKED_TO_BLOCK) {
            if (this.inBlockState != blockState && this.shouldFall()) {
                this.fall();
            }
            return;
        }
        Vec3d vec3d3 = this.getPos();
        HitResult hitResult = this.world.raycast(new RaycastContext(vec3d3, vec3d2 = vec3d3.add(vec3d), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
        if (hitResult.getType() != HitResult.Type.MISS) {
            vec3d2 = hitResult.getPos();
        }
        while (!this.isRemoved()) {
            EntityHitResult entityHitResult = this.getEntityCollision(vec3d3, vec3d2);
            if (entityHitResult != null) {
                hitResult = entityHitResult;
            }
            if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
                Entity entity = ((EntityHitResult)hitResult).getEntity();
                Entity entity2 = this.getOwner();
                if (entity instanceof PlayerEntity && entity2 instanceof PlayerEntity && !((PlayerEntity)entity2).shouldDamagePlayer((PlayerEntity)entity)) {
                    hitResult = null;
                    entityHitResult = null;
                }
            }
            if (hitResult != null) {
                this.onCollision(hitResult);
                this.velocityDirty = true;
            }
            if (entityHitResult == null) break;
            hitResult = null;
        }
        vec3d = this.getVelocity();
        double e = vec3d.x;
        double f = vec3d.y;
        double g = vec3d.z;

        double h = this.getX() + e;
        double j = this.getY() + f;
        double k = this.getZ() + g;
        double l = vec3d.horizontalLength();

        this.setYaw((float)(MathHelper.atan2(e, g) * 57.2957763671875));

        this.setPitch((float)(MathHelper.atan2(f, l) * 57.2957763671875));
        this.setPitch(PersistentProjectileEntity.updateRotation(this.prevPitch, this.getPitch()));
        this.setYaw(PersistentProjectileEntity.updateRotation(this.prevYaw, this.getYaw()));
        float m = 0.99f;

        this.setVelocity(vec3d.multiply(m));

        Vec3d vec3d4 = this.getVelocity();
        this.setVelocity(vec3d4.x, vec3d4.y - (double)0.05f, vec3d4.z);

        this.setPosition(h, j, k);
        this.checkBlockCollision();
    }

    private boolean shouldFall() {
        return this.state == State.HOOKED_TO_BLOCK && this.world.isSpaceEmpty(new Box(this.getPos(), this.getPos()).expand(0.06));
    }

    private void fall() {
        NootNootOrigins.LOGGER.info("UNHOOKED");
        this.updateHookState(State.UNHOOKED);
    }

    @Override
    public void move(MovementType movementType, Vec3d movement) {
        super.move(movementType, movement);
        if (movementType != MovementType.SELF && this.shouldFall()) {
            this.fall();
        }
    }

    /*
    @Override
    public void tick() {
        super.tick();
        PlayerEntity playerEntity = this.getPlayerOwner();

        if (hookLength<minHookLength)
        {
            this.updateHookLength(minHookLength);
        }

        if (this.getOwner() != null && ((LivingEntityExtension)this.getOwner()).getHook() != this)
        {
            this.discard();
            return;
        }
        if ( this.state == State.HOOKED_TO_ENTITY && (this.hookedEntity.isRemoved() || this.hookedEntity.world.getRegistryKey() != this.world.getRegistryKey())) {
            this.discard();
            NootNootOrigins.LOGGER.info("HOOKED ENTITY GONE");
            return;
        }

        BlockPos blockPos;
        BlockState blockState;
        VoxelShape voxelShape;
        Vec3d vec3d2;
        if (!((blockState = this.world.getBlockState(blockPos = this.getBlockPos())).isAir() || (voxelShape = blockState.getCollisionShape(this.world, blockPos)).isEmpty())) {
            vec3d2 = this.getPos();
            for (Box box : voxelShape.getBoundingBoxes()) {
                if (!box.offset(blockPos).contains(vec3d2)) continue;
                this.updateHookState(State.HOOKED_TO_BLOCK);
                break;
            }
        }

        if (this.inBlockState != blockState && this.shouldFall()) {
            //this.discard();
            NootNootOrigins.LOGGER.info("HOOKED BLOCK GONE");
            return;
        }

        if (!this.world.isClient && this.removeIfInvalid(playerEntity)) {
            return;
        }


        if ( this.state == State.UNHOOKED ) {
            this.setVelocity(this.getVelocity().add(0.0, -0.03, 0.0));
            this.checkForCollision();
        }

        if (this.state == State.HOOKED_TO_ENTITY){
            this.setPosition(this.hookedEntity.getPos().add(hookOffset));
        }
        else if (this.state == State.UNHOOKED)
        {
            this.setPosition(this.getPos().add(this.getVelocity()));
        }

        if (  state != State.UNHOOKED && this.getOwner() != null && this.distanceTo(this.getOwner())>hookLength)
        {
            double addedVelocity = (this.getPos().distanceTo(this.getOwner().getPos())-hookLength)*0.05;
            this.getOwner().setVelocity(this.getOwner().getVelocity().add( this.getPos().subtract(this.getOwner().getPos()).normalize().multiply(addedVelocity)));
        }
    }
     */

    private boolean removeIfInvalid(PlayerEntity player) {
        if ( player == null || player.isRemoved() || !player.isAlive() || this.squaredDistanceTo(player) > 4096.0) {
            //this.discard();
            return true;
        }
        return false;
    }

    private Vec3d calculateHookOffset( Vec3d hitPosition, Vec3d hitEntityPosition ) {
        return hitPosition.subtract(hitEntityPosition);
    }

    private void updateHookedEntity(EntityHitResult entityHitResult) {
        this.updateHookState(State.HOOKED_TO_ENTITY);
        this.updateHookedEntityId(entityHitResult.getEntity());

        float delta;
        for ( delta = 0; delta < 1 ; delta+=0.1  )
        {
            Vec3d vec3d = this.getPos().lerp(this.getPos().add(this.getVelocity()),delta);
            if (ProjectileUtil.getEntityCollision(world, this, vec3d, vec3d, this.getBoundingBox().expand(1.0), this::canHit) != null )
            {
                break;
            }
        }
        this.setPosition(this.getPos().add(this.getVelocity().multiply(delta)));

        this.hookOffset = calculateHookOffset( this.getPos(), this.hookedEntity.getPos());
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        NootNootOrigins.LOGGER.info("HIT ENTITY");
        super.onEntityHit(entityHitResult);
        this.updateHookedEntity(entityHitResult);
        this.updateHookLength(Math.max(this.distanceTo(this.getOwner()),minHookLength));
        this.setVelocity(Vec3d.ZERO);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        NootNootOrigins.LOGGER.info("HIT BLOCK");
        this.inBlockState = this.world.getBlockState(blockHitResult.getBlockPos());
        super.onBlockHit(blockHitResult);
        Vec3d vec3d = blockHitResult.getPos().subtract(this.getX(), this.getY(), this.getZ());
        this.setVelocity(vec3d);
        Vec3d vec3d2 = vec3d.normalize().multiply(0.05f);
        this.setPos(this.getX() - vec3d2.x, this.getY() - vec3d2.y, this.getZ() - vec3d2.z);
        this.updateHookState(State.HOOKED_TO_BLOCK);
        this.updateHookLength(this.distanceTo(this.getOwner()));
    }

    @Override
    public boolean canUsePortals() {
        return false;
    }

    @Nullable
    public PlayerEntity getPlayerOwner() {
        Entity entity = this.getOwner();
        return entity instanceof PlayerEntity ? (PlayerEntity)entity : null;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        Entity entity = this.getOwner();
        return new EntitySpawnS2CPacket(this, entity == null ? this.getId() : entity.getId());
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        if (this.getPlayerOwner() == null) {
            int i = packet.getEntityData();
            field_36336.error("Failed to recreate hook on client. {} (id: {}) is not a valid owner.", (Object)this.world.getEntityById(i), (Object)i);
            this.kill();
        }
    }

    static enum State {
        UNHOOKED,
        HOOKED_TO_ENTITY,
        HOOKED_TO_BLOCK;
    }
}
