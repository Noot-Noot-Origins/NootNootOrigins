package morgan.noot.noot.origins.mixin.item;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import morgan.noot.noot.origins.NootNootOriginsEntityConditions;
import morgan.noot.noot.origins.item.ItemExtension;
import morgan.noot.noot.origins.tags.NootNootOriginsTags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
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
public abstract class ItemMixin implements ItemExtension {

    @Shadow public abstract ItemStack getDefaultStack();

    @Shadow @Nullable public abstract FoodComponent getFoodComponent();

    @Nullable
    public LivingEntity user;

    public boolean isFuelEdible(ItemStack stack){
        return (NootNootOriginsEntityConditions.PLANE_CAN_EAT_FUEL.isActive(user) && stack.isIn(NootNootOriginsTags.FUEL_SOURCES));
    }

    @Inject(method = "use",at = @At(value = "INVOKE",target = "Lnet/minecraft/item/Item;isFood()Z",shift = At.Shift.BEFORE),cancellable = true)
    public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        this.user = user;
        if (isFuelEdible(user.getStackInHand(hand))){
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
    public boolean modifyFinishUsingIsFood(boolean original, ItemStack stack, World world, LivingEntity user) {
        return original || isFuelEdible(stack);
    }

    @ModifyExpressionValue(method = "getUseAction", at = @At(value = "INVOKE",target = "Lnet/minecraft/item/Item;isFood()Z"))
    public boolean modifyGetUseActionIsFood(boolean original, ItemStack stack) {
        return original || isFuelEdible(stack);
    }

    @Inject(method = "getMaxUseTime", at = @At(value = "HEAD"),cancellable = true)
    public void getMaxUseTime(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (isFuelEdible(stack)){
            cir.setReturnValue(32);
            return;
        }
    }
}
