package morgan.noot.noot.origins.origins.powers;

import com.google.gson.JsonParseException;
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
                        boolean itemsPresent = data.isPresent("items");
                        boolean itemPresent = data.isPresent("item");
                        if(itemsPresent == itemPresent) {
                            throw new JsonParseException("An food can be either a tag or an item, " + (itemsPresent ? "not both" : "one has to be provided."));
                        }
                        if(itemPresent) {
                            power.addItemFoodComponentType((ItemFoodComponentType) data.get("item"));
                        }
                        else if(itemsPresent) {
                            ((List<ItemFoodComponentType>)data.get("items")).forEach(power::addItemFoodComponentType);
                        }
                        return power;
                    })
            .allowCondition();

    public static boolean canEatItem(LivingEntity entity, Item item)
    {
        for (CanEatPower canEatPower : PowerHolderComponent.getPowers(LivingEntity.class.cast(entity), CanEatPower.class)) {
            if (canEatPower.canEat(item)){
                return true;
            }
        }
        return false;
    }

    public static FoodComponent getItemFoodComponent(LivingEntity entity, Item item)
    {
        for (CanEatPower canEatPower : PowerHolderComponent.getPowers(LivingEntity.class.cast(entity), CanEatPower.class)) {
            FoodComponent foodComponent = canEatPower.getFoodComponent(item);
            if (foodComponent!=null){
                return foodComponent;
            }
        }
        return null;
    }

    // MAKE_NEUTRAL
    //
    //
    public static final PowerFactory<Power> MAKE_NEUTRAL = new PowerFactory<>(new Identifier(NootNootOrigins.MODID,"make_neutral"),
            new SerializableData()
                    .add("entity_group", SerializableDataTypes.ENTITY_GROUP, null)
                    .add("entity_tag", SerializableDataTypes.ENTITY_TAG, null)
                    .add("entity_type", SerializableDataTypes.ENTITY_TYPE, null),
            data ->
                    (type, player) -> {
                        MakeNeutralPower power = new MakeNeutralPower(type, player);
                        boolean tagPresent = data.isPresent("entity_tag");
                        boolean typePresent = data.isPresent("entity_type");
                        boolean groupPresent = data.isPresent("entity_group");
                        // if all the same (i.e none present or all present)
                        if(tagPresent == typePresent && typePresent == groupPresent) {
                            throw new JsonParseException("An entity can be either a type, a group or a tag, " + (tagPresent ? "not all of them" : "one has to be provided."));
                        }
                        // no idea how this works but i guess ill see
                        if ((tagPresent ^ typePresent ^ groupPresent) == (tagPresent && typePresent && groupPresent)){
                            throw new JsonParseException("An entity can be either a type, a group or a tag, not two of them");
                        }

                        if(tagPresent) {
                            power.addEntityTag(data.get("entity_tag"));
                        }
                        else if(typePresent) {
                            power.addEntityType(data.get("entity_type"));
                        }
                        else if(groupPresent) {
                            power.addEntityGroup(data.get("entity_group"));
                        }
                        return power;
                    })
            .allowCondition();

    public static boolean isNeutral(LivingEntity target, LivingEntity attacker)
    {
        for (MakeNeutralPower makeNeutralPower : PowerHolderComponent.getPowers(LivingEntity.class.cast(target), MakeNeutralPower.class)) {
            if (makeNeutralPower.isNeutral(attacker)){
                return true;
            }
        }
        return false;
    }

    // CHANGE_TRADE_PRICE
    //
    //
    public static final PowerFactory<Power> CHANGE_TRADE_PRICE = new PowerFactory<>(new Identifier(NootNootOrigins.MODID,"change_trade_price"),
            new SerializableData()
                    .add("amount", SerializableDataTypes.INT),
            data ->
                    (type, player) -> new ChangeTradePrice(type, player, data.getInt("amount")))
            .allowCondition();

    public static int getTradeChangePrice(LivingEntity entity)
    {
        int price = 0;
        for (ChangeTradePrice changeTradePrice : PowerHolderComponent.getPowers(LivingEntity.class.cast(entity), ChangeTradePrice.class)) {
            price += changeTradePrice.getAmount();
        }
        return price;
    }

    //BEE_TAME
    public static final PowerType<?> BEE_TAME = new PowerTypeReference<>(new Identifier(NootNootOrigins.MODID,"bloomfolk_bee_tame"));



    public static void init(){
        Registry.register(ApoliRegistries.POWER_FACTORY, DOUBLE_JUMP.getSerializerId(), DOUBLE_JUMP);
        Registry.register(ApoliRegistries.POWER_FACTORY, CAN_EAT.getSerializerId(), CAN_EAT);
        Registry.register(ApoliRegistries.POWER_FACTORY, MAKE_NEUTRAL.getSerializerId(), MAKE_NEUTRAL);
        Registry.register(ApoliRegistries.POWER_FACTORY, CHANGE_TRADE_PRICE.getSerializerId(), CHANGE_TRADE_PRICE);
    }
}
