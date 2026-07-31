package net.beeman.foambombs.mixin;

import net.beeman.foambombs.FoamBombs;
import net.beeman.foambombs.block.HealingFoamBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SulfurCubeArchetype;
import net.minecraft.world.entity.monster.cubemob.SulfurCube;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mixin(SulfurCube.class)
public class SulfurCubeMixin {

    @Shadow
    @Final
    private static EntityDataAccessor<Integer> MAX_FUSE;

    private static boolean isFoamTntItem(ItemStack stack) {
        return stack.is(FoamBombs.HEALING_FOAM_TNT_ITEM_KEY) ||
               stack.is(FoamBombs.INVISIBILITY_FOAM_TNT_ITEM_KEY) ||
               stack.is(FoamBombs.POISON_FOAM_TNT_ITEM_KEY);
    }

    private ItemStack getHeldFoamTnt(SulfurCube cube) {
        ItemStack main = cube.getItemBySlot(EquipmentSlot.MAINHAND);
        if (isFoamTntItem(main)) return main;
        ItemStack off = cube.getItemBySlot(EquipmentSlot.OFFHAND);
        if (isFoamTntItem(off)) return off;
        ItemStack body = cube.getItemBySlot(EquipmentSlot.BODY);
        if (isFoamTntItem(body)) return body;
        return ItemStack.EMPTY;
    }

    private void primeSulfurCube(SulfurCube cube) {
        SulfurCubeInvoker invoker = (SulfurCubeInvoker) cube;
        cube.getEntityData().set(MAX_FUSE, 80);
        invoker.callSetFuse(80);
        invoker.callPrimeTime(true);
    }

    @Inject(method = "matchingArchetypes", at = @At("HEAD"), cancellable = true)
    private void onMatchingArchetypes(ItemStack stack, CallbackInfoReturnable<List<SulfurCubeArchetype>> cir) {
        if (isFoamTntItem(stack)) {
            SulfurCube cube = (SulfurCube) (Object) this;
            cir.setReturnValue(cube.matchingArchetypes(new ItemStack(Items.TNT)));
        }
    }

    @Inject(method = "isSwallowableItem", at = @At("HEAD"), cancellable = true)
    private static void onIsSwallowableItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (isFoamTntItem(stack)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "canHoldItem", at = @At("HEAD"), cancellable = true)
    private void onCanHoldItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (isFoamTntItem(stack)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void onMobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        SulfurCube cube = (SulfurCube) (Object) this;
        ItemStack heldByPlayer = player.getItemInHand(hand);

        if (heldByPlayer.is(Items.POTION)) {
            PotionContents contents = heldByPlayer.get(DataComponents.POTION_CONTENTS);
            if (contents != null && contents.is(Potions.WATER)) {
                if (!getHeldFoamTnt(cube).isEmpty()) {
                    if (!cube.level().isClientSide()) {
                        primeSulfurCube(cube);
                    }
                    cube.level().playSound(null, cube.getX(), cube.getY(), cube.getZ(),
                            SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);

                    if (!player.getAbilities().instabuild) {
                        heldByPlayer.shrink(1);
                        ItemStack glassBottle = new ItemStack(Items.GLASS_BOTTLE);
                        if (heldByPlayer.isEmpty()) {
                            player.setItemInHand(hand, glassBottle);
                        } else if (!player.getInventory().add(glassBottle)) {
                            player.drop(glassBottle, false);
                        }
                    }

                    cir.setReturnValue(InteractionResult.SUCCESS);
                }
            }
        }
    }

    @Inject(method = "hurtServer", at = @At("HEAD"))
    private void onHurtServer(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        SulfurCube cube = (SulfurCube) (Object) this;
        if (source.getDirectEntity() instanceof AbstractThrownPotion thrownPotion) {
            ItemStack item = thrownPotion.getItem();
            PotionContents contents = item.get(DataComponents.POTION_CONTENTS);
            if (contents != null && contents.is(Potions.WATER)) {
                if (!getHeldFoamTnt(cube).isEmpty()) {
                    primeSulfurCube(cube);
                    level.playSound(null, cube.getX(), cube.getY(), cube.getZ(),
                            SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            }
        }
    }

    @Inject(method = "tickFuse", at = @At("HEAD"), cancellable = true)
    private void onTickFuse(CallbackInfo ci) {
        SulfurCube cube = (SulfurCube) (Object) this;
        ItemStack heldTnt = getHeldFoamTnt(cube);

        if (cube.isPrimed() && cube.getFuse() <= 1 && !heldTnt.isEmpty()) {
            Level level = cube.level();
            
            Block foamToPlace = FoamBombs.HEALING_FOAM_REGISTRY;
            if (heldTnt.is(FoamBombs.INVISIBILITY_FOAM_TNT_ITEM_KEY)) {
                foamToPlace = FoamBombs.INVISIBILITY_FOAM_REGISTRY;
            } else if (heldTnt.is(FoamBombs.POISON_FOAM_TNT_ITEM_KEY)) {
                foamToPlace = FoamBombs.POISON_FOAM_REGISTRY;
            }

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
                                double x = cube.getX();
                                double y = cube.getY(0.0625D);
                                double z = cube.getZ();
                                
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
                
                level.playSound(null, cube.getX(), cube.getY(), cube.getZ(),
                        SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 4.0F,
                        (1.0F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F) * 0.7F);
            }

            cube.discard();
            ci.cancel();
        }
    }
}
