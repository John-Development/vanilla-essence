package net.vanillaEssence.event;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Matrix4f;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.malilib.util.Color4f;
import net.vanillaEssence.config.Configs;
import net.vanillaEssence.config.FeatureToggle;
//import fi.dy.masa.tweakeroo.config.Configs;
//import fi.dy.masa.tweakeroo.config.FeatureToggle;
//import fi.dy.masa.tweakeroo.config.Hotkeys;
//import fi.dy.masa.tweakeroo.renderer.RenderUtils;

public class RenderHandler implements IRenderer
{
    @Override
    public void onRenderWorldLast(MatrixStack matrixStack, Matrix4f projMatrix)
    {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.player != null)
        {
            this.renderOverlays(matrixStack, mc);
        }
    }

    private void renderOverlays(MatrixStack matrixStack, MinecraftClient mc)
    {
        Entity entity = mc.getCameraEntity();

        if (
                FeatureToggle.TWEAK_FLEXIBLE_BLOCK_PLACEMENT.getBooleanValue() &&
                entity != null &&
                mc.crosshairTarget != null &&
                mc.crosshairTarget.getType() == HitResult.Type.BLOCK
        ) {
            BlockHitResult hitResult = (BlockHitResult) mc.crosshairTarget;
            RenderSystem.depthMask(false);
            RenderSystem.disableCull();
            RenderSystem.disableTexture();
            RenderSystem.disableDepthTest();

            fi.dy.masa.malilib.render.RenderUtils.setupBlend();

            Color4f color = Configs.Generic.FLEXIBLE_PLACEMENT_OVERLAY_COLOR.getColor();

            fi.dy.masa.malilib.render.RenderUtils.renderBlockTargetingOverlay(
                    entity,
                    hitResult.getBlockPos(),
                    hitResult.getSide(),
                    hitResult.getPos(),
                    color,
                    matrixStack,
                    mc);

            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
            RenderSystem.depthMask(true);
        }
    }
}
