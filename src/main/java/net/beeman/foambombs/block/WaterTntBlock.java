package net.beeman.foambombs.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

public class WaterTntBlock extends Block {

    public WaterTntBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.is(Items.POTION)) {
            PotionContents component = stack.get(DataComponents.POTION_CONTENTS);
            if (component != null && component.is(Potions.WATER)) {
                // Play sound
                level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 
                                SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
                
                // Spawn TNT entity on server side
                if (!level.isClientSide()) {
                    PrimedTnt tntEntity = new PrimedTnt(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, player);
                    net.beeman.foambombs.FoamBombs.WATER_TNT_UUIDS.add(tntEntity.getUUID());
                    level.addFreshEntity(tntEntity);
                    level.gameEvent(player, GameEvent.PRIME_FUSE, pos);
                }

                // Remove block
                level.removeBlock(pos, false);

                // Handle Survival mode bottle inventory mechanics
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                    ItemStack glassBottle = new ItemStack(Items.GLASS_BOTTLE);
                    if (stack.isEmpty()) {
                        player.setItemInHand(hand, glassBottle);
                    } else {
                        if (!player.getInventory().add(glassBottle)) {
                            player.drop(glassBottle, false);
                        }
                    }
                }

                return InteractionResult.SUCCESS;
            }
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    @Override
    protected void onProjectileHit(Level level, BlockState state, net.minecraft.world.phys.BlockHitResult hitResult, net.minecraft.world.entity.projectile.Projectile projectile) {
        if (!level.isClientSide()) {
            if (projectile instanceof net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion thrownPotion) {
                ItemStack item = thrownPotion.getItem();
                PotionContents contents = item.get(DataComponents.POTION_CONTENTS);
                if (contents != null && contents.is(Potions.WATER)) {
                    BlockPos pos = hitResult.getBlockPos();
                    level.removeBlock(pos, false);
                    
                    // Trigger sound
                    level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 
                                    SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
                                    
                    PrimedTnt tntEntity = new PrimedTnt(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 
                        projectile.getOwner() instanceof net.minecraft.world.entity.LivingEntity ? (net.minecraft.world.entity.LivingEntity) projectile.getOwner() : null);
                    net.beeman.foambombs.FoamBombs.WATER_TNT_UUIDS.add(tntEntity.getUUID());
                    level.addFreshEntity(tntEntity);
                    level.gameEvent(projectile, GameEvent.PRIME_FUSE, pos);
                    
                    // Discard the potion projectile since it broke on the TNT
                    projectile.discard();
                }
            }
        }
    }
}
