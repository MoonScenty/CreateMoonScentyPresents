package me.moonscenty.createmoonscentypresents.content.processing;

import com.simibubi.create.AllShapes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A plain block that borrows Create's basin geometry - the shape only, none of the
 * basin's processing behaviour.
 * <p>
 * The shape is not cosmetic. Left as a default full cube the game treats the whole
 * cell as filled and shades the inward-facing interior walls as if they were buried,
 * so the inside renders black. Create's basin shape has the middle carved out, which
 * is what lets light reach it.
 */
public class BasinShapedBlock extends Block {

    public BasinShapedBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return AllShapes.BASIN_BLOCK_SHAPE;
    }

    /** Solid to the cursor, so the hollow does not swallow clicks. */
    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return AllShapes.BASIN_RAYTRACE_SHAPE;
    }

    /** Dropped items settle inside instead of resting on the rim. */
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext ctx && ctx.getEntity() instanceof ItemEntity)
            return AllShapes.BASIN_COLLISION_SHAPE;
        return getShape(state, level, pos, context);
    }
}
