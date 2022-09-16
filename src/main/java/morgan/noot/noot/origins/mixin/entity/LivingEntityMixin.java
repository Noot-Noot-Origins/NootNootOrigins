package morgan.noot.noot.origins.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import morgan.noot.noot.origins.NootNootOriginsEntityConditions;
import morgan.noot.noot.origins.entity.LivingEntityExtension;
import morgan.noot.noot.origins.item.ItemExtension;
import morgan.noot.noot.origins.tags.NootNootOriginsTags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements LivingEntityExtension {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    public boolean canEatFuel(ItemStack stack){
        return (NootNootOriginsEntityConditions.PLANE_CAN_EAT_FUEL.isActive(this) && stack.isIn(NootNootOriginsTags.FUEL_SOURCES));
    }

    @ModifyExpressionValue(method = "eatFood",at = @At(value = "INVOKE",target = "Lnet/minecraft/item/ItemStack;isFood()Z"))
    public boolean modifyEatFoodIsFood(boolean original, World world, ItemStack stack){
        return original || ((ItemExtension)stack.getItem()).isFuelEdible(stack);
    }
}
