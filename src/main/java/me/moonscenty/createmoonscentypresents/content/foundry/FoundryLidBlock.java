package me.moonscenty.createmoonscentypresents.content.foundry;

import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;

import me.moonscenty.createmoonscentypresents.registry.ModBlockEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemHandlerHelper;

/**
 * The lid over a foundry basin.
 *
 * <p>Sneak and click to swing it open or shut, or wire it to redstone. Open it and the
 * melt stops, but the basin below can be reached through the top - which is the only
 * way to load one by hand. A wrench swaps the plain top for a windowed one.
 *
 * <p>Ported from Create: Metallurgy by Lucreeper74, MIT licensed.
 */
public class FoundryLidBlock extends HorizontalDirectionalBlock
        implements IBE<FoundryLidBlockEntity>, IWrenchable {

    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty WINDOW = BooleanProperty.create("window");

    public static final MapCodec<FoundryLidBlock> CODEC = simpleCodec(FoundryLidBlock::new);

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 6, 16);

    public FoundryLidBlock(Properties properties) {
        super(properties);
        registerDefaultState(super.defaultBlockState()
                .setValue(OPEN, false)
                .setValue(POWERED, false)
                .setValue(WINDOW, false));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    private void playSound(@Nullable Player player, Level level, BlockPos pos, boolean opened) {
        level.playSound(player, pos,
                opened ? BlockSetType.IRON.trapdoorOpen() : BlockSetType.IRON.trapdoorClose(),
                SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
        level.gameEvent(player, opened ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos,
            boolean moving) {
        boolean powered = level.hasNeighborSignal(pos);
        if (powered == state.getValue(POWERED)) {
            level.setBlock(pos, state, Block.UPDATE_CLIENTS);
            return;
        }
        if (powered != state.getValue(OPEN))
            playSound(null, level, pos, powered);
        level.setBlock(pos, state.setValue(POWERED, powered).setValue(OPEN, powered), Block.UPDATE_CLIENTS);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        level.setBlockAndUpdate(pos, state.cycle(WINDOW));
        level.playSound(null, pos, SoundEvents.NETHERITE_BLOCK_PLACE, SoundSource.PLAYERS, 1f,
                .2f + level.getRandom().nextFloat());
        return InteractionResult.SUCCESS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide() || hand != InteractionHand.MAIN_HAND)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        boolean open = state.getValue(OPEN);
        if (player.isCrouching()) {
            level.setBlock(pos, state.setValue(OPEN, !open), Block.UPDATE_ALL);
            playSound(null, level, pos, !open);
            return ItemInteractionResult.SUCCESS;
        }

        // Loading by hand only works through an open lid, which is what keeps a running
        // melt sealed.
        if (stack.isEmpty() || !open)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (!(level.getBlockEntity(pos.below()) instanceof FoundryBasinBlockEntity basin))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        ItemStack remainder = ItemHandlerHelper.insertItem(basin.getInputInventory(), stack, false);
        player.setItemInHand(hand, stack.split(remainder.getCount()));
        level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.PLAYERS, 1f,
                1f + level.getRandom().nextFloat());
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN, POWERED, WINDOW);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Player player = context.getPlayer();
        boolean flip = player != null && player.isShiftKeyDown();
        return defaultBlockState().setValue(FACING,
                flip ? context.getHorizontalDirection().getOpposite() : context.getHorizontalDirection());
    }

    @Override
    public Class<FoundryLidBlockEntity> getBlockEntityClass() {
        return FoundryLidBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends FoundryLidBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.FOUNDRY_LID.get();
    }
}
