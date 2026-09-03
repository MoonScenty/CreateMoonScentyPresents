package me.moonscenty.createmoonscentypresents.content.casting;

import com.simibubi.create.AllShapes;
import com.simibubi.create.foundation.block.IBE;

import me.moonscenty.createmoonscentypresents.registry.ModBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The table a mould sits on while metal is poured into it.
 *
 * <p>Everything happens on the top face: an empty hand puts a mould down, and once the
 * casting has set the same click takes it back. The table refuses to be handled while
 * there is fluid standing in it - reaching into molten metal is not a thing you do.
 *
 * <p>Reworked from Create: Metallurgy by Lucreeper74, MIT licensed.
 */
public class CastingTableBlock extends Block implements IBE<CastingTableBlockEntity> {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public CastingTableBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return AllShapes.CASING_14PX.get(Direction.UP);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hit) {
        if (hit.getDirection() != Direction.UP)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (level.isClientSide)
            return ItemInteractionResult.SUCCESS;

        return onBlockEntityUseItemOn(level, pos, table -> {
            // Taking the casting comes first, and it comes before the check below: a
            // finished casting is a solid thing sitting on top, so whatever is left in
            // the table underneath is no reason to refuse to hand it over. A table that
            // had been overfilled used to strand its own casting exactly here.
            if (!table.getResult().isEmpty()) {
                player.getInventory().placeItemBackInInventory(table.getResult());
                table.resultInv.setStackInSlot(0, ItemStack.EMPTY);
                table.notifyUpdate();
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f,
                        1f + level.getRandom().nextFloat());
                return ItemInteractionResult.SUCCESS;
            }

            // Reaching into molten metal is not a thing you do, so the mould stays put
            // while there is any standing in the table.
            if (!table.getFluidTank().isEmpty())
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

            if (!stack.isEmpty() && table.getMold().isEmpty()) {
                table.moldInv.setStackInSlot(0, stack.copyWithCount(1));
                stack.shrink(1);
                table.notifyUpdate();
                level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.PLAYERS, 1f,
                        1f + level.getRandom().nextFloat());
                return ItemInteractionResult.SUCCESS;
            }

            if (!table.getMold().isEmpty()) {
                player.getInventory().placeItemBackInInventory(table.getMold());
                table.moldInv.setStackInSlot(0, ItemStack.EMPTY);
                table.reset();
                table.notifyUpdate();
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .2f,
                        1f + level.getRandom().nextFloat());
                return ItemInteractionResult.SUCCESS;
            }

            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        });
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        super.createBlockStateDefinition(builder);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (!state.is(newState.getBlock()))
            IBE.onRemove(state, level, pos, newState);
        super.onRemove(state, level, pos, newState, moving);
    }

    @Override
    public Class<CastingTableBlockEntity> getBlockEntityClass() {
        return CastingTableBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CastingTableBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.CASTING_TABLE.get();
    }
}
