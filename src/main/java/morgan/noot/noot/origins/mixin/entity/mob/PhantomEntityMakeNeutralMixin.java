package morgan.noot.noot.origins.mixin.entity.mob;

import morgan.noot.noot.origins.origins.powers.NootNootOriginsPowers;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PhantomEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/entity/mob/PhantomEntity$FindTargetGoal")
public abstract class PhantomEntityMakeNeutralMixin
        extends Goal {

    /* half finished code, trying to fix phantoms aggro
    @Inject(method = "canStart",at = @At(value = "INVOKE",target = "Lnet/minecraft/entity/mob/PhantomEntity;isTarget(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/ai/TargetPredicate;)Z"))
    public void canStart(CallbackInfoReturnable<Boolean> cir){
        if (NootNootOriginsPowers.isNeutral(player2, this)) continue;
    }
     */

}
