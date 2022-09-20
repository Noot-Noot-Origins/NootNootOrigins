package morgan.noot.noot.origins.origins.powers;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import morgan.noot.noot.origins.origins.data.ItemFoodComponentType;
import morgan.noot.noot.origins.origins.data.ItemFoodComponentsType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.tag.TagKey;

import java.util.LinkedList;
import java.util.List;

public class MakeNeutralPower extends Power {
    protected EntityType entityType = null;
    protected TagKey<EntityType<?>> entityTag = null;
    protected EntityGroup entityGroup = null;

    public MakeNeutralPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }

    public void addEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public void addEntityTag(TagKey<EntityType<?>> entityTag) {
        this.entityTag = entityTag;
    }

    public void addEntityGroup(EntityGroup entityGroup) {
        this.entityGroup = entityGroup;
    }

    public EntityType getEntityType() {
        return this.entityType;
    }

    public TagKey<EntityType<?>> getEntityTag() {
        return this.entityTag;
    }

    public EntityGroup getEntityGroup() {
        return this.entityGroup;
    }

    public boolean isNeutral(LivingEntity attacker){
        if (this.entityType != null && attacker.getType() == this.entityType){
            return true;
        }
        else if (this.entityTag != null && attacker.getType().isIn(this.entityTag)){
            return true;
        }
        else if (this.entityGroup != null && attacker.getGroup() == this.entityGroup){
            return true;
        }
        return false;
    }
}
