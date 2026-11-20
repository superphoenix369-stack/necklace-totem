package com.example.necklacetotem;

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
        setBackground(NecklaceTotemMod.id("container/slot/necklace")); // empty-slot icon
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
