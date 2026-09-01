package me.moonscenty.createmoonscentypresents.content.kinetics;

import com.simibubi.create.content.kinetics.gearbox.GearboxBlock;
import com.simibubi.create.content.kinetics.gearbox.GearboxBlockEntity;

import java.util.List;

import me.moonscenty.createmoonscentypresents.registry.ModBlockEntityTypes;
import me.moonscenty.createmoonscentypresents.registry.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.HitResult;

/**
 * Create's gearbox, rebound to this mod's block entity type.
 * <p>
 * Everything else - the axis handling, the way rotation is split and reversed across
 * the four sides, the shafts drawn on each face - is inherited unchanged. Only
 * {@link #getBlockEntityType()} has to be overridden, because Create's own type only
 * accepts Create's blocks.
 *
 * @see ModCogwheelBlock the same arrangement for the cogwheel
 */
public class ModGearboxBlock extends GearboxBlock {

    public ModGearboxBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends GearboxBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.GEARBOX.get();
    }

    // A gearbox lying on a horizontal axis is the vertical gearbox, and hands back that
    // item rather than the one in its loot table. Create's version of these two names
    // Create's item, which is what a broken primitive gearbox was dropping.

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        if (state.getValue(AXIS).isVertical())
            return super.getDrops(state, builder);
        return List.of(new ItemStack(ModItems.PRIMITIVE_VERTICAL_GEARBOX.get()));
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos,
            Player player) {
        if (state.getValue(AXIS).isVertical())
            return super.getCloneItemStack(state, target, level, pos, player);
        return new ItemStack(ModItems.PRIMITIVE_VERTICAL_GEARBOX.get());
    }
}
