package net.beeman.foambombs.mixin;

import net.beeman.foambombs.FoamBombs;
import net.beeman.foambombs.block.HealingFoamBlock;
import net.beeman.foambombs.block.HealingFoamTntBlock;
import net.beeman.foambombs.block.InvisibilityFoamBlock;
import net.beeman.foambombs.block.InvisibilityFoamTntBlock;
import net.beeman.foambombs.block.PoisonFoamBlock;
import net.beeman.foambombs.block.PoisonFoamTntBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(PrimedTnt.class)
public class PrimedTntMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTick(CallbackInfo ci) {
        PrimedTnt tnt = (PrimedTnt) (Object) this;
        
        if (tnt.getFuse() <= 1) {
            Block tntBlock = tnt.getBlockState().getBlock();
            Block foamToPlace = null;

            if (tntBlock instanceof HealingFoamTntBlock || FoamBombs.HEALING_FOAM_TNT_UUIDS.contains(tnt.getUUID())) {
                foamToPlace = FoamBombs.HEALING_FOAM_REGISTRY;
            } else if (tntBlock instanceof InvisibilityFoamTntBlock) {
                foamToPlace = FoamBombs.INVISIBILITY_FOAM_REGISTRY;
            } else if (tntBlock instanceof PoisonFoamTntBlock) {
                foamToPlace = FoamBombs.POISON_FOAM_REGISTRY;
            }

            if (foamToPlace != null) {
                Level level = tnt.level();
                
                if (!level.isClientSide()) {
                    Set<BlockPos> affectedBlocks = new HashSet<>();
                    float radius = 1.5F;
                    
                    for (int j = 0; j < 16; ++j) {
                        for (int k = 0; k < 16; ++k) {
                            for (int l = 0; l < 16; ++l) {
                                if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                                    double dx = (float)j / 15.0F * 2.0F - 1.0F;
                                    double dy = (float)k / 15.0F * 2.0F - 1.0F;
                                    double dz = (float)l / 15.0F * 2.0F - 1.0F;
                                    double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                                    dx /= dist;
                                    dy /= dist;
                                    dz /= dist;
                                    
                                    float power = radius * (0.7F + level.getRandom().nextFloat() * 0.6F);
                                    double x = tnt.getX();
                                    double y = tnt.getY(0.0625D);
                                    double z = tnt.getZ();
                                    
                                    for (; power > 0.0F; power -= 0.225F) {
                                        BlockPos pos = BlockPos.containing(x, y, z);
                                        if (!level.isInWorldBounds(pos)) break;
                                        
                                        BlockState state = level.getBlockState(pos);
                                        boolean isReplaceable = state.isAir() || state.is(Blocks.SHORT_GRASS) || state.is(Blocks.TALL_GRASS);
                                        
                                        if (isReplaceable) {
                                            affectedBlocks.add(pos);
                                        } else {
                                            power = 0.0F;
                                        }
                                        
                                        x += dx * 0.3D;
                                        y += dy * 0.3D;
                                        z += dz * 0.3D;
                                    }
                                }
                            }
                        }
                    }
                    
                    for (BlockPos pos : affectedBlocks) {
                        level.setBlock(pos, foamToPlace.defaultBlockState().setValue(HealingFoamBlock.PERSISTENT, false), 3);
                    }
                    
                    level.playSound(null, tnt.getX(), tnt.getY(), tnt.getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 4.0F, (1.0F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F) * 0.7F);
                    FoamBombs.HEALING_FOAM_TNT_UUIDS.remove(tnt.getUUID());
                }
                
                tnt.discard();
                ci.cancel();
            }
        }
    }
}
