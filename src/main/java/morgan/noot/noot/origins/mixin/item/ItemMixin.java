package morgan.noot.noot.origins.mixin.item;

import morgan.noot.noot.origins.NootNootOrigins;
import morgan.noot.noot.origins.origins.powers.NootNootOriginsPowers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Debug(export = true)
@Mixin(value = Item.class)
public abstract class ItemMixin {

    @Shadow public abstract ItemStack getDefaultStack();

    @Shadow @Nullable public abstract FoodComponent getFoodComponent();

    @Shadow public abstract boolean isFood();

    @Nullable
    public LivingEntity user = null;

    @Inject(method = "isFood",at = @At("HEAD"),cancellable = true)
    public void isFood(CallbackInfoReturnable<Boolean> cir){
        NootNootOrigins.LOGGER.info(String.valueOf(NootNootOriginsPowers.canEatItem(user,(Item)(Object)this)));
        if (user != null && NootNootOriginsPowers.canEatItem(user,(Item)(Object)this)){
            cir.setReturnValue(true);
            return;
        }
    }

    @Inject(method = "getFoodComponent",at = @At("HEAD"),cancellable = true)
    public void getFoodComponent(CallbackInfoReturnable<FoodComponent> cir){
        if (this.isFood() && NootNootOriginsPowers.getItemFoodComponent(user,(Item)(Object)this)!=null){
            cir.setReturnValue(NootNootOriginsPowers.getItemFoodComponent(user,(Item)(Object)this));
            return;
        }
    }

    @Inject(method = "use",at = @At(value = "HEAD"))
    public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        this.user = user;
    }
}
