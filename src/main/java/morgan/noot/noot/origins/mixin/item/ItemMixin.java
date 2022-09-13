package morgan.noot.noot.origins.mixin.item;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import morgan.noot.noot.origins.NootNootOriginsEntityConditions;
import morgan.noot.noot.origins.tags.NootNootOriginsTags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin {

    @Shadow public abstract ItemStack getDefaultStack();

    @Shadow @Nullable public abstract FoodComponent getFoodComponent();

    public boolean canEatFuel(ItemStack stack, World world, LivingEntity user){
        return (NootNootOriginsEntityConditions.CAN_EAT_FUEL.isActive(user) && this.getDefaultStack().isIn(NootNootOriginsTags.FUEL_SOURCES));
    }

    @Inject(method = "use",at = @At(value = "INVOKE",target = "Lnet/minecraft/item/Item;isFood()Z",shift = At.Shift.BEFORE),cancellable = true)
    public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (canEatFuel(user.getStackInHand(hand),world,user)){
            ItemStack itemStack = user.getStackInHand(hand);
            if (user.canConsume(false)) {
                user.setCurrentHand(hand);
                cir.setReturnValue(TypedActionResult.consume(itemStack));
            } else {
                cir.setReturnValue(TypedActionResult.fail(itemStack));
            }
            return;
        }
    }

    @ModifyExpressionValue(method = "finishUsing", at = @At(value = "INVOKE",target = "Lnet/minecraft/item/Item;isFood()Z"))
    public boolean modifyFinishUsing(boolean original, ItemStack stack, World world, LivingEntity user) {
        return original || canEatFuel(stack,world,user);
    }
}
