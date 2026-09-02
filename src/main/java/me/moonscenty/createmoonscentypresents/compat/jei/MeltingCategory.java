package me.moonscenty.createmoonscentypresents.compat.jei;

import java.util.HashMap;
import java.util.Map;

import com.simibubi.create.content.processing.recipe.HeatCondition;

import me.moonscenty.createmoonscentypresents.content.foundry.FoundryRecipe;
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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;

/** What a foundry basin melts, and what fire it takes to do it. */
public class MeltingCategory implements IRecipeCategory<RecipeHolder<FoundryRecipe>> {

    public static final RecipeType<RecipeHolder<FoundryRecipe>> TYPE = RecipeType.createRecipeHolderType(
            ResourceLocation.fromNamespaceAndPath("createmoonscentypresents", "melting"));

    private static final int WIDTH = 116;
    private static final int HEIGHT = 50;

    private static final int SLOT_Y = 6;
    private static final int INPUT_X = 8;
    private static final int OUTPUT_X = WIDTH - 8 - 18;
    private static final int GAP_CENTER = (INPUT_X + 18 + OUTPUT_X) / 2;

    private static final int ARROW_WIDTH = 24;
    private static final int ARROW_X = GAP_CENTER - ARROW_WIDTH / 2;
    private static final int ARROW_Y = SLOT_Y + 1;

    private static final int TIME_Y = SLOT_Y + 24;
    private static final int HEAT_Y = TIME_Y + 10;
    private static final int TEXT_COLOUR = 0xFF808080;

    /** Enough for the largest pour a mould could ask for; the bar is read against it. */
    private static final int TANK_CAPACITY = 1000;

    private final IGuiHelper guiHelper;
    private final IDrawable icon;
    private final Map<Integer, IDrawableAnimated> arrows = new HashMap<>();

    public MeltingCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.FOUNDRY_BASIN.get()));
    }

    @Override
    public RecipeType<RecipeHolder<FoundryRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(ModRecipes.MELTING_CATEGORY_KEY);
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<FoundryRecipe> holder,
            IFocusGroup focuses) {
        FoundryRecipe recipe = holder.value();

        int x = INPUT_X;
        for (Ingredient ingredient : recipe.getIngredients()) {
            builder.addSlot(RecipeIngredientRole.INPUT, x, SLOT_Y).addIngredients(ingredient);
            x += 20;
        }

        for (FluidStack result : recipe.getFluidResults())
            builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, SLOT_Y)
                    .setFluidRenderer(TANK_CAPACITY, false, 16, 16)
                    .addFluidStack(result.getFluid(), result.getAmount(), result.getComponentsPatch());
    }

    @Override
    public void draw(RecipeHolder<FoundryRecipe> holder, IRecipeSlotsView slots, GuiGraphics graphics,
            double mouseX, double mouseY) {
        FoundryRecipe recipe = holder.value();
        int ticks = recipe.getProcessingDuration();
        arrows.computeIfAbsent(ticks, guiHelper::createAnimatedRecipeArrow).draw(graphics, ARROW_X, ARROW_Y);

        Font font = Minecraft.getInstance().font;
        Component time = Component.translatable(ModRecipes.TIME_KEY, JeiFormat.seconds(ticks));
        graphics.drawString(font, time, GAP_CENTER - font.width(time) / 2, TIME_Y, TEXT_COLOUR, false);

        // A foundry always needs some fire, so even Create's "no requirement" is worth
        // saying out loud here - it means a campfire will do, not that nothing will.
        HeatCondition heat = recipe.getRequiredHeat();
        Component needs = Component.translatable(ModRecipes.NEEDS_HEAT_KEY,
                Component.translatable(heat == HeatCondition.NONE
                        ? ModRecipes.ANY_FIRE_KEY
                        : heat.getTranslationKey()))
                .withStyle(ChatFormatting.GRAY);
        graphics.drawString(font, needs, GAP_CENTER - font.width(needs) / 2, HEAT_Y, TEXT_COLOUR, false);
    }
}
