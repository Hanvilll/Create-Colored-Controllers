package hanvil.create.coloredcontrollers.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.redstone.link.controller.LecternControllerBlockEntity;
import com.simibubi.create.content.redstone.link.controller.LecternControllerBlock;
import com.simibubi.create.content.redstone.link.controller.LecternControllerRenderer;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerClientHandler;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import hanvil.create.coloredcontrollers.ColoredLinkedControllerItem;
import hanvil.create.coloredcontrollers.ColoredLinkedControllerItemRenderer;
import hanvil.create.coloredcontrollers.CreateColoredControllers;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = LecternControllerRenderer.class, remap = false)
public class LecternControllerRendererMixin {
    @Unique private static final Map<String, PartialModel> coloredcontrollers$POWERED_MODELS = new HashMap<>();

    @Unique private static PartialModel coloredcontrollers$getPoweredModel(String modelKey) {
        return coloredcontrollers$POWERED_MODELS.computeIfAbsent(modelKey, key ->
                PartialModel.of(ResourceLocation.parse(CreateColoredControllers.MODID + ":item/" + key + "_linked_controller_powered"))
        );
    }

    @Inject(method = "renderSafe", at = @At("HEAD"), cancellable = true)
    private void renderColoredControllerOnLectern(LecternControllerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay, CallbackInfo ci) {
        ItemStack stack = be.getController();

        if (stack.getItem() instanceof ColoredLinkedControllerItem coloredItem) {
            ItemDisplayContext transformType = ItemDisplayContext.NONE;
            CustomRenderedItemModel mainModel = (CustomRenderedItemModel) Minecraft.getInstance().getItemRenderer().getModel(stack, be.getLevel(), null, 0);
            PartialItemModelRenderer renderer = PartialItemModelRenderer.of(stack, transformType, ms, buffer, overlay);

            boolean active = be.hasUser();
            boolean renderDepression = be.isUsedBy(Minecraft.getInstance().player);
            Direction facing = be.getBlockState().getValue(LecternControllerBlock.FACING);

            PoseTransformStack msr = TransformStack.of(ms);
            ms.pushPose();

            msr.translate(0.5F, 1.45F, 0.5F);
            msr.rotateYDegrees(AngleHelper.horizontalAngle(facing) - 90.0F);
            msr.translate(0.28F, 0.0F, 0.0F);
            msr.rotateZDegrees(-22.0F);

            coloredcontrollers$renderInLectern(stack, coloredItem, mainModel, renderer, ms, light, active, renderDepression);

            ms.popPose();
            ci.cancel();
        }
    }

    @Unique
    private static void coloredcontrollers$renderInLectern(ItemStack stack, ColoredLinkedControllerItem coloredItem, CustomRenderedItemModel model, PartialItemModelRenderer renderer, PoseStack ms, int light, boolean active, boolean renderDepression) {
        float pt = AnimationTickHolder.getPartialTicks();
        PoseTransformStack msr = TransformStack.of(ms);

        if (active) {
            PartialModel powered = coloredcontrollers$getPoweredModel(coloredItem.getModelKey());
            renderer.render(powered.get(), light);
        } else {
            renderer.render(model.getOriginalModel(), light);
        }

        if (active) {
            BakedModel button = ColoredLinkedControllerItemRenderer.VANILLA_BUTTON.get();
            float s = 0.0625F;
            float b = s * -0.75F;
            int index = 0;

            if (LinkedControllerClientHandler.MODE == LinkedControllerClientHandler.Mode.BIND) {
                int i = (int) Mth.lerp((Mth.sin(AnimationTickHolder.getRenderTime() / 4.0F) + 1.0F) / 2.0F, 5.0F, 15.0F);
                light = i << 20;
            }

            ms.pushPose();
            msr.translate(2.0F * s, 0.0F, 8.0F * s);
            coloredcontrollers$renderButton(renderer, ms, light, pt, button, b, index++, renderDepression);
            msr.translate(4.0F * s, 0.0F, 0.0F);
            coloredcontrollers$renderButton(renderer, ms, light, pt, button, b, index++, renderDepression);
            msr.translate(-2.0F * s, 0.0F, 2.0F * s);
            coloredcontrollers$renderButton(renderer, ms, light, pt, button, b, index++, renderDepression);
            msr.translate(0.0F, 0.0F, -4.0F * s);
            coloredcontrollers$renderButton(renderer, ms, light, pt, button, b, index++, renderDepression);
            ms.popPose();

            ms.pushPose();
            msr.translate(3.0F * s, 0.0F, 3.0F * s);
            coloredcontrollers$renderButton(renderer, ms, light, pt, button, b, index++, renderDepression);
            msr.translate(2.0F * s, 0.0F, 0.0F);
            coloredcontrollers$renderButton(renderer, ms, light, pt, button, b, index++, renderDepression);
            ms.popPose();
        }
    }

    @Unique
    private static void coloredcontrollers$renderButton(PartialItemModelRenderer renderer, PoseStack ms, int light, float pt, BakedModel button, float b, int index, boolean renderDepression) {
        ms.pushPose();
        if (renderDepression) {
            float depression = b * ColoredLinkedControllerItemRenderer.buttons.get(index).getValue(pt);
            ms.translate(0.0F, depression, 0.0F);
        }
        renderer.renderSolid(button, light);
        ms.popPose();
    }
}