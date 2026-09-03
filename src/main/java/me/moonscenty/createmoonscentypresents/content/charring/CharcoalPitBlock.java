package me.moonscenty.createmoonscentypresents.content.charring;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.block.IBE;

import me.moonscenty.createmoonscentypresents.content.firing.KilnBlock;
import me.moonscenty.createmoonscentypresents.content.processing.HorizontalCubeBlock;
import me.moonscenty.createmoonscentypresents.registry.ModBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Wood packed into a pit and lit, to smoulder behind brick.
 *
 * <p>Unlike the kiln this carries its own fire rather than standing over one. A charcoal
 * burn is not a thing you keep a flame under - it is lit once and left, and the heat that
 * chars the wood is the wood itself going. So there is nothing to build around it at all:
 * pack it, strike it, walk away.
 *
 * <p>The fire goes out when the load does. A pit with nothing left in it has nothing
 * left to burn, so every batch is struck fresh.
 */
public class CharcoalPitBlock extends HorizontalCubeBlock implements KilnBlock<CharcoalPitBlockEntity> {

    public static final MapCodec<CharcoalPitBlock> CODEC = simpleCodec(CharcoalPitBlock::new);

    /**
     * Create's own burner property, carried by this block on purpose.
     *
     * <p>A basin asks the block under it for {@code HEAT_LEVEL} before anything else, so
     * wearing the property is what makes a pit a fire that a foundry can stand on -
     * without a mixin, and without pretending to be in a tag it is not in.
     *
     * <p>Three of the five rungs are used: none is a cold pit, smouldering is a lit one,
     * and kindled is one with somebody working a bellows on it.
     */
    public static final EnumProperty<BlazeBurnerBlock.HeatLevel> HEAT_LEVEL = BlazeBurnerBlock.HEAT_LEVEL;

    public CharcoalPitBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(HEAT_LEVEL, BlazeBurnerBlock.HeatLevel.NONE));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HEAT_LEVEL);
    }

    public static boolean isLit(BlockState state) {
        return state.hasProperty(HEAT_LEVEL) && state.getValue(HEAT_LEVEL) != BlazeBurnerBlock.HeatLevel.NONE;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.is(Items.FLINT_AND_STEEL))
            return strike(stack, state, level, pos, player, hand);
        return load(stack, level, pos, player);
    }

    /**
     * Puts a flame to the load.
     *
     * <p>Refused when there is nothing in it or it is already going, and the click is
     * eaten either way rather than passed on - letting it through would have vanilla
     * set a fire on top of the pit, which is the one place a fire does no good.
     */
    private ItemInteractionResult strike(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand) {
        CharcoalPitBlockEntity pit = getBlockEntity(level, pos);
        if (pit == null || isLit(state) || pit.getLoad().isEmpty())
            return ItemInteractionResult.CONSUME;
        if (level.isClientSide)
            return ItemInteractionResult.SUCCESS;

        level.setBlock(pos, state.setValue(HEAT_LEVEL, BlazeBurnerBlock.HeatLevel.SMOULDERING),
                Block.UPDATE_ALL);
        level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F,
                level.random.nextFloat() * 0.4F + 0.8F);
        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        stack.hurtAndBreak(1, player,
                hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        return ItemInteractionResult.CONSUME;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hit) {
        return unload(level, pos, player);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (!state.is(newState.getBlock()))
            IBE.onRemove(state, level, pos, newState);
        super.onRemove(state, level, pos, newState, moving);
    }

    @Override
    public Class<CharcoalPitBlockEntity> getBlockEntityClass() {
        return CharcoalPitBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CharcoalPitBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.CHARCOAL_PIT.get();
    }
}
