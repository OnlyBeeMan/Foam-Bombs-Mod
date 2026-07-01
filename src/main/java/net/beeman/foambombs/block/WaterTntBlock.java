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
}
