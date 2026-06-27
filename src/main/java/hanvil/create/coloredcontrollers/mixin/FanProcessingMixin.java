package hanvil.create.coloredcontrollers.mixin;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessing;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import java.util.List;

@Mixin(value = FanProcessing.class, remap = false)
public class FanProcessingMixin {
    @ModifyVariable(method = "applyProcessing(Lnet/minecraft/world/entity/item/ItemEntity;Lcom/simibubi/create/content/kinetics/fan/processing/FanProcessingType;)Z", at = @At(value = "INVOKE_ASSIGN", target = "Lcom/simibubi/create/content/kinetics/fan/processing/FanProcessingType;process(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;)Ljava/util/List;"), remap = false)
    private static List<ItemStack> copyComponentsFromItemEntity(List<ItemStack> stacks, ItemEntity entity, FanProcessingType type) {
        copyControllerData(entity.getItem(), stacks, type);
        return stacks;
    }

    @ModifyVariable(method = "applyProcessing(Lcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;Lnet/minecraft/world/level/Level;Lcom/simibubi/create/content/kinetics/fan/processing/FanProcessingType;)Lcom/simibubi/create/content/kinetics/belt/behaviour/TransportedItemStackHandlerBehaviour$TransportedResult;", at = @At(value = "INVOKE_ASSIGN", target = "Lcom/simibubi/create/content/kinetics/fan/processing/FanProcessingType;process(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;)Ljava/util/List;"), remap = false)
    private static List<ItemStack> copyComponentsFromTransportedBelt(List<ItemStack> stacks, TransportedItemStack transported, Level world, FanProcessingType type) {
        copyControllerData(transported.stack, stacks, type);
        return stacks;
    }

    @Unique
    private static void copyControllerData(ItemStack inputStack, List<ItemStack> outputStacks, FanProcessingType type) {
        if (outputStacks == null || outputStacks.isEmpty()) {
            return;
        }

        ResourceLocation typeId = CreateBuiltInRegistries.FAN_PROCESSING_TYPE.getKey(type);
        if (typeId == null || !typeId.toString().equals("create:splashing")) {
            return;
        }

        if (inputStack.getItem() instanceof LinkedControllerItem && inputStack.has(AllDataComponents.LINKED_CONTROLLER_ITEMS)) {
            for (ItemStack outputStack : outputStacks) {
                if (outputStack.getItem() instanceof LinkedControllerItem) {
                    outputStack.set(AllDataComponents.LINKED_CONTROLLER_ITEMS, inputStack.get(AllDataComponents.LINKED_CONTROLLER_ITEMS));
                }
            }
        }
    }
}