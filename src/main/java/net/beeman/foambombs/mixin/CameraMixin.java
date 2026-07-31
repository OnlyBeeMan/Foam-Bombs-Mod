package net.beeman.foambombs.mixin;

import net.beeman.foambombs.block.HealingFoamBlock;
import net.beeman.foambombs.block.InvisibilityFoamBlock;
import net.beeman.foambombs.block.PoisonFoamBlock;
import net.minecraft.client.Camera;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class CameraMixin {

    @Inject(method = "getFluidInCamera", at = @At("HEAD"), cancellable = true)
    private void onGetFluidInCamera(CallbackInfoReturnable<FogType> cir) {
        Camera camera = (Camera) (Object) this;
        if (camera.entity() != null && camera.entity().level() != null) {
            BlockState state = camera.entity().level().getBlockState(camera.blockPosition());
            if (state.getBlock() instanceof HealingFoamBlock ||
                state.getBlock() instanceof InvisibilityFoamBlock ||
                state.getBlock() instanceof PoisonFoamBlock) {
                cir.setReturnValue(FogType.POWDER_SNOW);
            }
        }
    }
}
