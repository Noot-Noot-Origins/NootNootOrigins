package morgan.noot.noot.origins.tags;

import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class NootNootOriginsTags {
    public static final TagKey<Item> FUEL_SOURCES = TagKey.of(Registry.ITEM_KEY, new Identifier("nootnoot", "fuel_sources"));

    public static void init(){

    }
}
