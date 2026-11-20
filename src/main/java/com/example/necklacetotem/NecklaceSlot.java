package com.example.necklacetotem;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/** The slot above the offhand slot. Accepts only the Necklace Totem. */
public class NecklaceSlot extends Slot {
    // Offhand slot is at (77, 62) in the inventory screen; one row (18px) up.
    public static final int X = 77;
    public static final int Y = 44;

    private final Player owner;

    public NecklaceSlot(Player owner) {
        super(new NecklaceContainer(owner), 0, X, Y);
        this.owner = owner;
    }

    private static final Identifier EMPTY_ICON = NecklaceTotemMod.id("container/slot/necklace");

    /** Icon drawn while the slot is empty (assets/necklacetotem/textures/gui/sprites/container/slot/necklace.png). */
    @Override
    public Identifier getNoItemIcon() {
        return EMPTY_ICON;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.is(ModItems.NECKLACE_TOTEM);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    /** The creative inventory re-lays-out slots by index and would misplace this one, so hide it there. */
    @Override
    public boolean isActive() {
        return !owner.isCreative();
    }
}
