package hanvil.create.coloredcontrollers.mixin;

import com.simibubi.create.content.redstone.link.controller.LinkedControllerClientHandler;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerItem;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = LinkedControllerClientHandler.class, remap = false)
public class LinkedControllerClientHandlerMixin {
    @Unique private static final ItemStack FAKE_MAIN_HAND = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse("create:linked_controller")));
    @Unique private static final ItemStack FAKE_OFF_HAND = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse("create:linked_controller")));

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getMainHandItem()Lnet/minecraft/world/item/ItemStack;", remap = true))
    private static ItemStack fakeMainHandController(LocalPlayer player) {
        ItemStack stack = player.getMainHandItem();

        if (stack.getItem() instanceof LinkedControllerItem && stack.getItem() != FAKE_MAIN_HAND.getItem()) {
            FAKE_MAIN_HAND.applyComponents(stack.getComponents());
            return FAKE_MAIN_HAND;
        }

        return stack;
    }

    @Redirect(method = "tick",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getOffhandItem()Lnet/minecraft/world/item/ItemStack;", remap = true))
    private static ItemStack fakeOffHandController(LocalPlayer player) {
        ItemStack stack = player.getOffhandItem();

        if (stack.getItem() instanceof LinkedControllerItem && stack.getItem() != FAKE_OFF_HAND.getItem()) {
            FAKE_OFF_HAND.applyComponents(stack.getComponents());
            return FAKE_OFF_HAND;
        }

        return stack;
    }
}