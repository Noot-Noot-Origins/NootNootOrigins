package morgan.noot.noot.origins.origins.powers;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import morgan.noot.noot.origins.NootNootOrigins;
import morgan.noot.noot.origins.origins.data.ItemFoodComponentType;
import morgan.noot.noot.origins.origins.data.ItemFoodComponentsType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;

import java.util.Iterator;
import java.util.List;

public class CanEatPower extends Power {
    public ItemFoodComponentsType itemFoodComponents = new ItemFoodComponentsType();

    public CanEatPower(PowerType<?> type, LivingEntity entity) {
        super(type, entity);
    }

    public ItemFoodComponentsType addItemFoodComponentType(ItemFoodComponentType instance) {
        return itemFoodComponents.addItemFoodComponentType(instance);
    }

    public boolean canEat(Item item) {
        List<ItemFoodComponentType> itemFoodComponentTypes = itemFoodComponents.getItemFoodComponents();
        Iterator<ItemFoodComponentType> iterator = itemFoodComponentTypes.iterator();

        while(iterator.hasNext()) {
            ItemFoodComponentType itemFoodComponentType = iterator.next();
            if (itemFoodComponentType.getItem() != null && item == itemFoodComponentType.getItem()) {
                return true;
            }
            if (itemFoodComponentType.getTag() != null && item.getDefaultStack().isIn(itemFoodComponentType.getTag())) {
                return true;

            }
        }

        return false;
    }

    public FoodComponent getFoodComponent(Item item) {
        List<ItemFoodComponentType> itemFoodComponentTypes = itemFoodComponents.getItemFoodComponents();
        Iterator<ItemFoodComponentType> iterator = itemFoodComponentTypes.iterator();

        while(iterator.hasNext()) {
            ItemFoodComponentType itemFoodComponentType = iterator.next();
            if (itemFoodComponentType.getItem() != null && item == itemFoodComponentType.getItem()) {
                return itemFoodComponentType.getFoodComponent();
            }
            if (itemFoodComponentType.getTag() != null && item.getDefaultStack().isIn(itemFoodComponentType.getTag())) {
                return itemFoodComponentType.getFoodComponent();
            }
        }

        return null;
    }
}
