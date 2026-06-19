package hanvil.create.coloredcontrollers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerClientHandler;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerClientHandler.Mode;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerItem;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import java.util.ArrayList;
import java.util.List;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.animation.LerpedFloat.Chaser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ColoredLinkedControllerItemRenderer extends CustomRenderedItemModelRenderer {
    public static final PartialModel VANILLA_BUTTON = PartialModel.of(ResourceLocation.parse("create:item/linked_controller/button"));

    public static final LerpedFloat equipProgress = LerpedFloat.linear().startWithValue(0.0F);
    public static final List<LerpedFloat> buttons = new ArrayList<>(6);

    static {
        for (int i = 0; i < 6; ++i) {
            buttons.add(LerpedFloat.linear().startWithValue(0.0F));
        }
    }

    private final PartialModel poweredModel;

    public ColoredLinkedControllerItemRenderer(String modelKey) {
        this.poweredModel = PartialModel.of(ResourceLocation.parse(CreateColoredControllers.MODID + ":item/" + modelKey + "_linked_controller_powered"));
    }

    public static void tick() {
        if (!Minecraft.getInstance().isPaused()) {
            boolean active = LinkedControllerClientHandler.MODE != Mode.IDLE;
            equipProgress.chase(active ? 1.0F : 0.0F, 0.2F, Chaser.EXP);
            equipProgress.tickChaser();
            if (active) {
                for (int i = 0; i < buttons.size(); ++i) {
                    LerpedFloat lerpedFloat = buttons.get(i);
                    lerpedFloat.chase(LinkedControllerClientHandler.currentlyPressed.contains(i) ? 1.0F : 0.0F, 0.4F, Chaser.EXP);
                    lerpedFloat.tickChaser();
                }
            }
        }
    }

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        float pt = AnimationTickHolder.getPartialTicks();
        PoseTransformStack msr = TransformStack.of(ms);
        ms.pushPose();

        boolean active = false;
        boolean renderDepression = false;

        if (transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || transformType == ItemDisplayContext.GUI) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player != null) {
                boolean rightHanded = mc.options.mainHand().get() == HumanoidArm.RIGHT;

                ItemDisplayContext mainHand = rightHanded ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
                ItemDisplayContext offHand = rightHanded ? ItemDisplayContext.FIRST_PERSON_LEFT_HAND : ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;

                boolean noControllerInMain = !(mc.player.getMainHandItem().getItem() instanceof LinkedControllerItem);

                if (transformType == mainHand || (transformType == offHand && noControllerInMain)) {
                    float equip = equipProgress.getValue(pt);
                    int handModifier = transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ? -1 : 1;
                    msr.translate(0.0F, equip / 4.0F, equip / 4.0F * handModifier);
                    msr.rotateYDegrees(equip * -30.0F * handModifier);
                    msr.rotateZDegrees(equip * -30.0F);

                    active = true;
                }

                if (transformType == ItemDisplayContext.GUI) {
                    msr.translate(0.5F, 0.5F, 0.5F);
                    msr.scale(1.25F, 1.25F, 1.25F);
                    msr.translate(-0.5F, -0.45F, -0.5F);

                    if (stack == mc.player.getMainHandItem() || (stack == mc.player.getOffhandItem() && noControllerInMain)) {
                        active = true;
                    }
                }

                active &= LinkedControllerClientHandler.MODE != Mode.IDLE;
                renderDepression = true;
            }
        }

        if (active) {
            renderer.render(this.poweredModel.get(), light);
        } else {
            renderer.render(model.getOriginalModel(), light);
        }

        if (!active) {
            ms.popPose();
        } else {
            BakedModel button = VANILLA_BUTTON.get();
            float s = 0.0625F;
            float b = s * -0.75F;
            int index = 0;

            if (LinkedControllerClientHandler.MODE == Mode.BIND) {
                int i = (int) Mth.lerp((Mth.sin(AnimationTickHolder.getRenderTime() / 4.0F) + 1.0F) / 2.0F, 5.0F, 15.0F);
                light = i << 20;
            }

            ms.pushPose();
            msr.translate(2.0F * s, 0.0F, 8.0F * s);
            renderButton(renderer, ms, light, pt, button, b, index++, renderDepression);
            msr.translate(4.0F * s, 0.0F, 0.0F);
            renderButton(renderer, ms, light, pt, button, b, index++, renderDepression);
            msr.translate(-2.0F * s, 0.0F, 2.0F * s);
            renderButton(renderer, ms, light, pt, button, b, index++, renderDepression);
            msr.translate(0.0F, 0.0F, -4.0F * s);
            renderButton(renderer, ms, light, pt, button, b, index++, renderDepression);
            ms.popPose();
            msr.translate(3.0F * s, 0.0F, 3.0F * s);
            renderButton(renderer, ms, light, pt, button, b, index++, renderDepression);
            msr.translate(2.0F * s, 0.0F, 0.0F);
            renderButton(renderer, ms, light, pt, button, b, index++, renderDepression);
            ms.popPose();
        }
    }

    private static void renderButton(PartialItemModelRenderer renderer, PoseStack ms, int light, float pt, BakedModel button, float b, int index, boolean renderDepression) {
        ms.pushPose();

        if (renderDepression) {
            float depression = b * buttons.get(index).getValue(pt);
            ms.translate(0.0F, depression, 0.0F);
        }
        renderer.renderSolid(button, light);
        ms.popPose();
    }
}