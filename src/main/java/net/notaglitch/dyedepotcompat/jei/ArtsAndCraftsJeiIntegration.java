package net.notaglitch.dyedepotcompat.jei;

import com.kekecreations.arts_and_crafts.common.item.palette.PaintbrushPalette;
import com.kekecreations.arts_and_crafts.core.registry.ACBlocks;
import com.kekecreations.arts_and_crafts.core.registry.ACItems;
import com.kekecreations.arts_and_crafts.core.registry.ACRegistries;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ArtsAndCraftsJeiIntegration {

    public static void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new PaintingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    public static void registerRecipes(IRecipeRegistration registration) {
        registerDecoratedPotRecipes(registration);
        registerPaintingRecipes(registration);
    }

    private static void registerDecoratedPotRecipes(IRecipeRegistration registration) {
        List<RecipeHolder<CraftingRecipe>> recipes = new ArrayList<>();

        for (DyeColor dyeColor : DyeColor.values()) {
            Block dyedPotBlock = ACBlocks.getDyedDecoratedPot(dyeColor.getId());
            DyeItem dyeItem = DyeItem.byColor(dyeColor);
            if (dyedPotBlock != null && dyedPotBlock.asItem() != Items.AIR && dyeItem != null) {
                // Recipe: Plain Decorated Pot + Dye -> Dyed Decorated Pot
                NonNullList<Ingredient> dyeIngredients = NonNullList.of(
                        Ingredient.EMPTY,
                        Ingredient.of(Items.DECORATED_POT),
                        Ingredient.of(dyeItem)
                );
                ShapelessRecipe dyeRecipe = new ShapelessRecipe(
                        "arts_and_crafts:dyed_decorated_pot",
                        CraftingBookCategory.MISC,
                        new ItemStack(dyedPotBlock),
                        dyeIngredients
                );
                ResourceLocation dyeId = ResourceLocation.fromNamespaceAndPath(
                        "dyedepotcompat", "jei_dye_" + dyeColor.getName() + "_decorated_pot");
                recipes.add(new RecipeHolder<>(dyeId, dyeRecipe));

                // Recipe: Dyed Decorated Pot + Bleachdew -> Plain Decorated Pot
                Item bleachdew = ACItems.BLEACHDEW.get();
                if (bleachdew != null) {
                    NonNullList<Ingredient> bleachIngredients = NonNullList.of(
                            Ingredient.EMPTY,
                            Ingredient.of(dyedPotBlock),
                            Ingredient.of(bleachdew)
                    );
                    ShapelessRecipe bleachRecipe = new ShapelessRecipe(
                            "arts_and_crafts:bleach_decorated_pot",
                            CraftingBookCategory.MISC,
                            new ItemStack(Items.DECORATED_POT),
                            bleachIngredients
                    );
                    ResourceLocation bleachId = ResourceLocation.fromNamespaceAndPath(
                            "dyedepotcompat", "jei_bleach_" + dyeColor.getName() + "_decorated_pot");
                    recipes.add(new RecipeHolder<>(bleachId, bleachRecipe));
                }
            }
        }

        if (!recipes.isEmpty()) {
            registration.addRecipes(RecipeTypes.CRAFTING, recipes);
        }
    }

    private static void registerPaintingRecipes(IRecipeRegistration registration) {
        RegistryAccess access = null;
        if (Minecraft.getInstance().getConnection() != null) {
            access = Minecraft.getInstance().getConnection().registryAccess();
        } else if (Minecraft.getInstance().level != null) {
            access = Minecraft.getInstance().level.registryAccess();
        }

        if (access == null) return;

        Registry<PaintbrushPalette> paletteRegistry = access.registry(ACRegistries.PAINTBRUSH_PALETTE).orElse(null);
        if (paletteRegistry == null) return;

        List<PaintingRecipe> recipes = new ArrayList<>();
        for (PaintbrushPalette palette : paletteRegistry) {
            List<ItemStack> inputs = new ArrayList<>();
            for (Holder<Block> blockHolder : palette.blocks()) {
                if (blockHolder.isBound() && blockHolder.value().asItem() != Items.AIR) {
                    inputs.add(new ItemStack(blockHolder.value()));
                }
            }
            if (inputs.isEmpty()) continue;

            for (Map.Entry<Holder<Item>, Holder<Block>> entry : palette.mappings().entrySet()) {
                if (entry.getKey().isBound() && entry.getValue().isBound()) {
                    ItemStack brush = new ItemStack(entry.getKey().value());
                    ItemStack output = new ItemStack(entry.getValue().value());
                    if (brush.getItem() != Items.AIR && output.getItem() != Items.AIR) {
                        recipes.add(new PaintingRecipe(inputs, brush, output));
                    }
                }
            }
        }

        if (!recipes.isEmpty()) {
            registration.addRecipes(PaintingRecipeCategory.TYPE, recipes);
        }
    }

    public static void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        for (DyeColor dyeColor : DyeColor.values()) {
            Item brush = ACItems.getPaintBrush(dyeColor.getId());
            if (brush != null && brush != Items.AIR) {
                registration.addRecipeCatalysts(PaintingRecipeCategory.TYPE, brush);
            }
        }
        Item bleachdewBrush = ACItems.BLEACHDEW_PAINTBRUSH.get();
        if (bleachdewBrush != null && bleachdewBrush != Items.AIR) {
            registration.addRecipeCatalysts(PaintingRecipeCategory.TYPE, bleachdewBrush);
        }
    }
}
