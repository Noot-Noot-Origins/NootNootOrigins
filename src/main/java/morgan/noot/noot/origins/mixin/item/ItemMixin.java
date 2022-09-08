package morgan.noot.noot.origins.mixin.item;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Item.class)
public class ItemMixin {
    @ModifyExpressionValue(method = "u",at = @At(value = "INVOKE","isFood"))
    public void allowEatingFuel(){

    }
}
