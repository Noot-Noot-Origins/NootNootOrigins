package morgan.noot.noot.origins.entity.projectile;

import com.mojang.logging.LogUtils;
import morgan.noot.noot.origins.entity.LivingEntityExtension;
import morgan.noot.noot.origins.entity.NootNootOriginsEntityType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
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
    public static final float minHookLength = 1f;
    public static final float maxHookLength = 200f;

    @Nullable
    private BlockState inBlockState;

    private static final TrackedData<Float> HOOK_LENGTH = DataTracker.registerData(HookEntity.class, TrackedDataHandlerRegistry.FLOAT);

    public float getHookLength() { return this.hookLength; }

    public void updateHookLength(float hookLength) {
        hookLength = Math.max(hookLength,minHookLength);
        this.hookLength = hookLength;
        this.getDataTracker().set(HOOK_LENGTH, hookLength);
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(HOOK_LENGTH, 40.f);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (HOOK_LENGTH.equals(data)) {
            this.hookLength = this.getDataTracker().get(HOOK_LENGTH);
        }
        super.onTrackedDataSet(data);
    }

    private void updateHookedEntityId(@Nullable Entity entity) {
        this.hookedEntity = entity;
    }

    private void updateHookState( State state )
    {
        this.state = state;
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
    }

    public HookEntity(World world, LivingEntity owner) {
        super(NootNootOriginsEntityType.HookEntityType, world);
    }

    @Override
    public void setOwner(@Nullable Entity entity) {
        super.setOwner(entity);
        if (entity != null) {
            ((LivingEntityExtension)this.getOwner()).setHook(this);
        }
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

        //Min Hook length
        if (hookLength<minHookLength)
        {
            this.updateHookLength(minHookLength);
        }

        //doesnt own this hook
        if (this.getOwner() != null && (((LivingEntityExtension)this.getOwner()).getHook() != this))
        {
            this.discard();
            return;
        }

        //Owner is Dead
        if (this.removeIfInvalid(this.getPlayerOwner()))
        {
            return;
        }

        if (this.state == State.HOOKED_TO_ENTITY) {
            if (this.hookedEntity == null || this.hookedEntity.isRemoved() || this.hookedEntity.world.getRegistryKey() != this.world.getRegistryKey()) {
                this.discard();
                return;
            }
            if ( hookOffset != null )
            {
                this.setPosition(this.hookedEntity.getPos().add(hookOffset));
                //this.velocityDirty = true;
            }
            this.updateOwnersPosition();

            return;
        }

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
            this.updateOwnersPosition();
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
            //this is needed or it crashes the game
            break;
            /*
            if (entityHitResult == null) break;
            hitResult = null;
             */
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

        Vec3d vec3d4 = this.getVelocity();
        this.setVelocity(vec3d4.x, vec3d4.y - (double)0.05f, vec3d4.z);

        this.setPosition(h, j, k);
    }

    private void updateOwnersPosition()
    {
        if (  state != State.UNHOOKED && this.getOwner() != null && this.distanceTo(this.getOwner())>hookLength)
        {
            this.updateHookLength(Math.min(this.distanceTo(this.getOwner()),this.getHookLength()));
            double addedVelocity = (this.getPos().distanceTo(this.getOwner().getPos())-hookLength)*0.05;
            this.getOwner().setVelocity(this.getOwner().getVelocity().add( this.getPos().subtract(this.getOwner().getPos()).normalize().multiply(addedVelocity)));
        }
    }

    private boolean shouldFall() {
        return this.state == State.HOOKED_TO_BLOCK && this.world.isSpaceEmpty(new Box(this.getPos(), this.getPos()).expand(0.06));
    }

    private void fall() {
        this.discard();
    }

    @Override
    public void move(MovementType movementType, Vec3d movement) {
        super.move(movementType, movement);
        if (movementType != MovementType.SELF && this.shouldFall()) {
            this.fall();
        }
    }

    private boolean removeIfInvalid(PlayerEntity player) {
        if ( player == null || player.isRemoved() || !player.isAlive() || this.squaredDistanceTo(player) > (maxHookLength*maxHookLength)) {
            this.discard();
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
        super.onEntityHit(entityHitResult);
        this.updateHookedEntity(entityHitResult);
        this.updateHookLength(this.distanceTo(this.getOwner())+minHookLength);
        this.setVelocity(Vec3d.ZERO);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        this.inBlockState = this.world.getBlockState(blockHitResult.getBlockPos());
        super.onBlockHit(blockHitResult);
        Vec3d vec3d = blockHitResult.getPos().subtract(this.getX(), this.getY(), this.getZ());
        this.setVelocity(vec3d);
        Vec3d vec3d2 = vec3d.normalize().multiply(0.05f);
        this.setPos(this.getX() - vec3d2.x, this.getY() - vec3d2.y, this.getZ() - vec3d2.z);
        this.updateHookState(State.HOOKED_TO_BLOCK);
        this.updateHookLength(this.distanceTo(this.getOwner())+minHookLength);
    }

    @Override
    public boolean canUsePortals() {
        return false;
    }

    @Override
    public boolean shouldRender(double distance) {
        double d = this.getBoundingBox().getAverageSideLength() * 10.0;
        if (Double.isNaN(d)) {
            d = 1.0;
        }
        return distance < (d *= 64.0 * PersistentProjectileEntity.getRenderDistanceMultiplier()) * d;
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

    enum State {
        UNHOOKED,
        HOOKED_TO_ENTITY,
        HOOKED_TO_BLOCK;
    }
}
