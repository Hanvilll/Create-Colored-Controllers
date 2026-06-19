package hanvil.create.coloredcontrollers.mixin;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerItem;
import hanvil.create.coloredcontrollers.ColoredLinkedControllerItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ShapelessRecipe.class, remap = true)
public class ShapelessRecipeMixin {
    @Inject(method = "assemble", at = @At("RETURN"))
    private void copyControllerComponents(CraftingInput input, HolderLookup.Provider registries, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack result = cir.getReturnValue();

        if (result.getItem() instanceof ColoredLinkedControllerItem) {
            for (int i = 0; i < input.size(); i++) {
                ItemStack ingredient = input.getItem(i);

                if (ingredient.getItem() instanceof LinkedControllerItem && ingredient.has(AllDataComponents.LINKED_CONTROLLER_ITEMS)) {
                    result.set(AllDataComponents.LINKED_CONTROLLER_ITEMS, ingredient.get(AllDataComponents.LINKED_CONTROLLER_ITEMS));
                    break;
                }
            }
        }
    }
}