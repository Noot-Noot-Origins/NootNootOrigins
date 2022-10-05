package morgan.noot.noot.origins.entity.projectile;

import morgan.noot.noot.origins.NootNootOrigins;
import morgan.noot.noot.origins.entity.NootNootOriginsEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class HookEntity extends ProjectileEntity {
    public State state = State.UNHOOKED;
    public Vec3d hookOffset = null;
    public Entity hookedEntity = null;

    public HookEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public HookEntity(World world, LivingEntity owner) {
        super(NootNootOriginsEntityType.HookEntityType, world);
        if (owner == null)
        {
            owner = (this.world.getClosestPlayer(this, 100));
        }
        this.setOwner(owner);
        float f = owner.getPitch();
        float g = owner.getYaw();
        float h = MathHelper.cos(-g * ((float)Math.PI / 180) - (float)Math.PI);
        float i = MathHelper.sin(-g * ((float)Math.PI / 180) - (float)Math.PI);
        float j = -MathHelper.cos(-f * ((float)Math.PI / 180));
        float k = MathHelper.sin(-f * ((float)Math.PI / 180));
        double d = owner.getX() - (double)i * 0.3;
        double e = owner.getEyeY();
        double l = owner.getZ() - (double)h * 0.3;
        this.refreshPositionAndAngles(d, e, l, g, f);
        Vec3d vec3d = new Vec3d(-i, MathHelper.clamp(-(k / j), -5.0f, 5.0f), -h);
        double m = vec3d.length();
        vec3d = vec3d.multiply(0.6 / m + this.random.nextTriangular(0.5, 0.0103365), 0.6 / m + this.random.nextTriangular(0.5, 0.0103365), 0.6 / m + this.random.nextTriangular(0.5, 0.0103365));
        this.setVelocity(vec3d);
        this.setYaw((float)(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875));
        this.setPitch((float)(MathHelper.atan2(vec3d.y, vec3d.horizontalLength()) * 57.2957763671875));
        this.prevYaw = this.getYaw();
        this.prevPitch = this.getPitch();
    }

    private void checkForCollision() {
        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
        this.onCollision(hitResult);
    }

    @Override
    public void tick() {
        super.tick();
        PlayerEntity playerEntity = this.getPlayerOwner();
        if (!this.world.isClient && this.removeIfInvalid(playerEntity)) {
            return;
        }
        if (this.shouldFall()) {
            NootNootOrigins.LOGGER.info("unhooking from block");
            this.state = State.UNHOOKED;
        }
        if ( this.state == State.HOOKED_TO_BLOCK )
        {
            this.setVelocity(Vec3d.ZERO);
        }
        if ( this.state == State.UNHOOKED ) {
            NootNootOrigins.LOGGER.info("checking collisions");
            this.checkForCollision();
            if ( this.state == State.UNHOOKED)
            {
                NootNootOrigins.LOGGER.info("still unhooked");
                this.setVelocity(this.getVelocity().add(0.0, -0.03, 0.0));
            }
        }
        if (this.state == State.HOOKED_TO_ENTITY){
            if (this.hookedEntity.isRemoved() || this.hookedEntity.world.getRegistryKey() != this.world.getRegistryKey()) {
                this.state = State.UNHOOKED;
                this.hookedEntity = null;
            } else {
                this.setPosition(this.hookedEntity.getPos().add(hookOffset));
            }
        }
        this.move(MovementType.SELF, this.getVelocity());
        this.updateRotation();
        this.refreshPosition();
    }

    private boolean removeIfInvalid(PlayerEntity player) {
        if ( player == null || player.isRemoved() || !player.isAlive() || this.squaredDistanceTo(player) > 4096.0) {
            //this.discard();
            return true;
        }
        return false;
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
    }

    private Vec3d calculateHookOffset( Vec3d position ) {
        return this.getPos().subtract(position);
    }

    private void updateHookedEntity(@Nullable Entity entity) {
        this.state = State.HOOKED_TO_ENTITY;
        this.hookedEntity = entity;
        this.hookOffset = calculateHookOffset(this.hookedEntity.getPos());
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        NootNootOrigins.LOGGER.info("hit entity");
        if (!this.world.isClient) {
            this.updateHookedEntity(entityHitResult.getEntity());
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        NootNootOrigins.LOGGER.info("hit block");
        this.state = State.HOOKED_TO_BLOCK;
        this.setVelocity(Vec3d.ZERO);
    }

    private boolean shouldFall() {
        return this.state == State.HOOKED_TO_BLOCK && this.world.isSpaceEmpty(new Box(this.getPos(), this.getPos()).expand(0.06));
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
        return new EntitySpawnS2CPacket(this, entity == null ? this.world.getClosestPlayer(this, 100).getId() : entity.getId());
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        if (this.getPlayerOwner() == null) {
            int i = packet.getEntityData();
            NootNootOrigins.LOGGER.error("Failed to recreate hook on client. {} (id: {}) is not a valid owner.", (Object)this.world.getEntityById(i), (Object)i);
            this.kill();
        }
    }

    static enum State {
        UNHOOKED,
        HOOKED_TO_ENTITY,
        HOOKED_TO_BLOCK;
    }
}
