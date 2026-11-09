package net.notaglitch.dyedepotcompat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;

@JeiPlugin
public class DyeDepotCompatJeiPlugin implements IModPlugin {

    public static final ResourceLocation UID =
            ResourceLocation.fromNamespaceAndPath("dyedepotcompat", "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        if (ModList.get().isLoaded("arts_and_crafts")) {
            ArtsAndCraftsJeiIntegration.registerCategories(registration);
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (ModList.get().isLoaded("arts_and_crafts")) {
            ArtsAndCraftsJeiIntegration.registerRecipes(registration);
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        if (ModList.get().isLoaded("arts_and_crafts")) {
            ArtsAndCraftsJeiIntegration.registerRecipeCatalysts(registration);
        }
    }
}
