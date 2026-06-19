package hanvil.create.coloredcontrollers.mixin;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.redstone.link.controller.LecternControllerBlockEntity;
import hanvil.create.coloredcontrollers.ColoredLinkedControllerItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LecternControllerBlockEntity.class, remap = false)
public class LecternControllerBlockEntityMixin {
    @Shadow private ItemContainerContents controllerData;

    @Unique private Item coloredcontrollers$originalItem = null;

    @Inject(method = "setController", at = @At("HEAD"))
    private void rememberColoredController(ItemStack newController, CallbackInfo ci) {
        if (newController != null && newController.getItem() instanceof ColoredLinkedControllerItem) {
            this.coloredcontrollers$originalItem = newController.getItem();
        } else {
            this.coloredcontrollers$originalItem = null;
        }
    }

    @Inject(method = "write", at = @At("TAIL"), remap = true)
    private void saveColorToDisk(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        if (this.coloredcontrollers$originalItem != null) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(this.coloredcontrollers$originalItem);
            compound.putString("ColoredControllerId", id.toString());
        }
    }

    @Inject(method = "writeSafe", at = @At("TAIL"), remap = true)
    private void saveColorToDiskSafe(CompoundTag compound, HolderLookup.Provider registries, CallbackInfo ci) {
        if (this.coloredcontrollers$originalItem != null) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(this.coloredcontrollers$originalItem);
            compound.putString("ColoredControllerId", id.toString());
        }
    }

    @Inject(method = "read", at = @At("TAIL"), remap = true)
    private void loadColorFromDisk(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        if (compound.contains("ColoredControllerId")) {
            ResourceLocation id = ResourceLocation.parse(compound.getString("ColoredControllerId"));
            this.coloredcontrollers$originalItem = BuiltInRegistries.ITEM.get(id);
        } else {
            this.coloredcontrollers$originalItem = null;
        }
    }

    @Inject(method = "createLinkedController", at = @At("HEAD"), cancellable = true)
    private void returnColoredController(CallbackInfoReturnable<ItemStack> cir) {
        if (this.coloredcontrollers$originalItem != null) {
            ItemStack stack = new ItemStack(this.coloredcontrollers$originalItem);
            stack.set(AllDataComponents.LINKED_CONTROLLER_ITEMS, this.controllerData);
            cir.setReturnValue(stack);
        }
    }
}