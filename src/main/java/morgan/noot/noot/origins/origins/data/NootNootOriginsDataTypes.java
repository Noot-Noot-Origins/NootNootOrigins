package morgan.noot.noot.origins.origins.data;

import io.github.apace100.apoli.util.AttributedEntityAttributeModifier;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import morgan.noot.noot.origins.NootNootOrigins;
import morgan.noot.noot.origins.origins.powers.CanEatPower;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;

import java.util.List;

public class NootNootOriginsDataTypes {
    public static final SerializableDataType<ItemFoodComponentType> ITEM_FOOD_COMPONENT = SerializableDataType.compound(ItemFoodComponentType.class, new SerializableData()
                    .add("item", SerializableDataTypes.ITEM,null)
                    .add("tag", SerializableDataTypes.ITEM_TAG,null)
                    .add("food_component", SerializableDataTypes.FOOD_COMPONENT),
            data -> {
                ItemFoodComponentType itemFoodComponentType =  new ItemFoodComponentType(
                        (FoodComponent)data.get("food_component")
                );
                if (data.isPresent("item")){
                    itemFoodComponentType.setItem(data.get("item"));
                }
                if (data.isPresent("tag")){
                    itemFoodComponentType.setTag(data.get("tag"));
                }
                return itemFoodComponentType;
            },
            (serializableData, food_component) -> {
                SerializableData.Instance inst = serializableData.new Instance();
                inst.set("item", food_component.getItem());
                inst.set("tag", food_component.getTag());
                inst.set("food_component", food_component.getFoodComponent());
                return inst;
            });

    public static final SerializableDataType<List<ItemFoodComponentType>> ITEM_FOOD_COMPONENTS =
            SerializableDataType.list(ITEM_FOOD_COMPONENT);
}
