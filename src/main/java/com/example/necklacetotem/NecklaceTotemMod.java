package com.example.necklacetotem;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
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

        // 3) Only one Totem of Undying may sit in the offhand
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                limitOffhandTotem(player);
            }
        });
    }

    private static final int MAIN_INVENTORY_SLOTS = 36; // hotbar + main inventory

    /**
     * If the offhand holds more than one totem (clicked in, picked up, swapped with F...),
     * move the extras into the main inventory. If there is no room they stay put, so nothing is lost.
     */
    private static void limitOffhandTotem(ServerPlayer player) {
        ItemStack offhand = player.getOffhandItem();
        if (!offhand.is(Items.TOTEM_OF_UNDYING) || offhand.getCount() <= 1) return;

        Inventory inventory = player.getInventory();
        int max = offhand.getMaxStackSize();
        int excess = offhand.getCount() - 1;

        // top up existing totem stacks first
        for (int i = 0; i < MAIN_INVENTORY_SLOTS && excess > 0; i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty() && ItemStack.isSameItemSameComponents(stack, offhand) && stack.getCount() < max) {
                int moved = Math.min(max - stack.getCount(), excess);
                stack.grow(moved);
                excess -= moved;
            }
        }
        // then empty slots
        for (int i = 0; i < MAIN_INVENTORY_SLOTS && excess > 0; i++) {
            if (inventory.getItem(i).isEmpty()) {
                int moved = Math.min(max, excess);
                inventory.setItem(i, offhand.copyWithCount(moved));
                excess -= moved;
            }
        }
        offhand.setCount(1 + excess);
    }
}
