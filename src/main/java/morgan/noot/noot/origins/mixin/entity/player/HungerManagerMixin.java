package morgan.noot.noot.origins.mixin.entity.player;

import morgan.noot.noot.origins.NootNootOrigins;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HungerManager.class)
public abstract class HungerManagerMixin {
    @Inject(method = "eat",at = @At("HEAD"))
    public void eat(Item item, ItemStack stack, CallbackInfo ci) {
        NootNootOrigins.LOGGER.info(String.valueOf(item.getFoodComponent().getHunger()));
    }
}
