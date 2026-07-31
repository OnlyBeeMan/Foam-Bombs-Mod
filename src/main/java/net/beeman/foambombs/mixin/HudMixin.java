package net.beeman.foambombs.mixin;

import net.beeman.foambombs.block.HealingFoamBlock;
import net.beeman.foambombs.block.InvisibilityFoamBlock;
import net.beeman.foambombs.block.PoisonFoamBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Hud.class)
public abstract class HudMixin {

    @Shadow @Final
    private Minecraft minecraft;

    @Shadow
    private void extractTextureOverlay(GuiGraphicsExtractor guiGraphicsExtractor, Identifier identifier, float alpha) {}

    private static final Identifier HEALING_FOAM_OUTLINE = Identifier.fromNamespaceAndPath("foambombs", "textures/misc/healing_foam_outline.png");
    private static final Identifier INVISIBILITY_FOAM_OUTLINE = Identifier.fromNamespaceAndPath("foambombs", "textures/misc/invisibility_foam_outline.png");
    private static final Identifier POISON_FOAM_OUTLINE = Identifier.fromNamespaceAndPath("foambombs", "textures/misc/poison_foam_outline.png");

    @Inject(method = "extractCameraOverlays", at = @At("TAIL"))
    private void onExtractCameraOverlays(GuiGraphicsExtractor guiGraphicsExtractor, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (this.minecraft.gameRenderer != null && this.minecraft.gameRenderer.mainCamera() != null) {
            var camera = this.minecraft.gameRenderer.mainCamera();
            if (camera.entity() != null && camera.entity().level() != null) {
                BlockState state = camera.entity().level().getBlockState(camera.blockPosition());
                if (state.getBlock() instanceof HealingFoamBlock) {
                    this.extractTextureOverlay(guiGraphicsExtractor, HEALING_FOAM_OUTLINE, 1.0F);
                } else if (state.getBlock() instanceof InvisibilityFoamBlock) {
                    this.extractTextureOverlay(guiGraphicsExtractor, INVISIBILITY_FOAM_OUTLINE, 1.0F);
                } else if (state.getBlock() instanceof PoisonFoamBlock) {
                    this.extractTextureOverlay(guiGraphicsExtractor, POISON_FOAM_OUTLINE, 1.0F);
                }
            }
        }
    }
}
