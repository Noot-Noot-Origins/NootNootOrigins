package morgan.noot.noot.origins.origins.powers;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import morgan.noot.noot.origins.NootNootOrigins;
import morgan.noot.noot.origins.origins.data.ItemFoodComponentType;
import morgan.noot.noot.origins.origins.data.ItemFoodComponentsType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;

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
        return !itemFoodComponents.getItemFoodComponents().stream().filter(target -> target.getItem() == item).toList().isEmpty();
    }

    public FoodComponent getFoodComponent(Item item) {
        List<ItemFoodComponentType> filteredFoodComponentsType = itemFoodComponents.getItemFoodComponents().stream().filter(target -> target.getItem() == item).toList();
        if (!filteredFoodComponentsType.isEmpty()){
            return filteredFoodComponentsType.get(0).getFoodComponent();
        }
        return null;
    }
}
