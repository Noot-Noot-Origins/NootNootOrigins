package morgan.noot.noot.origins.mixin.item;

import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Items.class)
public class ItemsMixin {

    @ModifyArg(method = "<clinit>",at = @At(value = "INVOKE",target = ""))
}
