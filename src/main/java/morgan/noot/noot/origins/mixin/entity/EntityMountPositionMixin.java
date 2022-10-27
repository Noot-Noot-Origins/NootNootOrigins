package morgan.noot.noot.origins.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.onyxstudios.cca.api.v3.component.ComponentAccess;
import morgan.noot.noot.origins.EntityExtension;
import morgan.noot.noot.origins.NootNootOrigins;
import morgan.noot.noot.origins.network.packet.NootNootOriginsPacketsInit;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Nameable;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Entity.class)
abstract class EntityMountPositionMixin implements Nameable,
        EntityLike,
        CommandOutput,
        ComponentAccess,
        EntityExtension {
    @Shadow public World world;

    float extraMountedHeightOffset = 0;

    public float getExtraMountedHeightOffset(){
        return this.extraMountedHeightOffset;
    }

    public void setExtraMountedHeightOffset(float extraMountedHeightOffset){
        this.extraMountedHeightOffset = extraMountedHeightOffset;
        if (this.isPlayer() && !this.world.isClient() && ((ServerPlayerEntity) (Object) this).networkHandler != null) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeFloat(extraMountedHeightOffset);
            ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, NootNootOriginsPacketsInit.MOUNTED_HEIGHT_OFFSET, buf);
        }
    }
    
    @ModifyReturnValue(method = "getMountedHeightOffset", at = @At("RETURN"))
    public double modifyGetMountedHeightOffset(double original) {
        NootNootOrigins.LOGGER.info("offseting by:"+this.getExtraMountedHeightOffset());
        return original+this.getExtraMountedHeightOffset();
    }

    @Inject(method = "writeNbt", at = @At(value = "INVOKE",target = "Lnet/minecraft/nbt/NbtCompound;put(Ljava/lang/String;Lnet/minecraft/nbt/NbtElement;)Lnet/minecraft/nbt/NbtElement;"))
    public void writeNbt(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        nbt.putFloat("MountedHeightOffset",this.getExtraMountedHeightOffset());
    }

    @Inject(method = "readNbt", at = @At(value = "INVOKE",target = "Lnet/minecraft/nbt/NbtList;getDouble(I)D"))
    public void readNbt(NbtCompound nbt, CallbackInfo ci) {
        this.setExtraMountedHeightOffset(nbt.getFloat("MountedHeightOffset"));
    }
}
