package me.moonscenty.createmoonscentypresents.compat.jei;

import java.util.HashMap;
import java.util.Map;

import me.moonscenty.createmoonscentypresents.CreateMoonScentyPresents;
import me.moonscenty.createmoonscentypresents.content.applying.ApplyingRecipe;
import me.moonscenty.createmoonscentypresents.registry.ModItems;
import me.moonscenty.createmoonscentypresents.registry.ModRecipes;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * The brush recipes: a substance and the block it is worked into, and how long the coat
 * takes to take. Three slots rather than two, because unlike the other hand methods the
 * thing being worked is a block standing in the world, not the item in the other hand.
 */
public class ApplyingCategory implements IRecipeCategory<RecipeHolder<ApplyingRecipe>> {

    public static final RecipeType<RecipeHolder<ApplyingRecipe>> TYPE = RecipeType.createRecipeHolderType(
            ResourceLocation.fromNamespaceAndPath(CreateMoonScentyPresents.MODID, "applying"));

    private static final int WIDTH = 140;
    private static final int HEIGHT = 40;

    private static final int SLOT_Y = 6;
    // Substance first, then the block it goes on: the order the player does it in.
    private static final int SUBSTANCE_X = 8;
    private static final int TARGET_X = SUBSTANCE_X + 20;
    private static final int OUTPUT_X = WIDTH - 8 - 18;
    // Centred on the gap between the pair and the result, not on the whole category.
    private static final int GAP_CENTER = (TARGET_X + 18 + OUTPUT_X) / 2;

    private static final int ARROW_WIDTH = 24;
    private static final int ARROW_X = GAP_CENTER - ARROW_WIDTH / 2;
    private static final int ARROW_Y = SLOT_Y + 1;

    private static final int TIME_Y = SLOT_Y + 24;
    private static final int TIME_COLOUR = 0xFF808080;

    private final IGuiHelper guiHelper;
    private final IDrawable icon;

    // One arrow per distinct duration: the animation runs at the recipe's own speed,
    // and recipes that take the same time can share.
    private final Map<Integer, IDrawableAnimated> arrows = new HashMap<>();

    public ApplyingCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModItems.APPLICATOR_BRUSH.get()));
    }

    @Override
    public RecipeType<RecipeHolder<ApplyingRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(ModRecipes.APPLYING_CATEGORY_KEY);
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<ApplyingRecipe> holder, IFocusGroup focuses) {
        ApplyingRecipe recipe = holder.value();
        builder.addSlot(RecipeIngredientRole.INPUT, SUBSTANCE_X, SLOT_Y)
                .addIngredients(recipe.substance());
        builder.addSlot(RecipeIngredientRole.INPUT, TARGET_X, SLOT_Y)
                .addIngredients(recipe.block());
        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, SLOT_Y)
                .addItemStack(new ItemStack(recipe.result()));
    }

    @Override
    public void draw(RecipeHolder<ApplyingRecipe> holder, IRecipeSlotsView slots, GuiGraphics graphics,
            double mouseX, double mouseY) {
        int ticks = holder.value().processingTime();
        arrows.computeIfAbsent(ticks, guiHelper::createAnimatedRecipeArrow)
                .draw(graphics, ARROW_X, ARROW_Y);

        Font font = Minecraft.getInstance().font;
        Component time = Component.translatable(ModRecipes.TIME_KEY, JeiFormat.seconds(ticks));
        graphics.drawString(font, time, GAP_CENTER - font.width(time) / 2, TIME_Y, TIME_COLOUR, false);
    }
}
