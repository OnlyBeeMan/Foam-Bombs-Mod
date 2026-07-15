package net.beeman.foambombs.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class HealingFoamBlock extends PowderSnowBlock {

    public static final net.minecraft.world.level.block.state.properties.BooleanProperty PERSISTENT = net.minecraft.world.level.block.state.properties.BlockStateProperties.PERSISTENT;

    public HealingFoamBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(PERSISTENT, false));
    }

    @Override
    protected void createBlockStateDefinition(net.minecraft.world.level.block.state.StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PERSISTENT);
    }

    @Override
    public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext context) {
        return this.defaultBlockState().setValue(PERSISTENT, true);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Randomly melt/disappear only if it was NOT placed by a player
        if (!state.getValue(PERSISTENT)) {
            level.removeBlock(pos, false);
        }
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, net.minecraft.world.entity.InsideBlockEffectApplier applier, boolean isInside) {
        // Slow down movement exactly like powder snow
        entity.makeStuckInBlock(state, new Vec3(0.9D, 1.5D, 0.9D));
        
        if (!level.isClientSide() && entity instanceof LivingEntity living) {
            // Give Regeneration 1 effect while inside the foam
            living.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 0, false, true));
        }
    }

    @Override
    public net.minecraft.world.item.ItemStack pickupBlock(LivingEntity entity, net.minecraft.world.level.LevelAccessor level, BlockPos pos, BlockState state) {
        level.setBlock(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 11);
        if (!level.isClientSide()) {
            level.levelEvent(2001, pos, net.minecraft.world.level.block.Block.getId(state));
        }
        return new net.minecraft.world.item.ItemStack(net.beeman.foambombs.FoamBombs.HEALING_FOAM_ITEM_REGISTRY);
    }
}
