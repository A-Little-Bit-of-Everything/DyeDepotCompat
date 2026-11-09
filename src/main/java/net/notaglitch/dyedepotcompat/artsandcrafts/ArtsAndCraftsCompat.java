package net.notaglitch.dyedepotcompat.artsandcrafts;

import com.kekecreations.arts_and_crafts.common.block.DyedDecoratedPotBlock;
import com.kekecreations.arts_and_crafts.common.item.DyedDecoratedPotBlockItem;
import com.kekecreations.arts_and_crafts.core.registry.ACBlocks;
import com.kekecreations.arts_and_crafts.core.registry.ACItems;
import com.kekecreations.arts_and_crafts.core.registry.ACTabs;
import com.kekecreations.jinxedlib.core.util.JinxedCreativeCategoryUtils;
import com.mojang.logging.LogUtils;
import com.ninni.dye_depot.registry.DDDyes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Arts & Crafts compatibility module.
 *
 * <p>Registers missing decorated pots, populates creative mode tabs (ensuring all Dye Depot
 * variants appear in the creative search and JEI), and seeds JinxedLib's color order.
 */
public class ArtsAndCraftsCompat {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Map<DyeColor, Supplier<Block>> DECORATED_POTS = new HashMap<>();
    public static final Map<DyeColor, Supplier<Item>> DECORATED_POT_ITEMS = new HashMap<>();

    public static void init(IEventBus modEventBus) {
        try {
            for (DDDyes ddDye : DDDyes.values()) {
                DyeColor dyeColor = ddDye.get();
                if (!JinxedCreativeCategoryUtils.colourOrder.contains(dyeColor)) {
                    JinxedCreativeCategoryUtils.colourOrder.add(dyeColor);
                }
                if (!JinxedCreativeCategoryUtils.colourOrderBackToFront.contains(dyeColor)) {
                    JinxedCreativeCategoryUtils.colourOrderBackToFront.add(dyeColor);
                }
            }
        } catch (Throwable t) {
            LOGGER.warn("[DyeDepotCompat] Could not populate JinxedCreativeCategoryUtils: {}", t.getMessage());
        }

        modEventBus.addListener(ArtsAndCraftsCompat::onRegister);
        modEventBus.addListener(ArtsAndCraftsCompat::onBuildCreativeTab);
    }

    private static void onRegister(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.BLOCK)) {
            for (DDDyes ddDye : DDDyes.values()) {
                DyeColor dyeColor = ddDye.get();
                String name = dyeColor.getName() + "_decorated_pot";
                ResourceLocation id = ResourceLocation.fromNamespaceAndPath("arts_and_crafts", name);

                BlockBehaviour.Properties props = BlockBehaviour.Properties.ofFullCopy(Blocks.DECORATED_POT);
                DyedDecoratedPotBlock potBlock = new DyedDecoratedPotBlock(dyeColor, props);

                event.register(Registries.BLOCK, id, () -> potBlock);
                DECORATED_POTS.put(dyeColor, () -> potBlock);
                ACBlocks.DYED_DECORATED_POTS.put(dyeColor, () -> potBlock);
            }
            LOGGER.info("[DyeDepotCompat] Registered 16 Dye Depot decorated pots for Arts & Crafts.");
        } else if (event.getRegistryKey().equals(Registries.ITEM)) {
            for (DDDyes ddDye : DDDyes.values()) {
                DyeColor dyeColor = ddDye.get();
                String name = dyeColor.getName() + "_decorated_pot";
                ResourceLocation id = ResourceLocation.fromNamespaceAndPath("arts_and_crafts", name);

                Supplier<Block> blockSupplier = DECORATED_POTS.get(dyeColor);
                Block block = blockSupplier != null ? blockSupplier.get() : null;

                DyedDecoratedPotBlockItem potItem = new DyedDecoratedPotBlockItem(block, new Item.Properties());
                event.register(Registries.ITEM, id, () -> potItem);
                DECORATED_POT_ITEMS.put(dyeColor, () -> potItem);
                ACItems.DYED_DECORATED_POT_BLOCK_ITEMS.put(dyeColor, () -> potItem);
            }
        }
    }

    private static void accept(BuildCreativeModeTabContentsEvent event, ItemLike itemLike) {
        if (itemLike != null && itemLike.asItem() != null && itemLike.asItem() != net.minecraft.world.item.Items.AIR) {
            event.accept(new ItemStack(itemLike));
        }
    }

    private static void onBuildCreativeTab(BuildCreativeModeTabContentsEvent event) {
        boolean isExampleTab = ACTabs.EXAMPLE_TAB != null && event.getTab().equals(ACTabs.EXAMPLE_TAB.get());
        boolean isBuildingBlocks = event.getTabKey().equals(CreativeModeTabs.BUILDING_BLOCKS);
        boolean isColoredBlocks = event.getTabKey().equals(CreativeModeTabs.COLORED_BLOCKS);
        boolean isTools = event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES);
        boolean isFunctional = event.getTabKey().equals(CreativeModeTabs.FUNCTIONAL_BLOCKS);

        if (isExampleTab || isBuildingBlocks || isColoredBlocks || isTools || isFunctional) {
            for (DDDyes ddDye : DDDyes.values()) {
                int id = ddDye.get().getId();
                DyeColor dyeColor = ddDye.get();

                if (isExampleTab || isBuildingBlocks || isColoredBlocks) {
                    accept(event, ACBlocks.getDyedPlaster(id));
                    accept(event, ACBlocks.getDyedMudBricks(id));
                    accept(event, ACBlocks.getDyedMudBrickStairs(id));
                    accept(event, ACBlocks.getDyedMudBrickSlab(id));
                    accept(event, ACBlocks.getDyedMudBrickWall(id));
                    accept(event, ACBlocks.getDyedTerracottaShingles(id));
                    accept(event, ACBlocks.getDyedTerracottaShingleStairs(id));
                    accept(event, ACBlocks.getDyedTerracottaShingleSlab(id));
                    accept(event, ACBlocks.getDyedTerracottaShingleWall(id));
                    accept(event, ACBlocks.getDyedSoapstone(id));
                    accept(event, ACBlocks.getDyedSoapstoneStairs(id));
                    accept(event, ACBlocks.getDyedSoapstoneSlab(id));
                    accept(event, ACBlocks.getDyedSoapstoneWall(id));
                    accept(event, ACBlocks.getDyedPolishedSoapstone(id));
                    accept(event, ACBlocks.getDyedPolishedSoapstoneStairs(id));
                    accept(event, ACBlocks.getDyedPolishedSoapstoneSlab(id));
                    accept(event, ACBlocks.getDyedPolishedSoapstoneWall(id));
                    accept(event, ACBlocks.getDyedSoapstoneBricks(id));
                    accept(event, ACBlocks.getDyedSoapstoneBrickStairs(id));
                    accept(event, ACBlocks.getDyedSoapstoneBrickSlab(id));
                    accept(event, ACBlocks.getDyedSoapstoneBrickWall(id));
                }

                if (isExampleTab || isColoredBlocks) {
                    accept(event, ACBlocks.getChalk(id));
                    accept(event, ACBlocks.getChalkDust(id));
                }

                if (isExampleTab || isTools) {
                    accept(event, ACItems.getChalkStick(id));
                    accept(event, ACItems.getPaintBrush(id));
                }

                if (isExampleTab || isFunctional) {
                    accept(event, ACBlocks.getDyedFlowerPot(id));
                    Supplier<Item> potSupplier = DECORATED_POT_ITEMS.get(dyeColor);
                    if (potSupplier != null) {
                        accept(event, potSupplier.get());
                    }
                }
            }
        }
    }
}
