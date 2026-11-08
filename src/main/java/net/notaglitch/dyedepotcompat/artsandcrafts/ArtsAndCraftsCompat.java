package net.notaglitch.dyedepotcompat.artsandcrafts;

import com.kekecreations.arts_and_crafts.common.block.DyedDecoratedPotBlock;
import com.kekecreations.arts_and_crafts.common.item.DyedDecoratedPotBlockItem;
import com.kekecreations.arts_and_crafts.core.registry.ACBlocks;
import com.kekecreations.arts_and_crafts.core.registry.ACItems;
import com.kekecreations.arts_and_crafts.core.registry.ACTabs;
import com.mojang.logging.LogUtils;
import com.ninni.dye_depot.registry.DDDyes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
 * <p>Arts & Crafts already iterates over {@code DyeColor.values()} to register dyed plaster,
 * mud bricks, terracotta shingles, soapstone, chalk, flower pots, paintbrushes, etc.,
 * but explicitly skips decorated pots for dye IDs > 15 (which excludes Dye Depot's colors).
 *
 * <p>Here we register the missing decorated pots under the {@code arts_and_crafts} namespace
 * and populate Arts & Crafts' internal maps so that all tools, paintbrush palettes, and recipes
 * seamlessly recognize them.
 */
public class ArtsAndCraftsCompat {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Map<DyeColor, Supplier<Block>> DECORATED_POTS = new HashMap<>();
    public static final Map<DyeColor, Supplier<Item>> DECORATED_POT_ITEMS = new HashMap<>();

    public static void init(IEventBus modEventBus) {
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

    private static void onBuildCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.FUNCTIONAL_BLOCKS)
                || (ACTabs.EXAMPLE_TAB != null && event.getTab().equals(ACTabs.EXAMPLE_TAB.get()))) {
            for (Supplier<Item> itemSupplier : DECORATED_POT_ITEMS.values()) {
                Item item = itemSupplier.get();
                if (item != null) {
                    event.accept(new ItemStack(item));
                }
            }
        }
    }
}
