package morgan.noot.noot.origins.entity.ai.goal;

import morgan.noot.noot.origins.entity.passive.BeeEntityExtension;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.TameableEntity;

import java.util.EnumSet;

public class BeeAttackWithOwnerGoal extends TrackTargetGoal {
    private final BeeEntity bee;
    private LivingEntity attacking;
    private int lastAttackTime;

    public BeeAttackWithOwnerGoal(BeeEntity bee) {
        super(bee, false);
        this.bee = bee;
        this.setControls(EnumSet.of(Goal.Control.TARGET));
    }

    @Override
    public boolean canStart() {
        if (!((BeeEntityExtension)this.bee).isTamed()) {
            return false;
        }
        LivingEntity livingEntity = ((BeeEntityExtension)this.bee).getOwner();
        if (livingEntity == null) {
            return false;
        }
        this.attacking = livingEntity.getAttacking();
        int i = livingEntity.getLastAttackTime();
        return i != this.lastAttackTime && this.canTrack(this.attacking, TargetPredicate.DEFAULT) && ((BeeEntityExtension)this.bee).canAttackWithOwner(this.attacking, livingEntity);
    }

    @Override
    public void start() {
        this.mob.setTarget(this.attacking);
        LivingEntity livingEntity = ((BeeEntityExtension)this.bee).getOwner();
        if (livingEntity != null) {
            this.lastAttackTime = livingEntity.getLastAttackTime();
        }
        super.start();
    }
}

