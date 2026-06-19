package hanvil.create.coloredcontrollers.mixin;

import com.simibubi.create.content.redstone.link.controller.LinkedControllerItem;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerPacketBase;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LinkedControllerPacketBase.class, remap = false)
public abstract class LinkedControllerPacketBaseMixin {
    @Shadow @Nullable private BlockPos lecternPos;
    @Shadow abstract void handleItem(ServerPlayer player, ItemStack stack);

    @Inject(method = "handle", at = @At("HEAD"), cancellable = true)
    private void handleCustomControllers(ServerPlayer player, CallbackInfo ci) {
        if (this.lecternPos != null) {
            return;
        }

        ItemStack controller = player.getMainHandItem();
        if (controller.getItem() instanceof LinkedControllerItem) {
            this.handleItem(player, controller);
            ci.cancel();

            return;
        }

        controller = player.getOffhandItem();
        if (controller.getItem() instanceof LinkedControllerItem) {
            this.handleItem(player, controller);
            ci.cancel();

            return;
        }
    }
}