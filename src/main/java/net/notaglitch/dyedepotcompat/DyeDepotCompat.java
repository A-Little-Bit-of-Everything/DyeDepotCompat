package net.notaglitch.dyedepotcompat;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.notaglitch.dyedepotcompat.artsandcrafts.ArtsAndCraftsCompat;
import net.notaglitch.dyedepotcompat.mdm.MusicDiscMakerCompat;
import org.slf4j.Logger;

@Mod(DyeDepotCompat.MODID)
public class DyeDepotCompat {

    public static final String MODID = "dyedepotcompat";
    public static final Logger LOGGER = LogUtils.getLogger();

    public DyeDepotCompat(IEventBus modEventBus, ModContainer modContainer) {
        if (ModList.get().isLoaded("music_disc_maker")) {
            LOGGER.info("[DyeDepotCompat] Music Disc Maker detected; enabling MDM integration.");
            MusicDiscMakerCompat.init(modEventBus);
        }

        if (ModList.get().isLoaded("arts_and_crafts")) {
            LOGGER.info("[DyeDepotCompat] Arts & Crafts detected; enabling Arts & Crafts integration.");
            ArtsAndCraftsCompat.init(modEventBus);
        }
    }
}
