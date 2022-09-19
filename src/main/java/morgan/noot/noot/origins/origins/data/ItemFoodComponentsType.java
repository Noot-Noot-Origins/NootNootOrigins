package morgan.noot.noot.origins.origins.data;

import io.github.apace100.apoli.power.StatusEffectPower;
import morgan.noot.noot.origins.NootNootOrigins;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;

import java.util.LinkedList;
import java.util.List;

public class ItemFoodComponentsType {
    protected final List<ItemFoodComponentType> itemFoodComponents = new LinkedList<>();

    public ItemFoodComponentsType() {
    }

    public ItemFoodComponentsType addItemFoodComponentType(ItemFoodComponentType instance) {
        itemFoodComponents.add(instance);
        return this;
    }

    public List<ItemFoodComponentType> getItemFoodComponents(){
        return itemFoodComponents;
    }
}
