package morgan.noot.noot.origins.mixin.item;

import morgan.noot.noot.origins.NootNootOrigins;
import morgan.noot.noot.origins.origins.powers.NootNootOriginsPowers;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
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

//@Debug(export = true)
@Mixin(value = Item.class)
public abstract class ItemMixin implements ItemConvertible,
        FabricItem {

    @Shadow public abstract ItemStack getDefaultStack();

    @Shadow @Nullable public abstract FoodComponent getFoodComponent();

    @Shadow public abstract boolean isFood();

    @Nullable
    public LivingEntity user = null;

    public Item foodRemainder = null;

    @Inject(method = "isFood",at = @At("HEAD"),cancellable = true)
    public void isFood(CallbackInfoReturnable<Boolean> cir){
        if (user != null && NootNootOriginsPowers.canEatItem(user,(Item)(Object)this)){
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getFoodComponent",at = @At("HEAD"),cancellable = true)
    public void getFoodComponent(CallbackInfoReturnable<FoodComponent> cir){
        if (this.isFood() && NootNootOriginsPowers.getItemFoodComponent(user,(Item)(Object)this)!=null){
            cir.setReturnValue(NootNootOriginsPowers.getItemFoodComponent(user,(Item)(Object)this));
        }
    }

    @Inject(method = "use",at = @At(value = "HEAD"))
    public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        this.user = user;
    }

    @Inject(method = "finishUsing",at = @At(value = "INVOKE",target = "Lnet/minecraft/entity/LivingEntity;eatFood(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;",shift = At.Shift.BEFORE,ordinal = 0),cancellable = true)
    public void finishUsing(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir){
        if (this.foodRemainder != null) {
            ItemStack finalStack = user.eatFood(world, stack);
            if (finalStack.isEmpty()){
                cir.setReturnValue(foodRemainder.getDefaultStack());
                return;
            }
            cir.setReturnValue(finalStack);
            return;
        }
    }
}
