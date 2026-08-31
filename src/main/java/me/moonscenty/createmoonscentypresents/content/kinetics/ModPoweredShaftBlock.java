package me.moonscenty.createmoonscentypresents.content.kinetics;

import java.util.function.Supplier;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.steamEngine.PoweredShaftBlock;

import me.moonscenty.createmoonscentypresents.registry.ModBlockEntityTypes;

import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * The powered form of a {@link ModShaftBlock}, created when an engine is placed
 * beside one. Has no item of its own; it only ever appears through that swap.
 * <p>
 * Each one names the plain shaft it reverts to, so every path back to an item -
 * breaking, middle click, the engine going away - lands on the right age.
 */
public class ModPoweredShaftBlock extends PoweredShaftBlock {

    private final Supplier<? extends Block> plainVariant;

    public ModPoweredShaftBlock(Properties properties, Supplier<? extends Block> plainVariant) {
        super(properties);
        this.plainVariant = plainVariant;
    }

    /** The plain shaft of the same age. */
    public Block getPlainVariant() {
        return plainVariant.get();
    }

    /** Middle click. The parent hands out Create's shaft, which would skip several ages. */
    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos,
            Player player) {
        return new ItemStack(getPlainVariant());
    }

    /**
     * Reverts once the engine is gone. The parent always places Create's shaft here,
     * and unlike the shaft -> powered direction this does not go through
     * {@code getEquivalent}, so it cannot be handled by a mixin.
     */
    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (stillValid(state, level, pos))
            return;
        level.setBlock(pos, getPlainVariant().defaultBlockState()
                .setValue(AXIS, state.getValue(AXIS))
                .setValue(BlockStateProperties.WATERLOGGED, state.getValue(BlockStateProperties.WATERLOGGED)), 3);
    }

    /** The parent reaches for Create's placement helper id; this mod has its own. */
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.isShiftKeyDown() || !player.mayBuild())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        IPlacementHelper helper = PlacementHelpers.get(ModShaftBlock.placementHelperId);
        if (helper.matchesItem(stack))
            return helper.getOffset(player, level, state, pos, hitResult)
                    .placeInWorld(level, (BlockItem) stack.getItem(), player, hand, hitResult);

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public BlockEntityType<? extends KineticBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.POWERED_SHAFT.get();
    }
}
