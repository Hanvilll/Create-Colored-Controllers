package hanvil.create.coloredcontrollers;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@Mod(value = CreateColoredControllers.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = CreateColoredControllers.MODID, value = Dist.CLIENT)
public class CreateColoredControllersClient {
    @SubscribeEvent
    static void onClientTick(ClientTickEvent.Post event) {
        ColoredLinkedControllerItemRenderer.tick();
    }
}