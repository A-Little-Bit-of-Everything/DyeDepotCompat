package net.notaglitch.dyedepotcompat.jei;

import com.kekecreations.arts_and_crafts.core.registry.ACItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public class PaintingRecipeCategory implements IRecipeCategory<PaintingRecipe> {

    public static final RecipeType<PaintingRecipe> TYPE =
            RecipeType.create("arts_and_crafts", "painting", PaintingRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableStatic slot;
    private final IDrawableStatic arrow;
    private final IDrawableStatic plus;
    private final Component title;

    public PaintingRecipeCategory(IGuiHelper helper) {
        this.background = helper.createBlankDrawable(110, 26);
        this.icon = helper.createDrawableItemStack(new ItemStack(ACItems.getPaintBrush(DyeColor.RED.getId())));
        this.slot = helper.getSlotDrawable();
        this.arrow = helper.getRecipeArrow();
        this.plus = helper.getRecipePlusSign();
        this.title = Component.translatable("gui.dyedepotcompat.category.painting");
    }

    @Override
    public RecipeType<PaintingRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PaintingRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(2, 4).addItemStacks(recipe.inputs()).setBackground(slot, -1, -1);
        builder.addInputSlot(36, 4).addItemStack(recipe.paintbrush()).setBackground(slot, -1, -1);
        builder.addOutputSlot(86, 4).addItemStack(recipe.output()).setBackground(slot, -1, -1);
    }

    @Override
    public void draw(PaintingRecipe recipe, IRecipeSlotsView slotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        plus.draw(guiGraphics, 23, 8);
        arrow.draw(guiGraphics, 58, 5);
    }
}
