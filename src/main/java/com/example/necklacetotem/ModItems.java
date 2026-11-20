package com.example.necklacetotem;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public final class ModItems {
    private ModItems() {}

    public static final Item NECKLACE_TOTEM = register("necklace_totem",
            new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));

    private static Item register(String name, Item.Properties properties) {
        Identifier id = NecklaceTotemMod.id(name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return Registry.register(BuiltInRegistries.ITEM, key, new Item(properties.setId(key)));
    }

    /** Touching this class registers the items. */
    public static void init() {}
}
