package net.beeman.foambombs.mixin;

import net.beeman.foambombs.block.HealingFoamBlock;
import net.beeman.foambombs.block.InvisibilityFoamBlock;
import net.beeman.foambombs.block.PoisonFoamBlock;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FogType;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public class FogRendererMixin {

    @Inject(method = "getFogType", at = @At("HEAD"), cancellable = true)
    private static void onGetFogType(Camera camera, CallbackInfoReturnable<FogType> cir) {
        if (camera.entity() != null && camera.entity().level() != null) {
            BlockState state = camera.entity().level().getBlockState(camera.blockPosition());
            if (state.getBlock() instanceof HealingFoamBlock ||
                state.getBlock() instanceof InvisibilityFoamBlock ||
                state.getBlock() instanceof PoisonFoamBlock) {
                // Force POWDER_SNOW fog type so Minecraft applies powder snow camera culling (blocking outside world/border faces)
                cir.setReturnValue(FogType.POWDER_SNOW);
            }
        }
    }

    @Inject(method = "computeFogColor", at = @At("RETURN"))
    private static void onComputeFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float bossColorModifier, Vector4f color, CallbackInfo ci) {
        BlockState state = level.getBlockState(camera.blockPosition());
        if (state.getBlock() instanceof HealingFoamBlock) {
            // Soft pinkish fog color
            color.set(1.0F, 0.714F, 0.757F, 1.0F);
        } else if (state.getBlock() instanceof InvisibilityFoamBlock) {
            // Soft cyan/blue fog color
            color.set(0.6F, 0.9F, 1.0F, 1.0F);
        } else if (state.getBlock() instanceof PoisonFoamBlock) {
            // Toxic green fog color
            color.set(0.2F, 0.8F, 0.2F, 1.0F);
        }
    }

    @Inject(method = "setupFog", at = @At("RETURN"))
    private static void onSetupFog(Camera camera, int renderDistance, DeltaTracker deltaTracker, float f, ClientLevel level, CallbackInfoReturnable<FogData> cir) {
        BlockState state = level.getBlockState(camera.blockPosition());
        boolean isFoam = state.getBlock() instanceof HealingFoamBlock ||
                         state.getBlock() instanceof InvisibilityFoamBlock ||
                         state.getBlock() instanceof PoisonFoamBlock;

        if (isFoam) {
            FogData fogData = cir.getReturnValue();
            if (fogData != null) {
                fogData.environmentalStart = 0.2F;
                fogData.environmentalEnd = 3.5F;
                fogData.renderDistanceStart = 0.2F;
                fogData.renderDistanceEnd = 3.5F;
                fogData.skyEnd = 0.0F;
                fogData.cloudEnd = 0.0F;

                if (state.getBlock() instanceof HealingFoamBlock) {
                    fogData.color.set(1.0F, 0.714F, 0.757F, 1.0F);
                } else if (state.getBlock() instanceof InvisibilityFoamBlock) {
                    fogData.color.set(0.6F, 0.9F, 1.0F, 1.0F);
                } else if (state.getBlock() instanceof PoisonFoamBlock) {
                    fogData.color.set(0.2F, 0.8F, 0.2F, 1.0F);
                }
            }
        }
    }
}
