package morgan.noot.noot.origins.entity.ai.goal;

import morgan.noot.noot.origins.entity.passive.BeeEntityExtension;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.TameableEntity;

import java.util.EnumSet;

public class BeeTrackOwnerAttackerGoal
extends TrackTargetGoal {
    private final BeeEntity bee;
    private LivingEntity attacker;
    private int lastAttackedTime;

    public BeeTrackOwnerAttackerGoal(BeeEntity bee) {
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
        this.attacker = livingEntity.getAttacker();
        int i = livingEntity.getLastAttackedTime();
        return i != this.lastAttackedTime && this.canTrack(this.attacker, TargetPredicate.DEFAULT) && ((BeeEntityExtension)this.bee).canAttackWithOwner(this.attacker, livingEntity);
    }

    @Override
    public void start() {
        this.mob.setTarget(this.attacker);
        LivingEntity livingEntity = ((BeeEntityExtension)this.bee).getOwner();
        if (livingEntity != null) {
            this.lastAttackedTime = livingEntity.getLastAttackedTime();
        }
        super.start();
    }
}
