package hanvil.create.coloredcontrollers;

import net.minecraft.world.item.DyeColor;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerItem;
import java.util.EnumMap;
import java.util.Map;

@Mod(CreateColoredControllers.MODID)
public class CreateColoredControllers {
    public static final String MODID = "createcoloredcontrollers";

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final Map<DyeColor, DeferredItem<LinkedControllerItem>> COLORED_CONTROLLERS = new EnumMap<>(DyeColor.class);

    static {
        for (DyeColor color : DyeColor.values()) {
            COLORED_CONTROLLERS.put(color, ITEMS.register(color.getName() + "_linked_controller", () -> new ColoredLinkedControllerItem(new Item.Properties().stacksTo(1), color.getName())));
        }
    }

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> COLORED_CONTROLLERS_TAB = CREATIVE_MODE_TABS.register("colored_controllers_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.createcoloredcontrollers.tab"))
                    .icon(() -> COLORED_CONTROLLERS.get(DyeColor.PINK).get().getDefaultInstance())
                    .displayItems((parameters, output) -> {

                        DyeColor[] vanillaSortedColors = {DyeColor.WHITE, DyeColor.LIGHT_GRAY, DyeColor.GRAY, DyeColor.BLACK, DyeColor.BROWN, DyeColor.RED, DyeColor.ORANGE, DyeColor.YELLOW, DyeColor.LIME, DyeColor.GREEN, DyeColor.CYAN, DyeColor.LIGHT_BLUE, DyeColor.BLUE, DyeColor.PURPLE, DyeColor.MAGENTA, DyeColor.PINK};

                        for (DyeColor color : vanillaSortedColors) {
                            var itemHolder = COLORED_CONTROLLERS.get(color);
                            if (itemHolder != null) {
                                output.accept(itemHolder.get());
                            }
                        }

                    }).build());

    public CreateColoredControllers(IEventBus modEventBus, ModContainer modContainer) {
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}