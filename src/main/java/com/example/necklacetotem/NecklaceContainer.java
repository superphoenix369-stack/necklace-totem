package com.example.necklacetotem;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * One-slot container for the necklace slot.
 * Server side it reads/writes the player's saved attachment; client side it just mirrors
 * whatever the menu sync packets put in it (vanilla slot syncing does the rest).
 * It reads lazily because player data is loaded after the inventory menu is constructed.
 */
public class NecklaceContainer implements Container {
    private final Player player;
    private ItemStack clientStack = ItemStack.EMPTY;

    public NecklaceContainer(Player player) {
        this.player = player;
    }

    private boolean isClient() {
        return player.level().isClientSide();
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return getItem(0).isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        if (slot != 0) return ItemStack.EMPTY;
        return isClient() ? clientStack : player.getAttachedOrElse(NecklaceTotemMod.NECKLACE_SLOT, ItemStack.EMPTY);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack current = getItem(0);
        if (slot != 0 || current.isEmpty() || amount <= 0) return ItemStack.EMPTY;
        ItemStack taken = current.split(amount);
        if (current.isEmpty()) setItem(0, ItemStack.EMPTY);
        return taken;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot != 0) return ItemStack.EMPTY;
        ItemStack current = getItem(0);
        setItem(0, ItemStack.EMPTY);
        return current;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot != 0) return;
        if (isClient()) {
            clientStack = stack;
        } else if (stack.isEmpty()) {
            player.removeAttached(NecklaceTotemMod.NECKLACE_SLOT);
        } else {
            player.setAttached(NecklaceTotemMod.NECKLACE_SLOT, stack);
        }
    }

    @Override
    public void setChanged() {}

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        setItem(0, ItemStack.EMPTY);
    }
}
