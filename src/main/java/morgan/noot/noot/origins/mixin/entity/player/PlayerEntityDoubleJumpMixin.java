package morgan.noot.noot.origins.mixin.entity.player;

import morgan.noot.noot.origins.NootNootOrigins;
import morgan.noot.noot.origins.mixin.entity.LivingEntityDoubleJumpMixin;
import morgan.noot.noot.origins.origins.powers.NootNootOriginsPowers;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityDoubleJumpMixin extends LivingEntityDoubleJumpMixin {
    @Shadow public abstract void incrementStat(Identifier stat);

    @Shadow public abstract void addExhaustion(float exhaustion);

    @Shadow protected HungerManager hungerManager;

    protected PlayerEntityDoubleJumpMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void doubleJump() {
        super.doubleJump();

        this.incrementStat(Stats.JUMP);
        this.addExhaustion(0.4f);
    }

    @Override
    public boolean canDoubleJump(){
        return super.canDoubleJump() && this.hungerManager.getFoodLevel()>6;
    }
}
