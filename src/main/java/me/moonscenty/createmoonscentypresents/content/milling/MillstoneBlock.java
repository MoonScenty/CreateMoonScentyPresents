package me.moonscenty.createmoonscentypresents.content.milling;

import me.moonscenty.createmoonscentypresents.content.kinetics.GrinderBlock;
import me.moonscenty.createmoonscentypresents.registry.ModBlockEntityTypes;

import net.minecraft.world.level.block.entity.BlockEntityType;

/** A stone turned by a cog below it. Nothing beyond the shared shape. */
public class MillstoneBlock extends GrinderBlock<MillstoneBlockEntity> {

    public MillstoneBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<MillstoneBlockEntity> getBlockEntityClass() {
        return MillstoneBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends MillstoneBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.MILLSTONE.get();
    }
}
