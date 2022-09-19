package morgan.noot.noot.origins.origins.powers;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeReference;
import io.github.apace100.apoli.power.StackingStatusEffectPower;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.apoli.util.AttributedEntityAttributeModifier;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import morgan.noot.noot.origins.NootNootOrigins;
import morgan.noot.noot.origins.origins.data.ItemFoodComponentType;
import morgan.noot.noot.origins.origins.data.NootNootOriginsDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

import java.util.List;

public class NootNootOriginsPowers {

    // DOUBLE_JUMP
    //
    //
    public static final PowerFactory<Power> DOUBLE_JUMP = new PowerFactory<>(new Identifier(NootNootOrigins.MODID,"double_jump"),
            new SerializableData()
                    .add("double_jumps", SerializableDataTypes.INT, 1),
            data ->
                    (type, player) -> new DoubleJumpPower(type, player, data.getInt("double_jumps")))
            .allowCondition();

    public static boolean maxDoubleJumps(LivingEntity entity)
    {
        for (DoubleJumpPower doubleJumpPower : PowerHolderComponent.getPowers(LivingEntity.class.cast(entity), DoubleJumpPower.class)) {
            return true;
        }
        return false;
    }

    public static int getMaxDoubleJumps(LivingEntity entity)
    {
        int maxDoubleJumps = 0;
        for (DoubleJumpPower doubleJumpPower : PowerHolderComponent.getPowers(LivingEntity.class.cast(entity), DoubleJumpPower.class)) {
            maxDoubleJumps = Math.max(maxDoubleJumps,doubleJumpPower.getDoubleJumps());
        }
        return maxDoubleJumps;
    }


    //CAN_EAT
    //
    //
    public static final PowerFactory<Power> CAN_EAT = new PowerFactory<>(new Identifier(NootNootOrigins.MODID,"can_eat"),
            new SerializableData()
                    .add("item", NootNootOriginsDataTypes.ITEM_FOOD_COMPONENT, null)
                    .add("items", NootNootOriginsDataTypes.ITEM_FOOD_COMPONENTS, null),
            data ->
                    (type, player) -> {
                        CanEatPower power = new CanEatPower(type, player);
                        if(data.isPresent("item")) {
                            power.addItemFoodComponentType((ItemFoodComponentType) data.get("item"));
                        }
                        if(data.isPresent("items")) {
                            ((List<ItemFoodComponentType>)data.get("items")).forEach(power::addItemFoodComponentType);
                        }
                        return power;
                    })
            .allowCondition();

    public static boolean canEatItem(LivingEntity entity, Item item)
    {
        for (CanEatPower canEatPower : PowerHolderComponent.getPowers(LivingEntity.class.cast(entity), CanEatPower.class)) {
            return canEatPower.canEat(item);
        }
        return false;
    }

    public static FoodComponent getItemFoodComponent(LivingEntity entity, Item item)
    {
        for (CanEatPower canEatPower : PowerHolderComponent.getPowers(LivingEntity.class.cast(entity), CanEatPower.class)) {
            return canEatPower.getFoodComponent(item);
        }
        return null;
    }

    //PLANE_CAN_EAT_FUEL
    //
    //
    public static final PowerType<?> PLANE_CAN_EAT_FUEL = new PowerTypeReference<>(new Identifier(NootNootOrigins.MODID, "plane_can_eat_fuel"));

    public static void init(){
        Registry.register(ApoliRegistries.POWER_FACTORY, DOUBLE_JUMP.getSerializerId(), DOUBLE_JUMP);
        Registry.register(ApoliRegistries.POWER_FACTORY, CAN_EAT.getSerializerId(), CAN_EAT);
    }
}
