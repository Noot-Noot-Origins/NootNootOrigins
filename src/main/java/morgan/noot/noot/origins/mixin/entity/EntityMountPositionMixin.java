package morgan.noot.noot.origins.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.onyxstudios.cca.api.v3.component.ComponentAccess;
import morgan.noot.noot.origins.EntityExtension;
import morgan.noot.noot.origins.NootNootOriginsComponents;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.util.Nameable;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
abstract class EntityMountPositionMixin implements Nameable,
        EntityLike,
        CommandOutput,
        ComponentAccess,
        EntityExtension {
    @Shadow public World world;


    public float getExtraMountedHeightOffset(){
        return this.getComponent(NootNootOriginsComponents.MOUNT_POSITION).getValue();
    }

    public void setExtraMountedHeightOffset(float extraMountedHeightOffset){
        this.getComponent(NootNootOriginsComponents.MOUNT_POSITION).setValue(extraMountedHeightOffset);
    }
    
    @ModifyReturnValue(method = "getMountedHeightOffset", at = @At("RETURN"))
    public double modifyGetMountedHeightOffset(double original) {
        return original+this.getExtraMountedHeightOffset();
    }
}
