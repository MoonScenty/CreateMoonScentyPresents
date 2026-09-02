package me.moonscenty.createmoonscentypresents.content.applying;

import java.util.List;
import java.util.Optional;

import me.moonscenty.createmoonscentypresents.registry.ModDataComponents;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * The brush the stone age applies substances with.
 *
 * <p>It holds one kind of substance - as many as that item stacks to - and is otherwise
 * inert: the brush is the reusable part and the load is what gets spent. Adding tar or
 * wax later needs nothing but a recipe that names them, since the brush does not care
 * what it is carrying.
 *
 * <p>Loading is the same two handed gesture the rest of the mod uses: brush in one hand,
 * substance in the other, right click away from a block. Crouching empties it back into
 * the inventory. Only items that some applying recipe names can go in, so a brush can
 * never be filled with something that will do nothing.
 *
 * <p>Applying is a right click and hold on a block. The target is re-read every tick
 * from where the player is looking, the way vanilla brushing does it, so glancing away
 * cancels the work rather than treating a block behind your back.
 *
 * <p>The animation is vanilla too. {@link UseAnim#BRUSH} drives the first person motion
 * for any item that asks for it, and the third person sweep comes from the
 * {@code brushing} model overrides registered in the client class.
 */
public class ApplicatorBrushItem extends Item {

    /** Matches vanilla brushing: one scrape every ten ticks, offset so it starts early. */
    private static final int SCRAPE_INTERVAL = 10;
    private static final int SCRAPE_OFFSET = 5;
    private static final int PARTICLES_PER_SCRAPE = 4;
    private static final int PARTICLES_ON_FINISH = 12;

    public ApplicatorBrushItem(Properties properties) {
        // It carries contents, so two brushes are never interchangeable.
        super(properties.stacksTo(1));
    }

    public static ItemStack contentsOf(ItemStack brush) {
        BrushContents contents = brush.get(ModDataComponents.BRUSH_CONTENTS.get());
        return contents == null ? ItemStack.EMPTY : contents.substance();
    }

    private static void setContents(ItemStack brush, ItemStack substance) {
        if (substance.isEmpty())
            brush.remove(ModDataComponents.BRUSH_CONTENTS.get());
        else
            brush.set(ModDataComponents.BRUSH_CONTENTS.get(), new BrushContents(substance));
    }

    // --- loading and emptying ------------------------------------------------

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack brush = player.getItemInHand(hand);
        ItemStack loaded = contentsOf(brush);

        if (player.isShiftKeyDown()) {
            if (loaded.isEmpty())
                return new InteractionResultHolder<>(InteractionResult.PASS, brush);
            if (!level.isClientSide) {
                player.getInventory().placeItemBackInInventory(loaded);
                setContents(brush, ItemStack.EMPTY);
            }
            player.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 1.0F);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, brush);
        }

        InteractionHand other = hand == InteractionHand.MAIN_HAND
                ? InteractionHand.OFF_HAND
                : InteractionHand.MAIN_HAND;
        ItemStack offered = player.getItemInHand(other);
        if (offered.isEmpty())
            return new InteractionResultHolder<>(InteractionResult.PASS, brush);

        // A loaded brush only takes more of what it already carries; an empty one takes
        // anything a recipe knows what to do with.
        if (!loaded.isEmpty() && !ItemStack.isSameItemSameComponents(loaded, offered))
            return new InteractionResultHolder<>(InteractionResult.PASS, brush);
        if (loaded.isEmpty() && !ApplyingRecipe.isSubstance(level, offered))
            return new InteractionResultHolder<>(InteractionResult.PASS, brush);

        int room = offered.getMaxStackSize() - loaded.getCount();
        if (room <= 0)
            return new InteractionResultHolder<>(InteractionResult.PASS, brush);

        int taken = Math.min(room, offered.getCount());
        if (!level.isClientSide) {
            setContents(brush, offered.copyWithCount(loaded.getCount() + taken));
            offered.shrink(taken);
        }
        player.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 1.0F);
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, brush);
    }

    // --- applying ------------------------------------------------------------

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        // Crouching always means empty the brush, even while looking at a block it would
        // otherwise treat - otherwise there is nowhere to stand where emptying is certain.
        if (player == null || player.isShiftKeyDown())
            return InteractionResult.PASS;

        ItemStack brush = context.getItemInHand();
        Level level = context.getLevel();
        Optional<RecipeHolder<ApplyingRecipe>> recipe = ApplyingRecipe.find(level, contentsOf(brush),
                level.getBlockState(context.getClickedPos()));
        // Falls through to use(), so a right click on a block can still load the brush.
        if (recipe.isEmpty())
            return InteractionResult.PASS;

        // The duration has to live on the stack rather than be looked up inside
        // getUseDuration: it is asked once when the use begins, and the answer has to
        // come out the same on both sides for the whole stroke.
        brush.set(ModDataComponents.APPLYING_TIME.get(), recipe.get().value().processingTime());
        player.startUsingItem(context.getHand());
        return InteractionResult.CONSUME;
    }

    @Override
    public int getUseDuration(ItemStack brush, LivingEntity entity) {
        Integer time = brush.get(ModDataComponents.APPLYING_TIME.get());
        return time == null ? 0 : time;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack brush) {
        return UseAnim.BRUSH;
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack brush, int remaining) {
        if (remaining <= 0 || !(entity instanceof Player player)) {
            entity.releaseUsingItem();
            return;
        }

        BlockHitResult hit = lookingAt(player);
        if (hit == null) {
            entity.releaseUsingItem();
            return;
        }

        BlockPos pos = hit.getBlockPos();
        BlockState state = level.getBlockState(pos);
        ItemStack substance = contentsOf(brush);
        if (ApplyingRecipe.find(level, substance, state).isEmpty()) {
            entity.releaseUsingItem();
            return;
        }

        int elapsed = getUseDuration(brush, entity) - remaining + 1;
        if (elapsed % SCRAPE_INTERVAL != SCRAPE_OFFSET)
            return;

        level.playSound(player, pos, SoundEvents.BRUSH_GENERIC, SoundSource.BLOCKS);
        if (level.isClientSide)
            spawnParticles(level, hit, state, substance, PARTICLES_PER_SCRAPE);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack brush, Level level, LivingEntity entity) {
        brush.remove(ModDataComponents.APPLYING_TIME.get());
        if (!(entity instanceof Player player))
            return brush;

        BlockHitResult hit = lookingAt(player);
        if (hit == null)
            return brush;

        BlockPos pos = hit.getBlockPos();
        BlockState state = level.getBlockState(pos);
        ItemStack substance = contentsOf(brush);
        Optional<RecipeHolder<ApplyingRecipe>> recipe = ApplyingRecipe.find(level, substance, state);
        if (recipe.isEmpty())
            return brush;

        if (level.isClientSide) {
            spawnParticles(level, hit, state, substance, PARTICLES_ON_FINISH);
            return brush;
        }

        BlockState applied = recipe.get().value().resultFor(state);
        level.setBlock(pos, applied, Block.UPDATE_ALL);
        level.playSound(null, pos, applied.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);

        // Only the load is spent. The brush never wears out - it is the infrastructure,
        // and what runs out is whatever you dipped it in.
        ItemStack left = substance.copy();
        left.shrink(1);
        setContents(brush, left);
        return brush;
    }

    /** Let go early and nothing is spent; the load stays where it is. */
    @Override
    public void releaseUsing(ItemStack brush, Level level, LivingEntity entity, int timeLeft) {
        brush.remove(ModDataComponents.APPLYING_TIME.get());
    }

    @Override
    public void appendHoverText(ItemStack brush, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        ItemStack loaded = contentsOf(brush);
        if (loaded.isEmpty())
            return;
        tooltip.add(Component.literal(loaded.getCount() + " × ")
                .append(loaded.getHoverName())
                .withStyle(ChatFormatting.GRAY));
    }

    /**
     * The block under the crosshair, or null if there is none. Read fresh every tick
     * rather than remembered from the click, so looking away stops the work.
     */
    private static BlockHitResult lookingAt(Player player) {
        HitResult hit = ProjectileUtil.getHitResultOnViewVector(player,
                entity -> !entity.isSpectator() && entity.isPickable(), player.blockInteractionRange());
        return hit.getType() == HitResult.Type.BLOCK && hit instanceof BlockHitResult block ? block : null;
    }

    /** Dust off the block being worked, mixed with flecks of what is being worked in. */
    private static void spawnParticles(Level level, BlockHitResult hit, BlockState state, ItemStack substance,
            int count) {
        RandomSource random = level.random;
        Vec3 at = hit.getLocation();
        for (int i = 0; i < count; i++) {
            double dx = (random.nextDouble() - 0.5) / 4;
            double dy = (random.nextDouble() - 0.5) / 4;
            double dz = (random.nextDouble() - 0.5) / 4;
            level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), at.x, at.y, at.z, dx, dy, dz);
            if (!substance.isEmpty())
                level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, substance), at.x, at.y, at.z,
                        dx, dy, dz);
        }
    }
}
