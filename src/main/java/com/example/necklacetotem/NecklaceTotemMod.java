package com.example.necklacetotem;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class NecklaceTotemMod implements ModInitializer {
    public static final String MOD_ID = "necklacetotem";

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    /** The item sitting in the player's necklace slot. Saved with the player, kept through death. */
    public static final AttachmentType<ItemStack> NECKLACE_SLOT = AttachmentRegistry.create(
            id("necklace_slot"),
            builder -> builder.persistent(ItemStack.OPTIONAL_CODEC).copyOnDeath());

    @Override
    public void onInitialize() {
        ModItems.init();

        // 1) Totem of Undying stacks to 8
        DefaultItemComponentEvents.MODIFY.register(context ->
                context.modify(Items.TOTEM_OF_UNDYING, builder -> builder.set(DataComponents.MAX_STACK_SIZE, 8)));

        // Creative tab entry for the necklace
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT)
                .register(output -> output.accept(ModItems.NECKLACE_TOTEM));

        // 2) A Necklace Totem in the slot works like a totem when you would die
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, amount) -> {
            if (!(entity instanceof ServerPlayer player)) return true;
            if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return true; // void, /kill

            ItemStack necklace = player.getAttachedOrElse(NECKLACE_SLOT, ItemStack.EMPTY);
            if (!necklace.is(ModItems.NECKLACE_TOTEM)) return true;

            player.removeAttached(NECKLACE_SLOT); // consumed

            player.setHealth(1.0F);
            player.removeAllEffects();
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
            player.level().broadcastEntityEvent(player, (byte) 35); // totem pop animation
            return false; // cancel death
        });
    }
}
