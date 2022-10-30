package morgan.noot.noot.origins.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import morgan.noot.noot.origins.EntityExtension;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import static net.minecraft.util.math.MathHelper.floor;

@Mixin(LivingEntity.class)
public abstract class LivingEntityGravityMixin extends Entity {

    public LivingEntityGravityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @ModifyConstant(method = "travel",constant = @Constant(doubleValue = 0.08,ordinal = 0))
    public double modifyGravityValue(double original)
    {
        return ((EntityExtension)this).getFullGravity();
    }

    @ModifyConstant(method = "travel",constant = @Constant(doubleValue = 0.01,ordinal = 0))
    public double modifySlowFallGravityValue(double original)
    {
        return original/8;
    }

    @ModifyVariable(method = "computeFallDamage", at = @At("HEAD"), ordinal = 0)
    private float injected(float original) {
        return original / (0.08f/((EntityExtension)this).getFullGravity());
    }

}
