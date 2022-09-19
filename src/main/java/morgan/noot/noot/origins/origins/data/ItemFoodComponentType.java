package morgan.noot.noot.origins.origins.data;

import com.mojang.datafixers.types.templates.Tag;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;

public class ItemFoodComponentType {
    private Item item = null;
    private TagKey<Item> itemTag = null;
    private final FoodComponent foodComponent;

    public ItemFoodComponentType(FoodComponent foodComponent) {
        this.foodComponent = foodComponent;
    }

    public Item setItem(Item item){
        this.item = item;
        return item;
    }

    public TagKey<Item> setTag(TagKey<Item> itemTag){
        this.itemTag = itemTag;
        return itemTag;
    }

    public Item getItem(){ return item;}

    public TagKey<Item> getTag(){ return itemTag;}

    public FoodComponent getFoodComponent(){ return foodComponent; }
}
