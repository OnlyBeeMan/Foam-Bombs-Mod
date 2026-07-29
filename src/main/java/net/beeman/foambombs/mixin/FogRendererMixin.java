package net.beeman.foambombs.mixin;

import net.beeman.foambombs.block.HealingFoamBlock;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public class FogRendererMixin {

    @Inject(method = "computeFogColor", at = @At("RETURN"))
    private static void onComputeFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float bossColorModifier, Vector4f color, CallbackInfo ci) {
        BlockState state = level.getBlockState(camera.blockPosition());
        if (state.getBlock() instanceof HealingFoamBlock) {
            // Soft pinkish fog color (R: 1.0, G: 0.714, B: 0.757)
            color.set(1.0F, 0.714F, 0.757F, 1.0F);
        }
    }

    @Inject(method = "setupFog", at = @At("RETURN"))
    private static void onSetupFog(Camera camera, int renderDistance, DeltaTracker deltaTracker, float f, ClientLevel level, CallbackInfoReturnable<FogData> cir) {
        BlockState state = level.getBlockState(camera.blockPosition());
        if (state.getBlock() instanceof HealingFoamBlock) {
            FogData fogData = cir.getReturnValue();
            if (fogData != null) {
                // Set dense fog distances matching powder snow fog effect
                fogData.environmentalStart = -8.0F;
                fogData.environmentalEnd = 2.0F;
                fogData.renderDistanceStart = -8.0F;
                fogData.renderDistanceEnd = 2.0F;
                fogData.skyEnd = 0.0F;
                fogData.cloudEnd = 0.0F;
                fogData.color.set(1.0F, 0.714F, 0.757F, 1.0F);
            }
        }
    }
}
