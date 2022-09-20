package morgan.noot.noot.origins.mixin.entity.ai.goal;

import morgan.noot.noot.origins.origins.powers.NootNootOriginsPowers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ActiveTargetGoal.class)
public abstract class ActiveTargetGoalMakeNeutralMixin<T extends LivingEntity>
        extends TrackTargetGoal {
    @Shadow @Final protected Class<T> targetClass;

    @Shadow @Nullable protected LivingEntity targetEntity;

    @Shadow protected TargetPredicate targetPredicate;

    public ActiveTargetGoalMakeNeutralMixin(MobEntity mob, boolean checkVisibility) {
        super(mob, checkVisibility);
    }

    @Inject(method = "findClosestTarget",at = @At("HEAD"),cancellable = true)
    public void findClosestTarget(CallbackInfo ci) {
        if (this.targetClass == PlayerEntity.class || this.targetClass == ServerPlayerEntity.class) {
            double d = -1.0;
            PlayerEntity player = null;
            for (PlayerEntity player2 : this.mob.world.getPlayers()) {
                if (!this.targetPredicate.test(this.mob, player2)) continue;
                if (NootNootOriginsPowers.isNeutral(player2,this.mob)) continue;
                double e = player2.squaredDistanceTo(this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
                if (d != -1.0 && !(e < d)) continue;
                d = e;
                player = player2;
            }

            this.targetEntity = (T)player;
            ci.cancel();
            return;
        }
    }
}
