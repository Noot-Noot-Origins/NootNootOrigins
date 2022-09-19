package morgan.noot.noot.origins.origins.data;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;

public class ItemFoodComponentType {
    private final Item item;
    private final FoodComponent foodComponent;

    public ItemFoodComponentType(Item item,FoodComponent foodComponent) {
        this.item = item;
        this.foodComponent = foodComponent;
    }

    public Item getItem(){ return item;}

    public FoodComponent getFoodComponent(){ return foodComponent; }
}
