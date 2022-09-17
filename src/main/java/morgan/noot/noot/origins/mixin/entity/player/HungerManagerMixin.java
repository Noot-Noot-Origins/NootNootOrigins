package morgan.noot.noot.origins.mixin.entity.player;

import morgan.noot.noot.origins.item.ItemExtension;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HungerManager.class)
public abstract class HungerManagerMixin {
    @Shadow public abstract void add(int food, float saturationModifier);

    public FoodComponent getFuelFoodComponent(Item item) {
        if (item==Items.COAL) {return new FoodComponent.Builder().hunger(10).saturationModifier(1).build();}
        //else if (item==Items.something) {return new FoodComponent.Builder().hunger(is in half bars).saturationModifier(saturation given is this number * hunger).build();}
        return null;
    }

    @Inject(method = "eat",at = @At("HEAD"))
    public void eat(Item item, ItemStack stack, CallbackInfo ci){
        if (((ItemExtension)item).isFuelEdible(stack)) {
            FoodComponent foodComponent = getFuelFoodComponent(item);
            if (foodComponent == null)
            {
                this.add(foodComponent.getHunger(), foodComponent.getSaturationModifier());
            }
        }
    }
}
