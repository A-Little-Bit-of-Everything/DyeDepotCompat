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
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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
        List<PaintingRecipe> recipes = new ArrayList<>();

        // 1. Static Arts & Crafts palette recipes (guaranteed to load even if client is not in a world)
        addPaletteRecipes(recipes, ACBlocks.SOAPSTONE.get(), ACBlocks::getDyedSoapstone);
        addPaletteRecipes(recipes, ACBlocks.SOAPSTONE_STAIRS.get(), ACBlocks::getDyedSoapstoneStairs);
        addPaletteRecipes(recipes, ACBlocks.SOAPSTONE_SLAB.get(), ACBlocks::getDyedSoapstoneSlab);
        addPaletteRecipes(recipes, ACBlocks.SOAPSTONE_WALL.get(), ACBlocks::getDyedSoapstoneWall);
        addPaletteRecipes(recipes, ACBlocks.POLISHED_SOAPSTONE.get(), ACBlocks::getDyedPolishedSoapstone);
        addPaletteRecipes(recipes, ACBlocks.POLISHED_SOAPSTONE_STAIRS.get(), ACBlocks::getDyedPolishedSoapstoneStairs);
        addPaletteRecipes(recipes, ACBlocks.POLISHED_SOAPSTONE_SLAB.get(), ACBlocks::getDyedPolishedSoapstoneSlab);
        addPaletteRecipes(recipes, ACBlocks.POLISHED_SOAPSTONE_WALL.get(), ACBlocks::getDyedPolishedSoapstoneWall);
        addPaletteRecipes(recipes, ACBlocks.SOAPSTONE_BRICKS.get(), ACBlocks::getDyedSoapstoneBricks);
        addPaletteRecipes(recipes, ACBlocks.SOAPSTONE_BRICK_STAIRS.get(), ACBlocks::getDyedSoapstoneBrickStairs);
        addPaletteRecipes(recipes, ACBlocks.SOAPSTONE_BRICK_SLAB.get(), ACBlocks::getDyedSoapstoneBrickSlab);
        addPaletteRecipes(recipes, ACBlocks.SOAPSTONE_BRICK_WALL.get(), ACBlocks::getDyedSoapstoneBrickWall);

        // Chalk
        Block plainChalk = ACBlocks.CHALK.get(-1) != null ? ACBlocks.CHALK.get(-1).get() : null;
        addPaletteRecipes(recipes, plainChalk, ACBlocks::getChalk);

        // Plaster
        addPaletteRecipes(recipes, ACBlocks.PLASTER.get(), ACBlocks::getDyedPlaster);

        // Mud Bricks
        addPaletteRecipes(recipes, Blocks.MUD_BRICKS, ACBlocks::getDyedMudBricks);
        addPaletteRecipes(recipes, Blocks.MUD_BRICK_STAIRS, ACBlocks::getDyedMudBrickStairs);
        addPaletteRecipes(recipes, Blocks.MUD_BRICK_SLAB, ACBlocks::getDyedMudBrickSlab);
        addPaletteRecipes(recipes, Blocks.MUD_BRICK_WALL, ACBlocks::getDyedMudBrickWall);

        // Terracotta Shingles
        addPaletteRecipes(recipes, ACBlocks.TERRACOTTA_SHINGLES.get(), ACBlocks::getDyedTerracottaShingles);
        addPaletteRecipes(recipes, ACBlocks.TERRACOTTA_SHINGLE_STAIRS.get(), ACBlocks::getDyedTerracottaShingleStairs);
        addPaletteRecipes(recipes, ACBlocks.TERRACOTTA_SHINGLE_SLAB.get(), ACBlocks::getDyedTerracottaShingleSlab);
        addPaletteRecipes(recipes, ACBlocks.TERRACOTTA_SHINGLE_WALL.get(), ACBlocks::getDyedTerracottaShingleWall);

        // Pots
        addPaletteRecipes(recipes, Blocks.FLOWER_POT, ACBlocks::getDyedFlowerPot);
        addPaletteRecipes(recipes, Blocks.DECORATED_POT, ACBlocks::getDyedDecoratedPot);

        // 2. Datapack dynamic palettes (if registry is already present)
        RegistryAccess access = null;
        if (Minecraft.getInstance().getConnection() != null) {
            access = Minecraft.getInstance().getConnection().registryAccess();
        } else if (Minecraft.getInstance().level != null) {
            access = Minecraft.getInstance().level.registryAccess();
        }

        if (access != null) {
            Registry<PaintbrushPalette> paletteRegistry = access.registry(ACRegistries.PAINTBRUSH_PALETTE).orElse(null);
            if (paletteRegistry != null) {
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
            }
        }

        if (!recipes.isEmpty()) {
            registration.addRecipes(PaintingRecipeCategory.TYPE, recipes);
        }
    }

    private static void addPaletteRecipes(
            List<PaintingRecipe> recipes,
            Block plainBlock,
            Function<Integer, Block> dyedBlockGetter) {
        List<ItemStack> allVariants = new ArrayList<>();
        if (plainBlock != null && plainBlock.asItem() != Items.AIR) {
            allVariants.add(new ItemStack(plainBlock));
        }

        for (DyeColor dyeColor : DyeColor.values()) {
            try {
                Block dyed = dyedBlockGetter.apply(dyeColor.getId());
                if (dyed != null && dyed.asItem() != Items.AIR) {
                    allVariants.add(new ItemStack(dyed));
                }
            } catch (Throwable ignored) {
            }
        }

        if (allVariants.isEmpty()) return;

        // Recipes for each dye: allVariants + [Dye] Paintbrush -> [Dye] Block
        for (DyeColor dyeColor : DyeColor.values()) {
            try {
                Block dyed = dyedBlockGetter.apply(dyeColor.getId());
                Item brush = ACItems.getPaintBrush(dyeColor.getId());
                if (dyed != null && dyed.asItem() != Items.AIR && brush != null && brush != Items.AIR) {
                    recipes.add(new PaintingRecipe(allVariants, new ItemStack(brush), new ItemStack(dyed)));
                }
            } catch (Throwable ignored) {
            }
        }

        // Recipe for Bleachdew: allVariants + Bleachdew Paintbrush -> Plain Block
        if (plainBlock != null && plainBlock.asItem() != Items.AIR) {
            Item bleachBrush = ACItems.BLEACHDEW_PAINTBRUSH.get();
            if (bleachBrush != null && bleachBrush != Items.AIR) {
                recipes.add(new PaintingRecipe(allVariants, new ItemStack(bleachBrush), new ItemStack(plainBlock)));
            }
        }
    }

    public static void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        for (DyeColor dyeColor : DyeColor.values()) {
            try {
                Item brush = ACItems.getPaintBrush(dyeColor.getId());
                if (brush != null && brush != Items.AIR) {
                    registration.addRecipeCatalysts(PaintingRecipeCategory.TYPE, brush);
                }
            } catch (Throwable ignored) {
            }
        }
        Item bleachdewBrush = ACItems.BLEACHDEW_PAINTBRUSH.get();
        if (bleachdewBrush != null && bleachdewBrush != Items.AIR) {
            registration.addRecipeCatalysts(PaintingRecipeCategory.TYPE, bleachdewBrush);
        }
    }
}
