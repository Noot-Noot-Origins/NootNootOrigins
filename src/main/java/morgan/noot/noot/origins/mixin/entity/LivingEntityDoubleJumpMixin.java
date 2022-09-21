package morgan.noot.noot.origins.mixin.entity;

import morgan.noot.noot.origins.entity.LivingEntityExtension;
import morgan.noot.noot.origins.network.packet.NootNootOriginsPacketsInit;
import morgan.noot.noot.origins.origins.powers.NootNootOriginsPowers;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityDoubleJumpMixin extends Entity implements LivingEntityExtension {
    @Shadow protected boolean jumping;

    @Shadow protected abstract boolean shouldSwimInFluids();

    @Shadow private int jumpingCooldown;

    @Shadow protected abstract void jump();

    @Shadow protected abstract float getJumpVelocity();

    @Shadow public abstract double getJumpBoostVelocityModifier();

    @Shadow public float sidewaysSpeed;

    @Shadow protected abstract float getVelocityMultiplier();

    @Shadow public float forwardSpeed;

    @Shadow public abstract boolean canBeRiddenInWater();

    public LivingEntityDoubleJumpMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    public int doubleJumps = 0;

    public int getMaxDoubleJumps(){
        return NootNootOriginsPowers.getMaxDoubleJumps((LivingEntity) (Object)this);
    }

    public boolean canDoubleJump(){
        return (NootNootOriginsPowers.maxDoubleJumps((LivingEntity) (Object)this) && doubleJumps > 0 && !this.onGround);
    }

    public boolean hasDoubleJumped(){
        return (NootNootOriginsPowers.maxDoubleJumps((LivingEntity) (Object)this) && doubleJumps < getMaxDoubleJumps());
    }

    public float getDoubleJumps(){
        return this.doubleJumps;
    }

    public void setDoubleJumps(int doubleJumps){
        this.doubleJumps = doubleJumps;
        if (this.isPlayer() && !this.world.isClient() && ((ServerPlayerEntity) (Object) this).networkHandler != null) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(doubleJumps);
            ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, NootNootOriginsPacketsInit.DOUBLE_JUMP_PACKET_ID, buf);
        }
    }

    protected void doubleJump() {
        double d = (double) this.getJumpVelocity() + this.getJumpBoostVelocityModifier();
        Vec3d vec3d = this.getVelocity();
        this.setVelocity(vec3d.x, d, vec3d.z);
        float f;
        if (this.sidewaysSpeed < 0) {
            f = (this.getYaw() + 90) % 360 * ((float) Math.PI / 180);
            this.setVelocity(this.getVelocity().add(-MathHelper.sin(f) * 0.2f, 0.0, MathHelper.cos(f) * 0.2f));
            this.velocityDirty = true;
        }
        else if (this.sidewaysSpeed > 0) {
            f = (this.getYaw() - 90) % 360 * ((float) Math.PI / 180);
            this.setVelocity(this.getVelocity().add(-MathHelper.sin(f) * 0.2f, 0.0, MathHelper.cos(f) * 0.2f));
            this.velocityDirty = true;
        }
        else if (this.forwardSpeed < 0 ) {
            f = (this.getYaw() - 180) % 360 * ((float) Math.PI / 180);
            this.setVelocity(this.getVelocity().add(-MathHelper.sin(f) * 0.2f, 0.0, MathHelper.cos(f) * 0.2f));
            this.velocityDirty = true;
        }
        else if (this.forwardSpeed > 0 ) {
            f = this.getYaw() * ((float) Math.PI / 180);
            this.setVelocity(this.getVelocity().add(-MathHelper.sin(f) * 0.2f, 0.0, MathHelper.cos(f) * 0.2f));
            this.velocityDirty = true;
        }
        //this.setVelocity(this.getVelocity().multiply(-MathHelper.sin(f), 1, MathHelper.cos(f)));
    }

    @Inject(method = "tickMovement",at = @At(value = "INVOKE",target = "Lnet/minecraft/entity/LivingEntity;shouldSwimInFluids()Z",shift = At.Shift.BEFORE))
    public void tickMovementResetDoubleJump(CallbackInfo ci)
    {
        if (this.onGround && doubleJumps < getMaxDoubleJumps())
        {
            this.setDoubleJumps(getMaxDoubleJumps());
        }
        else if (this.isTouchingWater() && doubleJumps <= getMaxDoubleJumps() ) {
            this.setDoubleJumps(getMaxDoubleJumps()+1);
        }

        if (this.jumping && this.shouldSwimInFluids()) {
            double k = this.isInLava() ? this.getFluidHeight(FluidTags.LAVA) : this.getFluidHeight(FluidTags.WATER);
            boolean bl = this.isTouchingWater() && k > 0.0;
            double l = this.getSwimHeight();
            if (!(this.isInLava() && (!this.onGround || k > l))&&!(bl && (!this.onGround || k > l)) && ((canDoubleJump()) && this.jumpingCooldown == 0)) {
                this.doubleJump();
                this.setDoubleJumps(doubleJumps-1);
                this.jumpingCooldown = 10;
            }
        }
    }
}
