package me.moonscenty.createmoonscentypresents.compat.jei;

import me.moonscenty.createmoonscentypresents.CreateMoonScentyPresents;
import me.moonscenty.createmoonscentypresents.registry.ModItems;
import me.moonscenty.createmoonscentypresents.registry.ModRecipes;
import me.moonscenty.createmoonscentypresents.content.sawing.SawingRecipe;

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

/** The "hold the saw against the log" recipes, shown as input -> arrow -> output. */
public class SawingCategory implements IRecipeCategory<RecipeHolder<SawingRecipe>> {

    public static final RecipeType<RecipeHolder<SawingRecipe>> TYPE = RecipeType.createRecipeHolderType(
            ResourceLocation.fromNamespaceAndPath(CreateMoonScentyPresents.MODID, "sawing"));

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

    // The log being cut, drawn larger than an inventory item so the saw reads against it.
    private static final float LOG_SCALE = 1.5F;
    private static final int LOG_SIZE = Math.round(16 * LOG_SCALE);
    private static final int LOG_X = GAP_CENTER - LOG_SIZE / 2;
    private static final int LOG_Y = 4;

    // The saw stays at item size and rides on the log's centre line.
    private static final int SAW_X = GAP_CENTER - 8;
    private static final int SAW_Y = LOG_Y + (LOG_SIZE - 16) / 2;
    /** Along the blade: up-right on the push, down-left on the pull. */
    private static final int STROKE_TRAVEL = 6;
    private static final int STROKE_TICKS = 20;
    /** Vanilla renders GUI items at z 150; this lifts the saw clear of the log. */
    private static final int SAW_DEPTH = 200;

    private final IDrawableStatic arrow;
    private final IDrawable icon;
    private final ItemStack saw;
    private final ITickTimer stroke;

    public SawingCategory(IGuiHelper guiHelper) {
        this.arrow = guiHelper.getRecipeArrow();
        this.saw = new ItemStack(ModItems.WOODEN_SAW.get());
        this.icon = guiHelper.createDrawableItemStack(saw);
        // Counts 0..360 over one stroke, so the value can be read straight as degrees.
        this.stroke = guiHelper.createTickTimer(STROKE_TICKS, 360, false);
    }

    @Override
    public RecipeType<RecipeHolder<SawingRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(ModRecipes.SAWING_CATEGORY_KEY);
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<SawingRecipe> holder, IFocusGroup focuses) {
        SawingRecipe recipe = holder.value();
        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X, SLOT_Y)
                .addIngredients(recipe.input());
        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, SLOT_Y)
                .addItemStack(recipe.result());
        // The saw is drawn by hand in draw() so it can move; it is registered as a
        // catalyst instead of taking a slot here.
    }

    @Override
    public void draw(RecipeHolder<SawingRecipe> holder, IRecipeSlotsView slots, GuiGraphics graphics,
            double mouseX, double mouseY) {
        arrow.draw(graphics, ARROW_X, ARROW_Y);

        PoseStack pose = graphics.pose();
        ItemStack[] inputs = holder.value().input().getItems();
        if (inputs.length > 0) {
            pose.pushPose();
            pose.translate(LOG_X, LOG_Y, 0);
            pose.scale(LOG_SCALE, LOG_SCALE, 1.0F);
            graphics.renderItem(inputs[0], 0, 0);
            pose.popPose();
        }

        float phase = Mth.sin(stroke.getValue() * Mth.DEG_TO_RAD);
        // x and y move together and opposite, which traces the blade's own diagonal.
        int offset = Math.round(phase * STROKE_TRAVEL);
        pose.pushPose();
        // Both items would otherwise land on the same depth and fight for the pixels.
        pose.translate(0, 0, SAW_DEPTH);
        graphics.renderItem(saw, SAW_X + offset, SAW_Y - offset);
        pose.popPose();
    }
}
