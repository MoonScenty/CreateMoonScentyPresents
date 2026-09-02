package me.moonscenty.createmoonscentypresents.content.tapping;

import me.moonscenty.createmoonscentypresents.registry.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

/**
 * Bores a log so a tapper has somewhere to sit.
 *
 * <p>The hole does nothing on its own and cannot be undone: a bored log is not a log
 * any more, and the only thing left to do with one is saw it into planks. That is the
 * cost of tapping - a tree you have drilled is a tree you have spent.
 */
public class HandDrillItem extends Item {

    private static final int PARTICLES = 12;

    public HandDrillItem(Properties properties) {
        // Iron, and it only ever meets wood, so it lasts a long while without lasting
        // forever. Every hole is a use. durability already pins the stack size to one.
        super(properties.durability(128));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        Block bored = ModBlocks.holedVariantOf(state.getBlock());
        if (bored == null)
            return InteractionResult.PASS;

        if (level.isClientSide) {
            spawnChips(level, context.getClickLocation(), state);
            return InteractionResult.SUCCESS;
        }

        // The log keeps whichever way it was lying; a hole does not stand it upright.
        BlockState drilled = bored.defaultBlockState()
                .setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS));
        level.setBlock(pos, drilled, Block.UPDATE_ALL);
        level.playSound(null, pos, SoundEvents.WOOD_HIT, SoundSource.BLOCKS, 1.0f, 0.8f);
        level.gameEvent(context.getPlayer(), GameEvent.BLOCK_CHANGE, pos);

        Player player = context.getPlayer();
        if (player != null)
            context.getItemInHand().hurtAndBreak(1, player,
                    context.getHand() == InteractionHand.MAIN_HAND
                            ? EquipmentSlot.MAINHAND
                            : EquipmentSlot.OFFHAND);
        return InteractionResult.CONSUME;
    }

    private static void spawnChips(Level level, Vec3 at, BlockState state) {
        RandomSource random = level.random;
        for (int i = 0; i < PARTICLES; i++) {
            double dx = (random.nextDouble() - 0.5) / 3;
            double dy = random.nextDouble() / 4;
            double dz = (random.nextDouble() - 0.5) / 3;
            level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), at.x, at.y, at.z, dx, dy, dz);
        }
    }
}
