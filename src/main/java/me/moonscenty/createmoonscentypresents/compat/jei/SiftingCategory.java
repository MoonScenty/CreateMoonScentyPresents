package me.moonscenty.createmoonscentypresents.compat.jei;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.simibubi.create.content.processing.recipe.ProcessingOutput;

import me.moonscenty.createmoonscentypresents.content.sifting.SiftingRecipe;
import me.moonscenty.createmoonscentypresents.registry.ModBlocks;
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

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * What a sifter separates, and whether it has to be standing in water to do it.
 *
 * <p>The chances go under the outputs rather than in the tooltip: half the point of a
 * sifter is that it mostly gives nothing, and a page that hides that reads as a promise
 * it does not keep.
 */
public class SiftingCategory implements IRecipeCategory<RecipeHolder<SiftingRecipe>> {

    public static final RecipeType<RecipeHolder<SiftingRecipe>> TYPE = RecipeType.createRecipeHolderType(
            ResourceLocation.fromNamespaceAndPath("createmoonscentypresents", "sifting"));

    private static final int WIDTH = 140;
    private static final int HEIGHT = 62;

    private static final int SLOT_Y = 6;
    private static final int INPUT_X = 8;
    private static final int OUTPUT_X = 74;
    private static final int OUTPUT_SPACING = 20;

    private static final int ARROW_WIDTH = 24;
    private static final int ARROW_X = 40;
    private static final int ARROW_Y = SLOT_Y + 1;

    private static final int CHANCE_Y = SLOT_Y + 20;
    private static final int TIME_Y = HEIGHT - 20;
    private static final int WATER_Y = HEIGHT - 10;
    private static final int TEXT_COLOUR = 0xFF808080;

    private final IGuiHelper guiHelper;
    private final IDrawable icon;
    private final Map<Integer, IDrawableAnimated> arrows = new HashMap<>();

    public SiftingCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.PRIMITIVE_SIFTER.get()));
    }

    @Override
    public RecipeType<RecipeHolder<SiftingRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(ModRecipes.SIFTING_CATEGORY_KEY);
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<SiftingRecipe> holder,
            IFocusGroup focuses) {
        SiftingRecipe recipe = holder.value();
        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X, SLOT_Y)
                .addIngredients(recipe.getIngredients().get(0));

        List<ProcessingOutput> results = recipe.getRollableResults();
        for (int i = 0; i < results.size(); i++)
            builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X + i * OUTPUT_SPACING, SLOT_Y)
                    .addItemStack(results.get(i).getStack());
    }

    @Override
    public void draw(RecipeHolder<SiftingRecipe> holder, IRecipeSlotsView slots, GuiGraphics graphics,
            double mouseX, double mouseY) {
        SiftingRecipe recipe = holder.value();
        int ticks = recipe.getProcessingDuration();
        arrows.computeIfAbsent(ticks, guiHelper::createAnimatedRecipeArrow).draw(graphics, ARROW_X, ARROW_Y);

        Font font = Minecraft.getInstance().font;

        List<ProcessingOutput> results = recipe.getRollableResults();
        for (int i = 0; i < results.size(); i++) {
            float chance = results.get(i).getChance();
            if (chance >= 1)
                continue;
            String percent = Math.round(chance * 100) + "%";
            int centre = OUTPUT_X + i * OUTPUT_SPACING + 8;
            graphics.drawString(font, percent, centre - font.width(percent) / 2, CHANCE_Y, TEXT_COLOUR,
                    false);
        }

        Component time = Component.translatable(ModRecipes.TIME_KEY, JeiFormat.seconds(ticks));
        graphics.drawString(font, time, WIDTH / 2 - font.width(time) / 2, TIME_Y, TEXT_COLOUR, false);

        recipe.waterlogged().ifPresent(wet -> {
            Component water = Component.translatable(
                    wet ? ModRecipes.SIFTER_WET_KEY : ModRecipes.SIFTER_DRY_KEY)
                    .withStyle(ChatFormatting.GRAY);
            graphics.drawString(font, water, WIDTH / 2 - font.width(water) / 2, WATER_Y, TEXT_COLOUR,
                    false);
        });
    }
}
