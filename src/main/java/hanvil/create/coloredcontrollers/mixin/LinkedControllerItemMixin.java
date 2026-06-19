package hanvil.create.coloredcontrollers.mixin;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerItem;
import com.simibubi.create.foundation.item.ItemHelper;
import hanvil.create.coloredcontrollers.ColoredLinkedControllerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LinkedControllerItem.class, remap = false)
public class LinkedControllerItemMixin {
    @Inject(method = "getFrequencyItems", at = @At("HEAD"), cancellable = true)
    private static void allowColoredControllers(ItemStack stack, CallbackInfoReturnable<ItemStackHandler> cir) {
        if (stack.getItem() instanceof ColoredLinkedControllerItem) {
            ItemStackHandler newInv = new ItemStackHandler(12);

            if (stack.has(AllDataComponents.LINKED_CONTROLLER_ITEMS)) {
                ItemHelper.fillItemStackHandler(stack.getOrDefault(AllDataComponents.LINKED_CONTROLLER_ITEMS, ItemContainerContents.EMPTY), newInv);
            }

            cir.setReturnValue(newInv);
        }
    }
}