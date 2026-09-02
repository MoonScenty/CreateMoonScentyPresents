package me.moonscenty.createmoonscentypresents.compat.jei;

import me.moonscenty.createmoonscentypresents.CreateMoonScentyPresents;
import me.moonscenty.createmoonscentypresents.registry.ModItems;
import me.moonscenty.createmoonscentypresents.registry.ModRecipes;
import me.moonscenty.createmoonscentypresents.content.hammering.HammeringRecipe;

import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

/** The "hold the hammer against the log" recipes, shown as input -> arrow -> output. */
public class HammeringCategory implements IRecipeCategory<RecipeHolder<HammeringRecipe>> {

    public static final RecipeType<RecipeHolder<HammeringRecipe>> TYPE = RecipeType.createRecipeHolderType(
            ResourceLocation.fromNamespaceAndPath(CreateMoonScentyPresents.MODID, "hammering"));

    private static final int WIDTH = 116;
    private static final int HEIGHT = 54;

    // Slots are 18 wide. Placed symmetrically, so the gap between them - and every
    // thing drawn in it - is centred on the category.
    private static final int SLOT_Y = 32;
    private static final int INPUT_X = 8;
    private static final int OUTPUT_X = WIDTH - 8 - 18;
    private static final int GAP_CENTER = (INPUT_X + 18 + OUTPUT_X) / 2;

    private static final int ARROW_WIDTH = 24;
    private static final int ARROW_X = GAP_CENTER - ARROW_WIDTH / 2;
    private static final int ARROW_Y = SLOT_Y + 1;

    // The material being struck, drawn larger than an inventory item so the hammer reads
    // against it.
    private static final float STOCK_SCALE = 1.5F;
    private static final int STOCK_SIZE = Math.round(16 * STOCK_SCALE);
    private static final int STOCK_X = GAP_CENTER - STOCK_SIZE / 2;
    private static final int STOCK_Y = 4;

    // The hammer stays at item size and falls onto the stock from above.
    private static final int HAMMER_X = GAP_CENTER - 8;
    private static final int HAMMER_Y = STOCK_Y + (STOCK_SIZE - 16) / 2;
    /** How far the head lifts between blows. */
    private static final int SWING_LIFT = 7;
    private static final int BLOW_TICKS = 14;
    /** Vanilla renders GUI items at z 150; this lifts the hammer clear of the log. */
    private static final int HAMMER_DEPTH = 200;

    private final IDrawableStatic arrow;
    private final IDrawable icon;
    private final ItemStack hammer;
    private final ITickTimer blow;

    public HammeringCategory(IGuiHelper guiHelper) {
        this.arrow = guiHelper.getRecipeArrow();
        this.hammer = new ItemStack(ModItems.STONE_HAMMER.get());
        this.icon = guiHelper.createDrawableItemStack(hammer);
        // Counts 0..360 over one blow, so the value can be read straight as degrees.
        this.blow = guiHelper.createTickTimer(BLOW_TICKS, 360, false);
    }

    @Override
    public RecipeType<RecipeHolder<HammeringRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(ModRecipes.HAMMERING_CATEGORY_KEY);
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<HammeringRecipe> holder, IFocusGroup focuses) {
        HammeringRecipe recipe = holder.value();
        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X, SLOT_Y)
                .addIngredients(recipe.input());
        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, SLOT_Y)
                .addItemStack(recipe.result());
        // The hammer is drawn by hand in draw() so it can move; it is registered as a
        // catalyst instead of taking a slot here.
    }

    @Override
    public void draw(RecipeHolder<HammeringRecipe> holder, IRecipeSlotsView slots, GuiGraphics graphics,
            double mouseX, double mouseY) {
        arrow.draw(graphics, ARROW_X, ARROW_Y);

        PoseStack pose = graphics.pose();
        ItemStack[] inputs = holder.value().input().getItems();
        if (inputs.length > 0) {
            pose.pushPose();
            pose.translate(STOCK_X, STOCK_Y, 0);
            pose.scale(STOCK_SCALE, STOCK_SCALE, 1.0F);
            graphics.renderItem(inputs[0], 0, 0);
            pose.popPose();
        }

        // A hammer spends most of the cycle lifting and then drops fast, so the phase is
        // shaped rather than a plain sine: 0 at impact, 1 at the top of the lift.
        float cycle = blow.getValue() / 360.0F;
        float raised = cycle < 0.7F ? Mth.sin(cycle / 0.7F * Mth.HALF_PI)
                : 1.0F - (cycle - 0.7F) / 0.3F;

        pose.pushPose();
        // Both items would otherwise land on the same depth and fight for the pixels.
        pose.translate(0, 0, HAMMER_DEPTH);
        graphics.renderItem(hammer, HAMMER_X, HAMMER_Y - Math.round(raised * SWING_LIFT));
        pose.popPose();
    }
}
