package morgan.noot.noot.origins.mixin.entity;

import morgan.noot.noot.origins.NootNootOrigins;
import morgan.noot.noot.origins.entity.LivingEntityExtension;
import morgan.noot.noot.origins.entity.projectile.HookEntity;
import morgan.noot.noot.origins.network.packet.NootNootOriginsPacketsInit;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityHooksMixin extends Entity implements LivingEntityExtension {

    public LivingEntityHooksMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    HookEntity hook = null;

    public boolean hasHook() { return this.hook != null; }

    public HookEntity getHook(){
        return this.hook;
    }

    public void setHook(HookEntity hook){
        if ( this.hook == hook )
        {
            return;
        }
        this.hook = hook;
        if (this.isPlayer() && !this.world.isClient() && ((ServerPlayerEntity) (Object) this).networkHandler != null) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(hook.getId());
            ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, NootNootOriginsPacketsInit.HOOK_ID_PACKET_ID, buf);
        }
    }

    @Inject(method = "onDeath",at = @At("TAIL"))
    public void onDeath(DamageSource damageSource, CallbackInfo ci)
    {
        if (this.getHook() != null)
        {
            this.getHook().discard();
            this.setHook(null);
        }
    }
}
