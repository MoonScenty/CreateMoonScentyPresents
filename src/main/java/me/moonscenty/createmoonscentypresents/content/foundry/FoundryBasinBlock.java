package me.moonscenty.createmoonscentypresents.content.foundry;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.processing.basin.BasinBlock;

import me.moonscenty.createmoonscentypresents.registry.ModBlockEntityTypes;
import me.moonscenty.createmoonscentypresents.registry.ModBlocks;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.items.ItemHandlerHelper;

/**
 * The block half of {@link FoundryBasinBlockEntity}. Create's basin with this mod's
 * block entity behind it, so things dropped in fall in.
 *
 * <p>Ported from Create: Metallurgy by Lucreeper74, MIT licensed.
 */
public class FoundryBasinBlock extends BasinBlock implements IWrenchable {

    public FoundryBasinBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends FoundryBasinBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.FOUNDRY_BASIN.get();
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter level, Entity entity) {
        super.updateEntityAfterFallOn(level, entity);
        if (!ModBlocks.FOUNDRY_BASIN.has(level.getBlockState(entity.blockPosition())))
            return;
        if (!(entity instanceof ItemEntity itemEntity) || !entity.isAlive())
            return;

        withBlockEntityDo(level, entity.blockPosition(), be -> {
            if (!(be instanceof FoundryBasinBlockEntity basin))
                return;
            ItemStack remainder = ItemHandlerHelper.insertItem(basin.getInputInventory(),
                    itemEntity.getItem().copy(), false);
            if (remainder.isEmpty()) {
                itemEntity.discard();
                return;
            }
            itemEntity.setItem(remainder);
        });
    }
}
