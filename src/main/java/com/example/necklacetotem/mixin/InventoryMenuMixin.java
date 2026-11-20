package com.example.necklacetotem.mixin;

import com.example.necklacetotem.ModItems;
import com.example.necklacetotem.NecklaceSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryMenu.class)
public abstract class InventoryMenuMixin {

    // Append the necklace slot after all vanilla slots (it becomes the last index).
    @Inject(method = "<init>", at = @At("TAIL"))
    private void necklacetotem$addNecklaceSlot(Inventory inventory, boolean active, Player owner, CallbackInfo ci) {
        ((AbstractContainerMenuInvoker) (Object) this).necklacetotem$addSlot(new NecklaceSlot(owner));
    }

    // Shift-click a Necklace Totem from the inventory/hotbar straight into the necklace slot.
    // (Shift-clicking it back out already works through vanilla's fallback branch.)
    @Inject(method = "quickMoveStack", at = @At("HEAD"), cancellable = true)
    private void necklacetotem$quickMove(Player player, int index, CallbackInfoReturnable<ItemStack> cir) {
        InventoryMenu menu = (InventoryMenu) (Object) this;
        if (index < 9 || index > 44 || index >= menu.slots.size()) return;

        Slot source = menu.slots.get(index);
        ItemStack stack = source.getItem();
        if (!stack.is(ModItems.NECKLACE_TOTEM)) return;

        for (Slot slot : menu.slots) {
            if (slot instanceof NecklaceSlot necklaceSlot && !necklaceSlot.hasItem()) {
                ItemStack moved = stack.copy();
                necklaceSlot.setByPlayer(stack.split(1));
                if (stack.isEmpty()) {
                    source.setByPlayer(ItemStack.EMPTY);
                } else {
                    source.setChanged();
                }
                cir.setReturnValue(moved);
                return;
            }
        }
    }
}
