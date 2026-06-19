package hanvil.create.coloredcontrollers;

import com.simibubi.create.content.redstone.link.controller.LinkedControllerItem;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import java.util.function.Consumer;

public class ColoredLinkedControllerItem extends LinkedControllerItem {
    private final String modelKey;

    public ColoredLinkedControllerItem(Properties properties, String modelKey) {
        super(properties);
        this.modelKey = modelKey;
    }

    public String getModelKey() {
        return modelKey;
    }

    @Override @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new ColoredLinkedControllerItemRenderer(this.modelKey)));
    }
}