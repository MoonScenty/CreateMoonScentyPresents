package me.moonscenty.createmoonscentypresents.content.kinetics;

import com.simibubi.create.content.kinetics.crank.HandCrankBlock;
import com.simibubi.create.content.kinetics.crank.HandCrankBlockEntity;

import me.moonscenty.createmoonscentypresents.registry.ModBlockEntityTypes;

import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Create's hand crank, rebound to this mod's block entity type.
 * <p>
 * The speed it turns at is Create's own 32 RPM, which is already the stone age cap, so
 * nothing about the rotation needed changing.
 */
public class ModHandCrankBlock extends HandCrankBlock {

    public ModHandCrankBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Class<HandCrankBlockEntity> getBlockEntityClass() {
        return HandCrankBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends HandCrankBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.HAND_CRANK.get();
    }
}
