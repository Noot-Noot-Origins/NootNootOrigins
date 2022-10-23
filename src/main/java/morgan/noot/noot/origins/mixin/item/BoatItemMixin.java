package morgan.noot.noot.origins.mixin.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoatItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoatItem.class)
public abstract class BoatItemMixin extends ItemMixin{
    @Inject(method = "use",at = @At(value = "INVOKE",target = "Lnet/minecraft/util/TypedActionResult;pass(Ljava/lang/Object;)Lnet/minecraft/util/TypedActionResult;",shift = At.Shift.BEFORE,ordinal = 0), cancellable = true)
    public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        this.user = user;
        if (this.isFood()) {
            ItemStack itemStack = user.getStackInHand(hand);
            if (user.canConsume(this.getFoodComponent().isAlwaysEdible())) {
                user.setCurrentHand(hand);
                cir.setReturnValue(TypedActionResult.consume(itemStack));
            }
            cir.setReturnValue(TypedActionResult.fail(itemStack));
        }
    }
}
