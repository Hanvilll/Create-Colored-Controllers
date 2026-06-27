package hanvil.create.coloredcontrollers.mixin;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessing;
import com.simibubi.create.content.kinetics.fan.processing.SplashingType;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = FanProcessing.class, remap = false)
public class SplashingTypeMixin {

    @Inject(method = "process", at = @At("RETURN"))
    private void copyControllerComponentsOnWashing(ItemStack stack, Level level, CallbackInfoReturnable<List<ItemStack>> cir) {
        List<ItemStack> results = cir.getReturnValue();

        // Если рецепт ничего не вернул (предмет не подходит), то ничего не делаем
        if (results == null || results.isEmpty()) return;

        // Проверяем, что входящий предмет — это контроллер, и у него внутри есть записанные каналы
        if (stack.getItem() instanceof LinkedControllerItem && stack.has(AllDataComponents.LINKED_CONTROLLER_ITEMS)) {

            // Пробегаемся по всему списку предметов, которые выдал рецепт промывки
            for (ItemStack result : results) {

                // Если среди результатов есть контроллер (например, перекрашенный в другой цвет), копируем NBT/Компоненты
                if (result.getItem() instanceof LinkedControllerItem) {
                    result.set(AllDataComponents.LINKED_CONTROLLER_ITEMS, stack.get(AllDataComponents.LINKED_CONTROLLER_ITEMS));
                }
            }
        }
    }
}